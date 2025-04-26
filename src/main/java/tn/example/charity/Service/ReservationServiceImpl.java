package tn.example.charity.Service;

import tn.example.charity.Entity.Logement;
import tn.example.charity.Entity.Reservation;
import tn.example.charity.Entity.User;
import tn.example.charity.Repository.LogementRepository;
import tn.example.charity.Repository.ReservationRepository;
import tn.example.charity.Repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationServiceImpl implements IReservationService {

    @Autowired
    private LogementRepository logementRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private EmailService emailService;

    @Override
    @Transactional
    public Reservation ajouterReservation(Long idLog, String username, String message) {
        Logement logement = logementRepository.findById(idLog)
                .orElseThrow(() -> new RuntimeException("Logement not found"));

        // Check if there are available places
        if (logement.getCapacite() <= 0) {
            throw new RuntimeException("No available places in this housing");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Reservation res = new Reservation();
        res.setDateDemande(LocalDate.now());
        res.setStatut("EN_ATTENTE");
        res.setLogement(logement);
        res.setDemandeur(user);
        res.setMessage(message);

        return reservationRepository.save(res);
    }

    @Override
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    @Override
    public List<Reservation> getReservationsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return reservationRepository.findByDemandeur(user);
    }

    @Override
    @Transactional
    public Reservation changerStatut(Long id, String statut, String adminEmail) {
        Reservation res = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        String previousStatus = res.getStatut();
        res.setStatut(statut); // "ACCEPTEE" ou "REFUSEE"

        // If the reservation is being accepted, reduce the capacity
        if (statut.equals("ACCEPTEE") && !previousStatus.equals("ACCEPTEE")) {
            Logement logement = res.getLogement();
            int currentCapacity = logement.getCapacite();

            if (currentCapacity > 0) {
                logement.setCapacite(currentCapacity - 1);

                // If capacity becomes 0, set availability to "non disponible"
                if (logement.getCapacite() == 0) {
                    logement.setDisponnibilite("non disponible");
                }

                logementRepository.save(logement);

                // Send email notification when reservation is accepted
                if (adminEmail != null && !adminEmail.isEmpty()) {
                    emailService.sendReservationNotification(res, adminEmail);
                }
            } else {
                throw new RuntimeException("No available places in this housing");
            }
        }
        // If the reservation was previously accepted but now is refused, increase capacity back
        else if (statut.equals("REFUSEE") && previousStatus.equals("ACCEPTEE")) {
            Logement logement = res.getLogement();
            logement.setCapacite(logement.getCapacite() + 1);

            // If it was not available before, make it available again
            if (logement.getDisponnibilite().equals("non disponible")) {
                logement.setDisponnibilite("disponible");
            }

            logementRepository.save(logement);

            // Optionally send notification for refused reservations
            if (adminEmail != null && !adminEmail.isEmpty()) {
                emailService.sendReservationNotification(res, adminEmail);
            }
        }

        return reservationRepository.save(res);
    }

    @Override
    @Transactional
    public void supprimerReservation(Long id) {
        Reservation res = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        // If the reservation was accepted, increase capacity when it's deleted
        if (res.getStatut().equals("ACCEPTEE")) {
            Logement logement = res.getLogement();
            logement.setCapacite(logement.getCapacite() + 1);

            // If it was not available before, make it available again
            if (logement.getDisponnibilite().equals("non disponible")) {
                logement.setDisponnibilite("disponible");
            }

            logementRepository.save(logement);
        }

        reservationRepository.deleteById(id);
    }
}
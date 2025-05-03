package tn.example.charity.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import tn.example.charity.Entity.DeliveryLocation;
import tn.example.charity.Entity.EtatLivraisons;
import tn.example.charity.Entity.Livraisons;
import tn.example.charity.Repository.DeliveryLocationRepository;
import tn.example.charity.Repository.LivraisonRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
@Slf4j
public class LivraisonServiceImpl implements ILivraisonService {
    LivraisonRepository livraisonRepository;
    private SimpMessagingTemplate messagingTemplate;
    private DeliveryLocationRepository deliveryLocationRepository;
    @Autowired
    EmailService emailService;
   /* @Autowired*/
   /* private OpenRouteServiceClient openRouteServiceClient;*/

    @Override
    public Livraisons addLivraison(Livraisons livraison) {
        Livraisons savedLivraison = livraisonRepository.save(livraison);
        String pinCode = String.format("%04d", new Random().nextInt(10000));
        livraison.setPinCode(pinCode);
        // Envoi d'un email au client
        if (livraison.getEmailClient() != null && !livraison.getEmailClient().isEmpty()) {
            String subject = "Confirmation de votre livraison";
            String body = "Bonjour " + livraison.getNom() + ",\n\n" +
                    "Votre demande de livraison à l'adresse : " + livraison.getAdresseLivr() +
                    " pour le " + livraison.getDateLivraison() + " a été bien enregistrée.\n\n" +
                    "Merci pour votre confiance.\nL'équipe Humanity";

            emailService.sendSimpleEmail(livraison.getEmailClient(), subject, body);
        }

        return savedLivraison;
    }

    public void deleteLivraison(Long idLivraison) {
        livraisonRepository.deleteById(idLivraison);

    }

    @Override
    public Livraisons modifyLivraison(Livraisons livraison) {
        return livraisonRepository.save(livraison);
    }

    @Override
    public List<Livraisons> getAllLivraison() {
        return livraisonRepository.findAll();
    }

    @Override
    public List<Livraisons> retreiveallLivraison(Livraisons livraison) {
        return List.of();
    }

    @Override
    public Livraisons retrieveallLivraisonbyid(Long idLivraison) {
        return livraisonRepository.findById(idLivraison).get();
    }

    @Override
    public byte[] generatePDF() {
        return new byte[0];
    }

    @Override
    public Livraisons updateDeliveryStatus(Long id, EtatLivraisons newStatus) {
        return null;
    }

    @Override
    public Optional<Livraisons> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public void save(Livraisons livraison) {

    }

    @Override
    public void enableTracking(Long livraisonId) {
        Livraisons livraison = livraisonRepository.findById(livraisonId)
                .orElseThrow(() -> new RuntimeException("Livraison not found"));
        livraison.setTrackingEnabled(true);
        livraisonRepository.save(livraison);
    }

    @Override
    public void updateDriverLocation(Long livraisonId, double latitude, double longitude) {
        Livraisons livraison = livraisonRepository.findById(livraisonId)
                .orElseThrow(() -> new RuntimeException("Livraison not found"));
        if (!livraison.isTrackingEnabled()) {
            throw new RuntimeException("Tracking is not enabled for this livraison");
        }

        DeliveryLocation location = new DeliveryLocation();
        location.setLivraisonId(livraisonId);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setTimestamp(LocalDateTime.now());
        deliveryLocationRepository.save(location);

        // Broadcast location update to WebSocket clients
        messagingTemplate.convertAndSend("/topic/delivery/" + livraisonId, location);
    }
}
   /* @Autowired
   /* private OpenRouteServiceClient openRouteServiceClient; // Injectez la classe OpenRouteServiceClient

    public String estimateDeliveryTime(double originLat, double originLon, double destLat, double destLon) {
        return openRouteServiceClient.estimateDeliveryTime(originLat, originLon, destLat, destLon);
    }
    @Override
    public String estimateDeliveryTime(double originLat, double originLon, double destLat, double destLon) {
        // Appel à la méthode estimateDeliveryTime de OpenRouteServiceClient
        return openRouteServiceClient.estimateDeliveryTime(originLat, originLon, destLat, destLon);
    }*/



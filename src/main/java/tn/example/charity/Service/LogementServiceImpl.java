package tn.example.charity.Service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.example.charity.Entity.Logement;
import tn.example.charity.Repository.LogementRepository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@AllArgsConstructor
public class LogementServiceImpl implements ILogementService {

    @Autowired
    private LogementRepository repositorylog;

    @Autowired
    private NotificationRefugeService notificationRefugeService; // Ajouter ici le service de notification

    private final Path rootLocation = Paths.get("uploads/images/refuges");

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage location", e);
        }
    }
    public Logement addLogement(Logement logement) {
        Logement savedLogement = repositorylog.save(logement);

        // Créer une notification après l'ajout du logement
        String notificationMessage = "Nouveau logement ajouté : " + savedLogement.getNom() +
                " à l'adresse " + savedLogement.getAdresse();
        notificationRefugeService.sendNotification(notificationMessage); // Envoi de la notification

        return savedLogement;
    }

    public List<Logement> getAllLogement() {
        return repositorylog.findAll();
    }

    public Logement getLogementById(Long idLog) {
        return repositorylog.findById(idLog)
                .orElseThrow(() -> new RuntimeException("Logement not found with id: " + idLog));
    }

    public Logement updateLogement(Logement logement) {
        return repositorylog.save(logement);
    }

    public void deleteLogement(Long idLog) {
        repositorylog.deleteById(idLog);
    }

    public List<Logement> searchLogementByNom(String nom) {
        return repositorylog.findByNomContainingIgnoreCase(nom);
    }
}

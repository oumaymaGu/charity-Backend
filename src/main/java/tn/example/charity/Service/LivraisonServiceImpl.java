package tn.example.charity.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.example.charity.Entity.Livraisons;
import tn.example.charity.Repository.LivraisonRepository;

import java.util.List;
@Service
@AllArgsConstructor
@Slf4j
public class LivraisonServiceImpl implements ILivraisonService {
    LivraisonRepository livraisonRepository;

    @Autowired
    EmailService emailService;

    @Override
    public Livraisons addLivraison(Livraisons livraison) {
        Livraisons savedLivraison = livraisonRepository.save(livraison);

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
}
package tn.example.charity.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.example.charity.Entity.Associations;
import tn.example.charity.Entity.Commentaire;
import tn.example.charity.Entity.User;
import tn.example.charity.Repository.AssociationsRepository;
import tn.example.charity.Repository.CommentaireRepository;
import tn.example.charity.Repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentaireService {

    @Autowired
    private CommentaireRepository commentaireRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssociationsRepository associationRepository;

    public Commentaire ajouterCommentaire(String email, Long idAss, String contenu) {
        System.err.println(email);
        // Récupérer l'utilisateur par email
        User user = userRepository.findByUsername(email).orElseThrow(() -> new RuntimeException("User not found"));

        // Récupérer l'association par id
        Associations association = associationRepository.findById(idAss).orElseThrow(() -> new RuntimeException("Association not found"));

        // Créer un nouveau commentaire
        Commentaire commentaire = new Commentaire();
        commentaire.setContenu(contenu);
        commentaire.setUser(user);
        commentaire.setAssociation(association);
        commentaire.setDateCreation(LocalDateTime.now());

        // Sauvegarder et retourner le commentaire
        return commentaireRepository.save(commentaire);
    }

    public List<Commentaire> getCommentaires(Long idAss) {
        return commentaireRepository.findByAssociationIdAssAndParentIsNullOrderByDateCreationDesc(idAss);
    }

    public Commentaire ajouterReponse(String email, Long parentId, String contenu) {
        User user = userRepository.findByUsername(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Commentaire parent = commentaireRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Commentaire parent non trouvé"));

        Commentaire reponse = new Commentaire();
        reponse.setUser(user);
        reponse.setParent(parent);
        reponse.setAssociation(parent.getAssociation());
        reponse.setContenu(contenu);
        reponse.setDateCreation(LocalDateTime.now());

        return commentaireRepository.save(reponse);
    }


    public Commentaire likeCommentaire(Long id) {
        Commentaire c = commentaireRepository.findById(id).orElseThrow();
        c.setLikes(c.getLikes() + 1);
        return commentaireRepository.save(c);
    }
}

package tn.example.charity.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import tn.example.charity.dto.CommentaireMessage;
import tn.example.charity.Entity.Commentaire;
import tn.example.charity.Service.CommentaireService;

import java.security.Principal;
@CrossOrigin(origins = "http://localhost:4200")
@Controller
public class CommentaireWSController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private CommentaireService commentaireService;

    @MessageMapping("/commentaires")
    public void envoyerCommentaire(CommentaireMessage message, Principal principal) {
        Commentaire commentaire = commentaireService.ajouterCommentaire(
                principal.getName(), message.getIdAss(), message.getContenu());

        // Envoyer à tous les abonnés du topic spécifique à l'association
        messagingTemplate.convertAndSend(
                "/topic/commentaires/" + message.getIdAss(), commentaire
        );
    }
}


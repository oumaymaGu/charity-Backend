package tn.example.charity.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import tn.example.charity.Entity.Commentaire;
import tn.example.charity.Service.CommentaireService;
import tn.example.charity.dto.CommentaireMessage;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/commentaires")
@CrossOrigin(origins = "http://localhost:4200")
public class CommentaireController {

    @Autowired
    private CommentaireService commentaireService;


    @PostMapping("/add")
    public ResponseEntity<?> ajouterCommentaire(
            @RequestBody CommentaireMessage request,
            Principal principal
    ) {
        if (principal == null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Utilisateur non authentifié");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Commentaire commentaire = commentaireService.ajouterCommentaire(
                principal.getName(),
                request.getIdAss(),
                request.getContenu()
        );

        return ResponseEntity.ok(commentaire);
    }




    @GetMapping("/association/{associationId}")
    public ResponseEntity<List<Commentaire>> getCommentaires(@PathVariable Long associationId) {
        // Récupérer les commentaires pour une association
        List<Commentaire> commentaires = commentaireService.getCommentaires(associationId);
        return ResponseEntity.ok(commentaires);
    }

    @PostMapping("/{commentaireId}/like")
    public ResponseEntity<?> likeCommentaire(@PathVariable Long commentaireId) {
        Commentaire commentaire = commentaireService.likeCommentaire(commentaireId);
        return ResponseEntity.ok(commentaire);
    }

    @PostMapping("/{commentaireId}/repondre")
    public ResponseEntity<?> ajouterReponse(
            @PathVariable Long commentaireId,
            @RequestBody CommentaireMessage request,
            Principal principal
    ) {
        if (principal == null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Utilisateur non authentifié");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Commentaire reponse = commentaireService.ajouterReponse(
                principal.getName(),
                commentaireId,
                request.getContenu()
        );

        return ResponseEntity.ok(reponse);
    }


}

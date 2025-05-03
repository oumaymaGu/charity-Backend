package tn.example.charity.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Commentaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contenu;

    private LocalDateTime dateCreation;
    private int likes = 0;

    @ManyToOne
    private User user;

    @ManyToOne
    private Associations association;

    // Référence vers le commentaire parent (la réponse appartient à un commentaire)
    @JsonBackReference
    @ManyToOne
    private Commentaire parent;

    // Liste des réponses liées à ce commentaire
    @JsonManagedReference
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Commentaire> reponses;
}

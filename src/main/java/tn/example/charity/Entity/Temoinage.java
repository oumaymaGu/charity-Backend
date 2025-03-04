package tn.example.charity.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Temoinage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idTemoin;
    private String description;
    private String nom;
    private String photoUrl;


    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private TemoinageStatut statut;



    private String typeTemoinage;
    private long likes;
    private String date;
    private String localisation;
    private int note;
    private String categorie;
    private String contact;

    @ManyToMany(mappedBy = "temoinnages", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Event> events;
}
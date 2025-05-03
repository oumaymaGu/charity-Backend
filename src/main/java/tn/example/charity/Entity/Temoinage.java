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
    private String description_en;
    private String nom;

    @Column(name = "photo_url", columnDefinition = "TEXT")


    private String photoUrl;
    @Column(name = "audio_path")
    private String audioPath;



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

    private String audioUrl;

    @ManyToMany(mappedBy = "temoinnages", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Event> events;
}
package tn.example.charity.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Refuge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRfg;
    private String nom;
    private String prenom;
    private String email;
    private String nationnalite;
    private Date datedenaissance;
    private String localisationActuel;
    private  String besoin;
    @OneToOne
    @JsonIgnore
    private Logement logement;
}

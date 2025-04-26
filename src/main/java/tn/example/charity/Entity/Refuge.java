package tn.example.charity.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
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
    private String besoin;

    // New field for image
    private String imagePath;
    private String detectedGender; // New field for API result
    private Double genderConfidence; // Confidence level from API

    private Integer detectedAge;  // New field for estimated age

    // Emotions - store confidence scores (0.0 to 100.0)
    private Double emotionHappiness;
    private Double emotionSadness;
    private Double emotionAnger;
    private Double emotionSurprise;
    private Double emotionFear;
    private Double emotionDisgust;
    private Double emotionNeutral;

    @OneToOne
    @JsonIgnore
    private Logement logement;
}
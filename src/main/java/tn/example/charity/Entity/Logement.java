package tn.example.charity.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Logement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idLog;
    private String nom;
    private String adresse;
    private int capacite;
    private String disponnibilite;
    @OneToOne
    private Refuge refuge;
}

package tn.example.charity.Entity;

import javax.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Livraisons {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idLivr;
    private String nom;
    private String adresseLivr;
    private Date dateLivraison;
    @Enumerated(EnumType.STRING)
    private EtatLivraisons  etatLivraisons;
}

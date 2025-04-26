package tn.example.charity.Entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReservation;
    private LocalDate dateDemande;
    private String statut; // "EN_ATTENTE", "ACCEPTEE", "REFUSEE"
    private String message;
    @ManyToOne
    private User demandeur;
    @ManyToOne
    private Logement logement;


}

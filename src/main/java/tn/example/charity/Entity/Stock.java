package tn.example.charity.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idStock;
    private int capaciteTotale; // Capacité maximale du stock
    private int capaciteDisponible;
    private LocalDateTime dateCreation;
    private String typeStock;
    private String lieu;
    private boolean alerteEnvoyee = false;
    private LocalDateTime derniereAlerte;


    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties("stocks")
    private Associations associations;
}

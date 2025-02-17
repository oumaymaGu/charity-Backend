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
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idUser;
    private String nom;
    private String prenom;
    private String mdp;
    @Enumerated(EnumType.STRING)
    private Role role;
    @OneToMany
    private List<Refuge>refuges;
    @OneToMany
    private List<Livraisons>livraisons;
    @OneToOne
    private Don don;
    @OneToOne
    private Event event;
    @ManyToOne
    private Associations associations;
    @ManyToMany
    private List<Temoinage>temoinages;
}

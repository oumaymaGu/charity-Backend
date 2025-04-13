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
public class Don {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idDon;
    private Date dateDon;
    private String photoUrl; // URL de la photo uploadée
    private String donorContact; // Contact du donneur
    private double amount;
    private String category;
    @Column(nullable = true) // Autorise NULL si le champ n'est pas toujours rempli
    private String medicationName;

    @Column(nullable = true)
    private String lotNumber;

    @Column(nullable = true)
    private String expirationDate;

    @Column(nullable = true)
    private String productCode;


    @Enumerated(EnumType.STRING)
    private TypeDon typeDon; // ARGENT ou MATERIELr

    @JsonIgnore
    @OneToMany(mappedBy = "don", cascade = CascadeType.ALL)
    private List<StripePayment> stripePayments;

    @ManyToOne
    @JsonIgnore
    private Stock stock;

    @OneToOne
    @JsonIgnore
    private Livraisons livraisons;


}

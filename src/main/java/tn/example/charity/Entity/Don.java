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
    private String photoUrl;
    private String donorContact; // Used for email
    private String donorName;   // Added for donor's name
    private double amount;
    private String category;
    @Column(nullable = true)
    private String medicationName;
    @Column(nullable = true)
    private String lotNumber;
    @Column(nullable = true)
    private String fabricationDate;
    @Column(nullable = true)
    private String expirationDate;
    @Column(nullable = true)
    private String productCode;
    @Column(nullable = true)
    private String imageHash;
    @Column(nullable = true)
    private Integer quantity = 1;

    @Enumerated(EnumType.STRING)
    private TypeDon typeDon;

    @JsonIgnore
    @OneToMany(mappedBy = "don", cascade = CascadeType.ALL)
    private List<StripePayment> stripePayments;

    @ManyToOne
    @JsonIgnore
    private Stock stock;

    @OneToOne
    @JsonIgnore
    private Livraisons livraisons;

    public int getSafeQuantity() {
        return quantity != null && quantity > 0 ? quantity : 1;
    }
}
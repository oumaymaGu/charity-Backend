package tn.example.charity.Entity;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class StripePayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Stripe-specific fields
    private String paymentIntentId;
    private String chargeId;
    private String status;
    private String receiptUrl;
    private String clientSecret; // Ajout du champ clientSecret

    // Common fields
    private double amount;
    private String currency;
    private String email;
    private String description;

    @CreationTimestamp
    private LocalDateTime paymentDate;

    @ManyToOne
    @JoinColumn(name = "don_id_don", nullable = false)
    private Don don;
}
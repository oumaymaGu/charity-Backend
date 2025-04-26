package tn.example.charity.Entity;

import lombok.*;
import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String message;
        private String type; // "DON_MATERIEL", "DON_FINANCIER", "STRIPE_PAYMENT"
        private Date timestamp;
        private boolean isRead;

        @ManyToOne
        @JoinColumn(name = "don_id")
        private Don donation;

        @ManyToOne
        @JoinColumn(name = "stripe_payment_id")
        private StripePayment stripePayment;

        // Méthode utilitaire pour déterminer l'ID de l'entité liée
        public Long getLinkedEntityId() {
                return donation != null ? donation.getIdDon() :
                        stripePayment != null ? stripePayment.getId() : null;
        }
}
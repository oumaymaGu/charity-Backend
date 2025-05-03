package tn.example.charity.Entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long livraisonId; // Links to Livraisons entity
    private double latitude;
    private double longitude;
    private LocalDateTime timestamp;
}
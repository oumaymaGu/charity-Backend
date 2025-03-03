package tn.example.charity.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idpmt;
    private double amount;
    private String email;
    private String cardNumber;
    private int expirationMonth;
    private int expirationYear;
    private String cvv;
    private String cardHolderName;
    @CreationTimestamp
    private LocalDateTime date; // Date de création automatique
    @JsonIgnore
    @ManyToOne
    private Don don;

}

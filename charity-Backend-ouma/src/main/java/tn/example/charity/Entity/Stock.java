package tn.example.charity.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

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
    private int capacite;
    private String acheminement;
    private String TypeStock;
    @ManyToOne
    @JsonIgnore
    private Associations associations;
}

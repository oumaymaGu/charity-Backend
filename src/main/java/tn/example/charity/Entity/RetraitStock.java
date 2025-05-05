package tn.example.charity.Entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RetraitStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRetrait;

    private int quantite;
    private LocalDateTime dateRetrait;

    @ManyToOne
    @JoinColumn(name = "stock_id")
    private Stock stock;
}

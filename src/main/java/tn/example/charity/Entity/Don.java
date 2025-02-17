package tn.example.charity.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

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
    private  long idDon;
    private Float montant;
    private String methodePaiment;
    private String statusPaiment;

    @Enumerated(EnumType.STRING)
    private TypeDon  typeDon;
    @JsonIgnore
    @OneToMany(mappedBy = "don",cascade = CascadeType.ALL)
    private List<SuiviDon> svd;
    @ManyToOne
    private Stock stock;
    @OneToOne
    private Livraisons livraisons;
}

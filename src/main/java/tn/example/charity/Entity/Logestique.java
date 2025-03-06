package tn.example.charity.Entity;


import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Logestique {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idlogestique;
    private String ressourceName;
    private float quantity;

}

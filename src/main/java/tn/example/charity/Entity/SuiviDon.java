package tn.example.charity.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SuiviDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idSuivi;
    private Date dateSuivi;
    private  Date dateFin;
    private String status;
    @JsonIgnore
    @ManyToOne
    private Don don;

}

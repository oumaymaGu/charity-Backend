package tn.example.charity.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Associations {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAss;
    private String nomAss;
    private String lieu;
    private Date date;
    private String contact;
    private String email;
    private String description;
    @OneToMany(mappedBy = "association",cascade = CascadeType.ALL)
    private List<Event> events;
    @OneToMany (mappedBy = "associations",cascade = CascadeType.ALL)
    private List<Stock> stocks;
}


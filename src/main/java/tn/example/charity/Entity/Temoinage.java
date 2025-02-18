package tn.example.charity.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Temoinage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idTemoin;
    private String description;
    @ManyToMany(mappedBy = "temoinnages",cascade = CascadeType.ALL)
    private List<Event> events;

}

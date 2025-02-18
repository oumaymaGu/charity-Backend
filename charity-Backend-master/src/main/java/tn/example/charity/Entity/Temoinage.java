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
public class Temoinage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idTemoin;
    private String description;
    @JsonIgnore
    @ManyToMany(mappedBy = "temoinnages",cascade = CascadeType.ALL)
    private List<Event> events;

}

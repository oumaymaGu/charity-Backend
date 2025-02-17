package tn.example.charity.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long idEvent;
        private String nomEvent;
        private String description;
        private String lieu;
        private float prixevent;
        private Date dateEvent;

        @OneToMany(cascade = CascadeType.ALL)
        private List<Logestique > logestiques;
        @ManyToMany(cascade = CascadeType.ALL)
        private Set<Temoinage> temoinnages;
        @ManyToOne(cascade = CascadeType.ALL)
        private Associations association;
    }




package tn.example.charity.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
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
    private String weatherDescription;
    private Double temperature;
    private Double latitude;
    private Double longitude;
    private String photoEvent;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Logestique> logestiques;


    @JsonIgnore
    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Temoinage> temoinnages;
    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    private Associations association;

    @ManyToMany(mappedBy = "events")
    @JsonIgnore
    private Set<User> users = new HashSet<>();
}
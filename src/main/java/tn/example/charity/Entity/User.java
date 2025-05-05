package tn.example.charity.Entity;

import javax.persistence.*;
import javax.validation.constraints.Email;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email") })

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUser;
    private String username;
    private String password;
    @Email
    private String email;
    @Lob
    private String resetToken;
    @Enumerated(EnumType.STRING)
    private Role role;



    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<URole> roles = new HashSet<>();

    @OneToMany
    private List<Refuge>refuges;
    @OneToMany
    private List<Livraisons>livraisons;
    @OneToOne
    private Don don;
    @OneToOne
    private Event event;
    @ManyToOne
    private Associations associations;
    @ManyToMany
    private List<Temoinage>temoinages;




    public User() {
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}

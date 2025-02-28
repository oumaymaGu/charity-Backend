package tn.example.charity.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "roles")
public class URole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Role role;



    public URole(Role role) {
        this.role = role;
    }

    public URole() {

    }
}

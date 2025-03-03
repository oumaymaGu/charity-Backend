package tn.example.charity.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.nio.file.FileStore;

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



    public URole() {

    }

    public URole(Role role) {
        this.role = role;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}

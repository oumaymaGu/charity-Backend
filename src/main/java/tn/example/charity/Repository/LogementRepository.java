package tn.example.charity.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.example.charity.Entity.Don;
import tn.example.charity.Entity.Logement;

import java.util.List;

@Repository
public interface LogementRepository extends JpaRepository<Logement, Long> {
    // Recherche des logements par nom, insensible à la casse
    @Query("SELECT l FROM Logement l WHERE LOWER(l.nom) LIKE LOWER(CONCAT('%', :nom, '%'))")
    List<Logement> findByNomContainingIgnoreCase(@Param("nom") String nom);
}

package tn.example.charity.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.example.charity.Entity.Temoinage;
import tn.example.charity.Entity.TemoinageStatut;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemoinageRepository extends JpaRepository<Temoinage, Long> {



    Optional<Temoinage> findByNom(String nom);
    List<Temoinage> findByStatut(TemoinageStatut statut);

}
package tn.example.charity.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.example.charity.Entity.DeliveryLocation;

import java.util.List;

public interface DeliveryLocationRepository extends JpaRepository<DeliveryLocation, Long> {
    List<DeliveryLocation> findByLivraisonId(Long livraisonId);
}
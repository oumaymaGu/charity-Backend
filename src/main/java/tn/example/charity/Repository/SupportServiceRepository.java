package tn.example.charity.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.example.charity.Entity.ServiceType;
import tn.example.charity.Entity.SupportService;

import java.util.List;

@Repository
public interface SupportServiceRepository extends JpaRepository<SupportService, Long> {
    List<SupportService> findByEmergencyServiceTrue();
    List<SupportService> findByType(ServiceType type);
    List<SupportService> findBySpecializesInViolenceTrue();
    List<SupportService> findBySpecializesInParanoiaTrue();
    // Autres méthodes similaires pour les autres spécialisations
}
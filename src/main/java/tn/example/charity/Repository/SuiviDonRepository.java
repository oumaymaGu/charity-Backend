package tn.example.charity.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.example.charity.Entity.Don;
import tn.example.charity.Entity.SuiviDon;
@Repository

public interface SuiviDonRepository extends JpaRepository<SuiviDon, Long> {
}

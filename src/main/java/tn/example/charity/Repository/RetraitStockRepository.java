package tn.example.charity.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.example.charity.Entity.RetraitStock;

@Repository
public interface RetraitStockRepository extends JpaRepository<RetraitStock, Long> {
}

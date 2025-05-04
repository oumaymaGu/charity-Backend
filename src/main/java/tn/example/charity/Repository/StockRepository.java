package tn.example.charity.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.example.charity.Entity.Associations;
import tn.example.charity.Entity.Don;
import tn.example.charity.Entity.Stock;

import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    Optional<Stock> findByTypeStockAndLieuAndAssociations(String typeStock, String lieu, Associations associations);

}

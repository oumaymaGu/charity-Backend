package tn.example.charity.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.example.charity.Entity.Don;
import tn.example.charity.Entity.TypeDon;

import java.time.LocalDateTime;

@Repository
public interface DonRepository extends JpaRepository<Don, Long> {
    boolean existsByTypeDonAndCategoryAndDateDonAfter(
            TypeDon typeDon,
            String category,
            LocalDateTime date
    );
}

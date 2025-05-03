package tn.example.charity.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.example.charity.Entity.Don;
import tn.example.charity.Entity.TypeDon;

import java.util.List;
import java.util.Optional;

public interface DonRepository extends JpaRepository<Don, Long> {
    Optional<Don> findFirstByPhotoUrl(String photoUrl);
    Optional<Don> findFirstByPhotoUrlAndCategory(String photoUrl, String category);
    Optional<Don> findFirstByPhotoUrlAndCategoryAndMedicationNameAndLotNumber(
            String photoUrl, String category, String medicationName, String lotNumber);
    Optional<Don> findFirstByImageHash(String imageHash);
    // New method for searching by category
    List<Don> findByCategoryAndTypeDon(String category, TypeDon typeDon);
}
package tn.example.charity.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.example.charity.Entity.Associations;
import tn.example.charity.Entity.Don;
@Repository
public interface AssociationsRepository extends JpaRepository<Associations, Long> {

}

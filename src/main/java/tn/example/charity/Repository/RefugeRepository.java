package tn.example.charity.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.example.charity.Entity.Don;
import tn.example.charity.Entity.Refuge;
@Repository
public interface RefugeRepository extends JpaRepository<Refuge, Long> {

    Refuge findTopByOrderByIdRfgDesc();

}

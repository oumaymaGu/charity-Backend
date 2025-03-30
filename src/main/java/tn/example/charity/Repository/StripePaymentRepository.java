package tn.example.charity.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.example.charity.Entity.StripePayment;

import java.util.List;
@Repository
public interface StripePaymentRepository extends JpaRepository<StripePayment, Long> {
    List<StripePayment> findByDonIdDon(Long donId);
}


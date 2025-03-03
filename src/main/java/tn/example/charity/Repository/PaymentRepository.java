package tn.example.charity.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.example.charity.Entity.Payment;

import java.util.List;

@Repository

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByDonIdDon(Long donId);
}

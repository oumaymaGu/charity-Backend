package tn.example.charity.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.example.charity.Entity.Payment;
import tn.example.charity.Repository.PaymentRepository;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j

public class PaymentServiceImpl implements IPaymentService {

    PaymentRepository paymentRepository;



    @Override
    public Payment addPayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    @Override
    public List<Payment> getPaymentsByDonId(Long donId) {
        // Utiliser la méthode corrigée ou personnalisée
        return paymentRepository.findByDonIdDon(donId); // Ou findByDonId(donId) si vous avez utilisé @Query
    }

    public List<Payment> getAllPayment() {
        return paymentRepository.findAll();
    }

    @Override
    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }
}






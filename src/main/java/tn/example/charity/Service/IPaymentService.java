package tn.example.charity.Service;

import tn.example.charity.Entity.Payment;

import java.util.List;

public interface IPaymentService {
    Payment addPayment(Payment payment);
    void deletePayment(Long idPayment);
    List<Payment> getPaymentsByDonId(Long donId);
    List<Payment> getAllPayment();

}

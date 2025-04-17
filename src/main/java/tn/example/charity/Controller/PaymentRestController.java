package tn.example.charity.Controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.example.charity.Entity.Payment;
import tn.example.charity.Service.IPaymentService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/payments")
@CrossOrigin(origins = "http://localhost:4200")
public class PaymentRestController {
    IPaymentService paymentService;

    @PostMapping("/add")
    public Payment addPayment(@RequestBody Payment payment) {
        return paymentService.addPayment(payment);
    }

    @GetMapping("/don/{donId}")
    public List<Payment> getPaymentsByDonId(@PathVariable Long donId) {
        return paymentService.getPaymentsByDonId(donId);
    }

    @DeleteMapping("/delete/{id}")
    public void deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
    }

    @GetMapping("/all")
    public List<Payment> getAllPayments() {
        return paymentService.getAllPayment();
    }
}



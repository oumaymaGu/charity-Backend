package tn.example.charity.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.example.charity.Entity.Don;
import tn.example.charity.Entity.StripePayment;
import tn.example.charity.Repository.DonRepository;
import tn.example.charity.Repository.StripePaymentRepository;
import tn.example.charity.dto.StripePaymentRequest;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class StripePaymentServiceImpl implements IStripePaymentService {

    private final StripePaymentRepository stripePaymentRepository;
    private final DonRepository donRepository;

    static {
        Stripe.apiKey = "sk_test_51QwSi3GduHSrwvYo7AR4eR1xJUxwF2Va4sM8h4hQbOZmmPBgaLq8UR4w1J1UWxbbEYiox8FIiynYBN7OXAVqirTs00d9aYXRAN";
    }

    @Override
    public StripePayment createPaymentIntent(StripePaymentRequest request) throws StripeException {
        try {
            // Vérification de l'existence du don
            Optional<Don> donOptional = donRepository.findById(request.getDonId());
            if (donOptional.isEmpty()) {
                log.error("Don not found with ID: {}", request.getDonId());
                throw new IllegalArgumentException("Don not found with ID: " + request.getDonId());
            }

            log.info("Creating PaymentIntent for donation ID: {}", request.getDonId());

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(request.getAmountInCents()) // Utilisation de la méthode du DTO
                    .setCurrency(request.getCurrency().toLowerCase())
                    .setDescription(request.getDescription())
                    .setReceiptEmail(request.getEmail())
                    .putMetadata("donId", request.getDonId().toString())
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build()
                    )
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);
            return savePayment(paymentIntent, request, donOptional.get());

        } catch (StripeException e) {
            log.error("Stripe error while creating PaymentIntent: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in createPaymentIntent: {}", e.getMessage());
            throw new RuntimeException("Failed to create payment intent", e);
        }
    }

    public StripePayment savePayment(PaymentIntent paymentIntent, StripePaymentRequest request, Don don) {
        try {
            StripePayment payment = new StripePayment();
            payment.setPaymentIntentId(paymentIntent.getId());
            payment.setClientSecret(paymentIntent.getClientSecret()); // Stockage du client secret
            payment.setAmount(request.getAmount()); // Utilisation du montant original
            payment.setCurrency(request.getCurrency());
            payment.setStatus(paymentIntent.getStatus());
            payment.setEmail(request.getEmail());
            payment.setDescription(request.getDescription());
            payment.setDon(don);

            return stripePaymentRepository.save(payment);
        } catch (Exception e) {
            log.error("Error saving payment: {}", e.getMessage());
            throw new RuntimeException("Failed to save payment", e);
        }
    }

    // ... autres méthodes inchangées ...


    @Override
    public List<StripePayment> getPaymentsByDonId(Long donId) {
        try {
            return stripePaymentRepository.findByDonIdDon(donId);
        } catch (Exception e) {
            log.error("Error retrieving payments for donId {}: {}", donId, e.getMessage());
            throw new RuntimeException("Failed to retrieve payments", e);
        }
    }

    @Override
    public List<StripePayment> getAllStripePayments() {
        try {
            return stripePaymentRepository.findAll();
        } catch (Exception e) {
            log.error("Error retrieving all payments: {}", e.getMessage());
            throw new RuntimeException("Failed to retrieve payments", e);
        }
    }

    @Override
    public StripePayment getStripePaymentById(Long id) {
        try {
            return stripePaymentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + id));
        } catch (Exception e) {
            log.error("Error retrieving payment with ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to retrieve payment", e);
        }
    }

    @Override
    public StripePayment updatePaymentStatus(Long id, String status) {
        try {
            StripePayment payment = getStripePaymentById(id);
            payment.setStatus(status);
            return stripePaymentRepository.save(payment);
        } catch (Exception e) {
            log.error("Error updating payment status for ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to update payment status", e);
        }
    }

    @Override
    public void deleteStripePayment(Long id) {
        try {
            stripePaymentRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Error deleting payment with ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to delete payment", e);
        }
    }
}
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
import tn.example.charity.Entity.TypeDon;
import tn.example.charity.Repository.DonRepository;
import tn.example.charity.Repository.StripePaymentRepository;
import tn.example.charity.dto.StripePaymentRequest;

import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class StripePaymentServiceImpl implements IStripePaymentService {

    private final StripePaymentRepository stripePaymentRepository;
    private final DonRepository donRepository;
    private final NotificationService notificationService;

    static {
        Stripe.apiKey = "sk_test_51QwSi3GduHSrwvYo7AR4eR1xJUxwF2Va4sM8h4hQbOZmmPBgaLq8UR4w1J1UWxbbEYiox8FIiynYBN7OXAVqirTs00d9aYXRAN";
    }

    @Override
    public StripePayment createPaymentIntent(StripePaymentRequest request) throws StripeException {
        try {
            log.info("Reçu - Montant : {} (vérifier si en euros ou centimes)", request.getAmount());
            Don don = handleDonAssociation(request);
            PaymentIntent paymentIntent = createStripePaymentIntent(request, don);
            StripePayment savedPayment = savePayment(paymentIntent, request, don);
            log.info("Paiement sauvegardé - Montant : {} EUR", savedPayment.getAmount());
            notificationService.createAndSendStripeNotification(savedPayment);
            return savedPayment;
        } catch (StripeException e) {
            log.error("Erreur Stripe : {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erreur inattendue : {}", e.getMessage());
            throw new RuntimeException("Échec du traitement du paiement", e);
        }
    }

    private Don handleDonAssociation(StripePaymentRequest request) {
        // Convertir le montant en euros si nécessaire
        double amountInEuros = request.getAmount();
        if (amountInEuros > 1000) { // Si le montant est trop grand, il est probablement en centimes
            amountInEuros = amountInEuros / 100.0;
            log.info("Montant converti de centimes à euros : {} -> {} EUR", request.getAmount(), amountInEuros);
        }

        if (request.getDonId() == null) {
            Don newDon = new Don();
            newDon.setDateDon(new Date());
            newDon.setAmount(amountInEuros); // En euros
            newDon.setTypeDon(TypeDon.ARGENT);
            return donRepository.save(newDon);
        } else {
            return donRepository.findById(request.getDonId())
                    .orElseThrow(() -> new IllegalArgumentException("Don non trouvé avec l'ID : " + request.getDonId()));
        }
    }

    private PaymentIntent createStripePaymentIntent(StripePaymentRequest request, Don don) throws StripeException {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(request.getAmountInCents()) // Utiliser getAmountInCents() pour Stripe
                .setCurrency(request.getCurrency().toLowerCase())
                .setDescription(request.getDescription())
                .setReceiptEmail(request.getEmail())
                .putMetadata("donId", String.valueOf(don.getIdDon()))
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .build();
        return PaymentIntent.create(params);
    }

    public StripePayment savePayment(PaymentIntent paymentIntent, StripePaymentRequest request, Don don) {
        // Convertir le montant en euros si nécessaire
        double amountInEuros = request.getAmount();
        if (amountInEuros > 1000) { // Si le montant est trop grand, il est probablement en centimes
            amountInEuros = amountInEuros / 100.0;
            log.info("Montant converti de centimes à euros pour sauvegarde : {} -> {} EUR", request.getAmount(), amountInEuros);
        }

        StripePayment payment = new StripePayment();
        payment.setPaymentIntentId(paymentIntent.getId());
        payment.setClientSecret(paymentIntent.getClientSecret());
        payment.setAmount(amountInEuros); // En euros
        payment.setCurrency(request.getCurrency());
        payment.setStatus(paymentIntent.getStatus());
        payment.setEmail(request.getEmail());
        payment.setDescription(request.getDescription());
        payment.setDon(don);
        return stripePaymentRepository.save(payment);
    }

    @Override
    public List<StripePayment> getPaymentsByDonId(Long donId) {
        try {
            return stripePaymentRepository.findByDonIdDon(donId);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des paiements pour donId {} : {}", donId, e.getMessage());
            throw new RuntimeException("Échec de la récupération des paiements", e);
        }
    }

    @Override
    public List<StripePayment> getAllStripePayments() {
        try {
            return stripePaymentRepository.findAll();
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de tous les paiements : {}", e.getMessage());
            throw new RuntimeException("Échec de la récupération des paiements", e);
        }
    }

    @Override
    public StripePayment getStripePaymentById(Long id) {
        try {
            return stripePaymentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Paiement non trouvé avec l'ID : " + id));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du paiement avec l'ID {} : {}", id, e.getMessage());
            throw new RuntimeException("Échec de la récupération du paiement", e);
        }
    }

    @Override
    public StripePayment updatePaymentStatus(Long id, String status) {
        try {
            StripePayment payment = getStripePaymentById(id);
            payment.setStatus(status);
            return stripePaymentRepository.save(payment);
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour du statut du paiement pour l'ID {} : {}", id, e.getMessage());
            throw new RuntimeException("Échec de la mise à jour du statut du paiement", e);
        }
    }

    @Override
    public void deleteStripePayment(Long id) {
        try {
            stripePaymentRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Erreur lors de la suppression du paiement avec l'ID {} : {}", id, e.getMessage());
            throw new RuntimeException("Échec de la suppression du paiement", e);
        }
    }

    @Override
    public List<StripePayment> getAllPayment() {
        return stripePaymentRepository.findAll();
    }

    @Override
    public PaymentIntent retrievePaymentIntent(String paymentIntentId) throws StripeException {
        return PaymentIntent.retrieve(paymentIntentId);
    }
}
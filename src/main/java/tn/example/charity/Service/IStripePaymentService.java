package tn.example.charity.Service;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import tn.example.charity.Entity.Don;
import tn.example.charity.Entity.StripePayment;
import tn.example.charity.dto.StripePaymentRequest;

import java.util.List;

public interface IStripePaymentService {

    // Créer un Payment Intent Stripe et enregistrer le paiement
    StripePayment createPaymentIntent(StripePaymentRequest request) throws StripeException;

    // Enregistrer un paiement Stripe
    StripePayment savePayment(PaymentIntent paymentIntent, StripePaymentRequest request, Don don);

    // Récupérer les paiements par ID de don
    List<StripePayment> getPaymentsByDonId(Long donId);

    // Récupérer tous les paiements Stripe
    List<StripePayment> getAllStripePayments();

    // Récupérer un paiement par son ID
    StripePayment getStripePaymentById(Long id);

    // Mettre à jour le statut d'un paiement
    StripePayment updatePaymentStatus(Long id, String status);

    // Supprimer un paiement
    void deleteStripePayment(Long id);

    // Récupérer tous les paiements
    List<StripePayment> getAllPayment();

    // Nouvelle méthode pour récupérer un PaymentIntent
    PaymentIntent retrievePaymentIntent(String paymentIntentId) throws StripeException;
}
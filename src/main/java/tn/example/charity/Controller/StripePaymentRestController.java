package tn.example.charity.Controller;

import com.itextpdf.text.Font;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.example.charity.Entity.Payment;
import tn.example.charity.Service.IStripePaymentService;
import tn.example.charity.dto.StripePaymentRequest;
import tn.example.charity.Entity.StripePayment;


import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.Date;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/stripe-payments")
@CrossOrigin(origins = {"*"})
public class StripePaymentRestController {

    private final IStripePaymentService stripePaymentService;

    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processPayment(@RequestBody StripePaymentRequest request) {
        try {
            log.info("Processing payment for donation ID: {}", request.getDonId());
            StripePayment payment = stripePaymentService.createPaymentIntent(request);

            Map<String, Object> response = new HashMap<>();
            response.put("payment", payment);

            if ("requires_payment_method".equals(payment.getStatus())) {
                response.put("requiresAction", true);
                response.put("clientSecret", payment.getClientSecret());
                response.put("message", "Complete your payment");
            }

            return ResponseEntity.ok(response);

        } catch (StripeException e) {
            log.error("Stripe error - Code: {}, Message: {}", e.getCode(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(errorResponse("STRIPE_ERROR", e.getMessage(), e.getCode()));
        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(errorResponse("VALIDATION_ERROR", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(errorResponse("SERVER_ERROR", "Internal server error"));
        }
    }

    @GetMapping("/don/{donId}")
    public ResponseEntity<?> getPaymentsByDonId(@PathVariable Long donId) {
        try {
            List<StripePayment> payments = stripePaymentService.getPaymentsByDonId(donId);
            return payments.isEmpty() ?
                    ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(errorResponse("NOT_FOUND", "No payments found")) :
                    ResponseEntity.ok(payments);
        } catch (Exception e) {
            log.error("Retrieval error: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(errorResponse("SERVER_ERROR", "Failed to retrieve payments"));
        }
    }
    @GetMapping("/all")
    public List<StripePayment> getAllPayments() {

        return stripePaymentService.getAllPayment();
    }
    @GetMapping("/receipt/{paymentIntentId}")
    public ResponseEntity<ByteArrayResource> generateReceipt(
            @PathVariable String paymentIntentId,
            @RequestParam String customerName,
            @RequestParam String email,
            @RequestParam double amount) {

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);

            document.open();

            // Ajouter le contenu du PDF
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Reçu de Payement", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Date: " + new Date()));
            document.add(new Paragraph("Transaction ID: " + paymentIntentId));
            document.add(new Paragraph("Nom: " + customerName));
            document.add(new Paragraph("Email: " + email));
            document.add(new Paragraph(String.format("Montant: %.2f €", amount)));

            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Merci pour votre don !"));

            document.close();

            byte[] bytes = outputStream.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(bytes);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=receipt_" + paymentIntentId + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(bytes.length)
                    .body(resource);

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du reçu", e);
        }
    }


    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Stripe service is operational");
    }

    // Ajoutez cette méthode pour gérer les cas sans code d'erreur
    private Map<String, Object> errorResponse(String errorType, String message) {
        return errorResponse(errorType, message, null);
    }

    private Map<String, Object> errorResponse(String errorType, String message, String code) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", errorType);
        response.put("message", message);
        if (code != null) {
            response.put("code", code);
        }
        return response;
    }
}
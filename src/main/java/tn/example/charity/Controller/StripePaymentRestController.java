package tn.example.charity.Controller;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.example.charity.Service.IStripePaymentService;
import tn.example.charity.dto.StripePaymentRequest;
import tn.example.charity.Entity.StripePayment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
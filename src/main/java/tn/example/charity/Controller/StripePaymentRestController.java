package tn.example.charity.Controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.example.charity.Entity.StripePayment;
import tn.example.charity.Service.IStripePaymentService;
import tn.example.charity.dto.StripePaymentRequest;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    @GetMapping("/all")
    public List<StripePayment> getAllPayments() {
        return stripePaymentService.getAllPayment();
    }

    @GetMapping("/receipt/{paymentIntentId}")
    public ResponseEntity<ByteArrayResource> generateStyledReceipt(
            @PathVariable String paymentIntentId,
            @RequestParam String customerName,
            @RequestParam String email,
            @RequestParam double amount,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String phone) throws StripeException {

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Définition des couleurs personnalisées
            BaseColor headerBg = new BaseColor(44, 62, 80);
            BaseColor headerText = BaseColor.WHITE;
            BaseColor borderColor = new BaseColor(189, 195, 199);
            BaseColor labelColor = new BaseColor(52, 73, 94);
            BaseColor valueColor = BaseColor.BLACK;

            // Fontes
            Font organizationFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, labelColor);
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, labelColor);
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, labelColor);
            Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 12, valueColor);
            Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, headerText);
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.GRAY);

            // Ajout d'un fond décoratif
            try {
                ClassPathResource backgroundResource = new ClassPathResource("static/images/background.png");
                InputStream backgroundStream = backgroundResource.getInputStream();
                byte[] backgroundBytes = backgroundStream.readAllBytes();
                Image background = Image.getInstance(backgroundBytes);
                background.setAbsolutePosition(0, 0);
                background.scaleToFit(document.getPageSize().getWidth(), document.getPageSize().getHeight());
                document.add(background);
            } catch (Exception e) {
                log.warn("Background image not found in classpath (static/images/background.png), skipping background: {}", e.getMessage());
            }

            // En-tête : Logo et Nom de l'organisation
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{1f, 4f});
            headerTable.setSpacingAfter(20f);

            // Logo
            try {
                ClassPathResource logoResource = new ClassPathResource("static/images/logo.png");
                InputStream logoStream = logoResource.getInputStream();
                byte[] logoBytes = logoStream.readAllBytes();
                Image logo = Image.getInstance(logoBytes);
                logo.scaleToFit(50, 50);
                PdfPCell logoCell = new PdfPCell(logo);
                logoCell.setBorder(Rectangle.NO_BORDER);
                logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                headerTable.addCell(logoCell);
            } catch (Exception e) {
                log.warn("Logo not found in classpath (static/images/logo.png), skipping logo addition: {}", e.getMessage());
                PdfPCell emptyCell = new PdfPCell();
                emptyCell.setBorder(Rectangle.NO_BORDER);
                headerTable.addCell(emptyCell);
            }

            // Nom de l'organisation
            PdfPCell orgCell = new PdfPCell(new Phrase("Humanity", organizationFont));
            orgCell.setBorder(Rectangle.NO_BORDER);
            orgCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            orgCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            headerTable.addCell(orgCell);

            document.add(headerTable);

            // Titre et message d'acceptation
            Paragraph title = new Paragraph("REÇU DE PAIEMENT", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10f);
            document.add(title);

            Paragraph acceptance = new Paragraph("Votre paiement a été accepté", FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.GRAY));
            acceptance.setAlignment(Element.ALIGN_CENTER);
            acceptance.setSpacingAfter(20f);
            document.add(acceptance);

            // Tableau avec bordures et couleurs
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(90); // Ajusté pour centrer avec des espaces
            table.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.setWidths(new float[]{3f, 7f});
            table.setSpacingBefore(10f);

            // En-tête stylisé
            PdfPCell headerCell1 = new PdfPCell(new Phrase("DÉTAIL", tableHeaderFont));
            headerCell1.setBackgroundColor(headerBg);
            headerCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell1.setBorderColor(borderColor);
            table.addCell(headerCell1);

            PdfPCell headerCell2 = new PdfPCell(new Phrase("VALEUR", tableHeaderFont));
            headerCell2.setBackgroundColor(headerBg);
            headerCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell2.setBorderColor(borderColor);
            table.addCell(headerCell2);

            // Récupérer les détails du PaymentIntent
            PaymentIntent paymentIntent = stripePaymentService.retrievePaymentIntent(paymentIntentId);
            String paymentMethodId = paymentIntent.getPaymentMethod();
            PaymentMethod paymentMethod = paymentMethodId != null ? PaymentMethod.retrieve(paymentMethodId) : null;
            String brand = paymentMethod != null && paymentMethod.getCard() != null ? paymentMethod.getCard().getBrand() : "Unknown";
            String last4 = paymentMethod != null && paymentMethod.getCard() != null ? paymentMethod.getCard().getLast4() : "****";
            String authorizationCode = "N/A"; // À améliorer avec les Charges si nécessaire
            String transactionId = paymentIntent.getId();

            // Formatage de la date
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String formattedDate = dateFormat.format(new Date(paymentIntent.getCreated() * 1000L));

            // DÉTAIL DE PAIEMENT
            PdfPCell detailHeader = new PdfPCell(new Phrase("DÉTAIL DE PAIEMENT", tableHeaderFont));
            detailHeader.setColspan(2);
            detailHeader.setBackgroundColor(headerBg);
            detailHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            detailHeader.setBorderColor(borderColor);
            table.addCell(detailHeader);

            addStyledRow(table, "Date de paiement :", formattedDate, labelFont, valueFont, borderColor);
            addStyledRow(table, "N° de paiement :", paymentIntent.getId(), labelFont, valueFont, borderColor);
            addStyledRow(table, "Code d'autorisation :", authorizationCode, labelFont, valueFont, borderColor);
            addStyledRow(table, "Méthode de paiement :", brand.toUpperCase(), labelFont, valueFont, borderColor);
            addStyledRow(table, "N° de carte de paiement :", "**** **** **** " + last4, labelFont, valueFont, borderColor);
            addStyledRow(table, "N° transaction :", transactionId, labelFont, valueFont, borderColor);

            // DÉTAIL DE LA COMMANDE
            PdfPCell commandHeader = new PdfPCell(new Phrase("DÉTAIL DE LA COMMANDE", tableHeaderFont));
            commandHeader.setColspan(2);
            commandHeader.setBackgroundColor(headerBg);
            commandHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            commandHeader.setBorderColor(borderColor);
            table.addCell(commandHeader);

            addStyledRow(table, "Identifiant :", paymentIntent.getId() + "_order", labelFont, valueFont, borderColor);
            addStyledRow(table, "Montant (EUR) :", String.format("%.2f EUR", amount), labelFont, valueFont, borderColor);

            // INFORMATIONS DU CLIENT
            PdfPCell clientHeader = new PdfPCell(new Phrase("INFORMATIONS DU CLIENT", tableHeaderFont));
            clientHeader.setColspan(2);
            clientHeader.setBackgroundColor(headerBg);
            clientHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            clientHeader.setBorderColor(borderColor);
            table.addCell(clientHeader);

            addStyledRow(table, "Nom :", customerName, labelFont, valueFont, borderColor);
            addStyledRow(table, "Email :", email, labelFont, valueFont, borderColor);

            document.add(table);

            // Footer
            Paragraph footer = new Paragraph(
                    "Humanity (http://www.humanity.com)",
                    footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(30f);
            document.add(footer);

            document.close();

            byte[] bytes = outputStream.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(bytes);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=receipt_" + paymentIntentId + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(bytes.length)
                    .body(resource);

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du reçu", e);
        }
    }

    private void addStyledRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont, BaseColor borderColor) {
        PdfPCell cell1 = new PdfPCell(new Phrase(label, labelFont));
        cell1.setBorderColor(borderColor);
        cell1.setPadding(8f);
        table.addCell(cell1);

        PdfPCell cell2 = new PdfPCell(new Phrase(value, valueFont));
        cell2.setBorderColor(borderColor);
        cell2.setPadding(8f);
        table.addCell(cell2);
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Stripe service is operational");
    }

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
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
import java.net.URL;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/stripe-payments")
@CrossOrigin(origins = "http://localhost:4200")
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

            // Couleurs et polices
            BaseColor headerBg = new BaseColor(44, 62, 80);
            BaseColor borderColor = new BaseColor(189, 195, 199);
            BaseColor labelColor = new BaseColor(52, 73, 94);

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, labelColor);
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, labelColor);
            Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
            Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.GRAY);

            // 1. EN-TÊTE AVEC LOGO ET NOM - VERSION OPTIMISÉE
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setTotalWidth(PageSize.A4.getWidth() - document.leftMargin() - document.rightMargin());
            headerTable.setLockedWidth(true);
            headerTable.setWidths(new float[]{30f, 70f}); // 30% logo, 70% texte
            headerTable.setSpacingAfter(15f);

// Partie Logo (gauche)
            PdfPCell logoCell = new PdfPCell();
            logoCell.setBorder(Rectangle.NO_BORDER);
            logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            logoCell.setPadding(5f);

            try {
                Image logo = Image.getInstance(Paths.get("src/main/resources/static/images/logo.png").toAbsolutePath().toString());
                logo.scaleToFit(70, 70); // Taille réduite pour meilleur équilibre
                logoCell.addElement(logo);
            } catch (Exception e) {
                // Fallback textuel si logo non trouvé
                logoCell.addElement(new Paragraph("HUMANITY",
                        new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.RED)));
            }
            headerTable.addCell(logoCell);

// Partie Texte (droite) - Version non bold
            PdfPCell textCell = new PdfPCell();
            textCell.setBorder(Rectangle.NO_BORDER);
            textCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            textCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

// Police normale (sans bold) avec la couleur #2C3E50
            Font companyFont = new Font(Font.FontFamily.HELVETICA, 16, Font.NORMAL, new BaseColor(44, 62, 80));
            Paragraph orgName = new Paragraph("HUMANITY", companyFont);
            orgName.setAlignment(Element.ALIGN_RIGHT);

            textCell.addElement(orgName);
            headerTable.addCell(textCell);

            document.add(headerTable);

            // 2. TITRE ET MESSAGE
            Paragraph title = new Paragraph("REÇU DE PAIEMENT", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10f);
            document.add(title);

            Paragraph acceptance = new Paragraph("Votre paiement a été accepté",
                    FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.GRAY));
            acceptance.setAlignment(Element.ALIGN_CENTER);
            acceptance.setSpacingAfter(20f);
            document.add(acceptance);

            // 3. TABLEAU DES DÉTAILS
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(90);
            table.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.setWidths(new float[]{3f, 7f});
            table.setSpacingBefore(10f);

            // En-tête du tableau
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

            // Récupération des infos de paiement
            PaymentIntent paymentIntent = stripePaymentService.retrievePaymentIntent(paymentIntentId);
            PaymentMethod paymentMethod = paymentIntent.getPaymentMethod() != null ?
                    PaymentMethod.retrieve(paymentIntent.getPaymentMethod()) : null;

            String brand = paymentMethod != null && paymentMethod.getCard() != null ?
                    paymentMethod.getCard().getBrand() : "Unknown";
            String last4 = paymentMethod != null && paymentMethod.getCard() != null ?
                    paymentMethod.getCard().getLast4() : "****";

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String formattedDate = dateFormat.format(new Date(paymentIntent.getCreated() * 1000L));

            // Ajout des lignes
            addSectionHeader(table, "DÉTAIL DE PAIEMENT", tableHeaderFont, headerBg, borderColor);
            addStyledRow(table, "Date de paiement :", formattedDate, labelFont, valueFont, borderColor);
            addStyledRow(table, "N° de paiement :", paymentIntent.getId(), labelFont, valueFont, borderColor);
            addStyledRow(table, "Code d'autorisation :", "N/A", labelFont, valueFont, borderColor);
            addStyledRow(table, "Méthode de paiement :", brand.toUpperCase(), labelFont, valueFont, borderColor);
            addStyledRow(table, "N° de carte :", "**** **** **** " + last4, labelFont, valueFont, borderColor);

            addSectionHeader(table, "DÉTAIL DE LA COMMANDE", tableHeaderFont, headerBg, borderColor);
            addStyledRow(table, "Identifiant :", paymentIntent.getId() + "_order", labelFont, valueFont, borderColor);
            addStyledRow(table, "Montant (EUR) :", String.format("%.2f EUR", amount), labelFont, valueFont, borderColor);

            addSectionHeader(table, "INFORMATIONS DU CLIENT", tableHeaderFont, headerBg, borderColor);
            addStyledRow(table, "Nom :", customerName, labelFont, valueFont, borderColor);
            addStyledRow(table, "Email :", email, labelFont, valueFont, borderColor);
            if (address != null) {
                addStyledRow(table, "Adresse :", address, labelFont, valueFont, borderColor);
            }
            if (phone != null) {
                addStyledRow(table, "Téléphone :", phone, labelFont, valueFont, borderColor);
            }

            document.add(table);

            // 4. PIED DE PAGE
            Paragraph footer = new Paragraph(
                    "© 2023 Humanity - http://www.humanity.com", footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(30f);
            document.add(footer);

            document.close();

            // Préparation de la réponse
            byte[] bytes = outputStream.toByteArray();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=receipt_" + paymentIntentId + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(bytes.length)
                    .body(new ByteArrayResource(bytes));

        } catch (Exception e) {
            log.error("Erreur génération PDF: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la génération du reçu", e);
        }
    }

    private void addSectionHeader(PdfPTable table, String text, Font font, BaseColor bgColor, BaseColor borderColor) {
        PdfPCell header = new PdfPCell(new Phrase(text, font));
        header.setColspan(2);
        header.setBackgroundColor(bgColor);
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setBorderColor(borderColor);
        table.addCell(header);
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
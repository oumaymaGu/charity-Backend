package tn.example.charity.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.example.charity.Service.PDFService;
@RestController
@RequestMapping("/pdf")
public class PDFController {

    @Autowired
    private PDFService pdfService;

    @GetMapping("/recu/{idLivr}")
    public ResponseEntity<byte[]> getRecuPDF(@PathVariable Long idLivr) {
        try {
            byte[] pdfContent = pdfService.genererRecuPDF(idLivr);  // Passe l'ID au service
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "livraison_recu.pdf");
            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();  // Affiche l'erreur dans les logs
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Erreur lors de la génération du PDF : " + e.getMessage()).getBytes());
        }
    } }
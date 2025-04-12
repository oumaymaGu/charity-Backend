package tn.example.charity.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.example.charity.Service.OcrService;
import tn.example.charity.dto.MedicationInfo;

import java.io.IOException;

@RestController
@RequestMapping("/api/ocr")
@CrossOrigin(origins = "http://localhost:4200") // Autoriser les requêtes CORS
public class OcrController {

    @Autowired
    private OcrService ocrService;

    @PostMapping("/scan-medication")
    public ResponseEntity<?> scanMedication(@RequestParam("image") MultipartFile file) {
        try {
            // Validation basique
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Veuillez sélectionner une image");
            }
            if (!file.getContentType().startsWith("image/")) {
                return ResponseEntity.badRequest().body("Le fichier doit être une image");
            }
            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                        .body("L'image ne doit pas dépasser 5MB");
            }

            MedicationInfo info = ocrService.extractMedicationInfo(file);
            return ResponseEntity.ok(info);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors du traitement de l'image: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erreur d'analyse: " + e.getMessage());
        }
    }
}
package tn.example.charity.Controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.example.charity.Entity.Don;
import tn.example.charity.Entity.TypeDon;
import tn.example.charity.Service.IDonService;
import tn.example.charity.Service.IOcrService;
import tn.example.charity.Service.NotificationService;
import tn.example.charity.dto.MedicationInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/dons")
@CrossOrigin(origins = "http://localhost:4200")
public class DonRestController {

    private final IDonService donService;
    private final NotificationService notificationService;
    private static final String UPLOAD_DIRECTORY = "uploads/";
    private final IOcrService ocrService;

    @PostMapping("/add")
    public ResponseEntity<Don> addDon(@RequestBody Don d) {
        // Log des données reçues
        System.out.println("Don reçu dans addDon: " + d.toString());
        System.out.println("DonorName: " + d.getDonorName() + ", DonorContact: " + d.getDonorContact());

        // Vérifier et définir les valeurs par défaut si nécessaire
        if (d.getDonorContact() == null || d.getDonorContact().trim().isEmpty()) {
            System.out.println("DonorContact est vide, définition de la valeur par défaut.");
            d.setDonorContact("No email provided");
        }
        if (d.getDonorName() == null || d.getDonorName().trim().isEmpty()) {
            System.out.println("DonorName est vide, définition de la valeur par défaut.");
            d.setDonorName("Anonymous");
        }

        Don savedDon = donService.addDon(d);
        notificationService.createAndSendDonNotification(savedDon);
        System.out.println("Don sauvegardé: " + savedDon.toString());
        return ResponseEntity.ok(savedDon);
    }

    @PostMapping("/add-with-medication")
    public ResponseEntity<?> addDonWithMedication(
            @RequestPart("don") Don don,
            @RequestPart(value = "medicationImage", required = false) MultipartFile medicationImage) {
        try {
            // Log des données reçues
            System.out.println("Don reçu dans addDonWithMedication: " + don.toString());
            System.out.println("DonorName: " + don.getDonorName() + ", DonorContact: " + don.getDonorContact());

            String photoUrl = null;
            if (medicationImage != null && !medicationImage.isEmpty()) {
                String imageHash = computeImageHash(medicationImage.getBytes());
                Optional<Don> existingDon = donService.findByImageHash(imageHash);
                if (existingDon.isPresent()) {
                    photoUrl = existingDon.get().getPhotoUrl();
                } else {
                    Path uploadPath = Paths.get(UPLOAD_DIRECTORY);
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }
                    String fileName = System.currentTimeMillis() + "_" + medicationImage.getOriginalFilename();
                    Path filePath = uploadPath.resolve(fileName);
                    Files.write(filePath, medicationImage.getBytes());
                    photoUrl = "http://localhost:8089/uploads/" + fileName;
                }
                don.setPhotoUrl(photoUrl);
                don.setImageHash(imageHash);
            }

            // Vérifier et définir les valeurs par défaut si nécessaire
            if (don.getDonorContact() == null || don.getDonorContact().trim().isEmpty()) {
                System.out.println("DonorContact est vide, définition de la valeur par défaut.");
                don.setDonorContact("No email provided");
            }
            if (don.getDonorName() == null || don.getDonorName().trim().isEmpty()) {
                System.out.println("DonorName est vide, définition de la valeur par défaut.");
                don.setDonorName("Anonymous");
            }

            if (don.getTypeDon() == TypeDon.MATERIEL
                    && "MEDICAMENT".equalsIgnoreCase(don.getCategory())
                    && medicationImage != null
                    && !medicationImage.isEmpty()) {
                MedicationInfo info = ocrService.extractMedicationInfo(medicationImage);
                if (info != null) {
                    if (info.getMedicationName() != null) don.setMedicationName(info.getMedicationName());
                    if (info.getLotNumber() != null) don.setLotNumber(info.getLotNumber());
                    if (info.getExpirationDate() != null) don.setExpirationDate(info.getExpirationDate());
                    if (info.getFabricationDate() != null) don.setFabricationDate(info.getFabricationDate());
                    if (info.getProductCode() != null) don.setProductCode(info.getProductCode());
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Échec de l'extraction OCR : aucune information trouvée.");
                }
            }

            Don savedDon = donService.addDon(don);
            notificationService.createAndSendDonNotification(savedDon);
            System.out.println("Don sauvegardé: " + savedDon.toString());
            return ResponseEntity.ok(savedDon);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'upload de l'image : " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur inattendue : " + e.getMessage());
        }
    }

    private String computeImageHash(byte[] imageBytes) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashBytes = md.digest(imageBytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    @PostMapping("/upload-photo")
    public ResponseEntity<String> uploadPhoto(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Aucun fichier uploadé.");
        }
        try {
            Path uploadPath = Paths.get(UPLOAD_DIRECTORY);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, file.getBytes());
            return ResponseEntity.ok("http://localhost:8089/uploads/" + fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erreur lors de l'upload du fichier.");
        }
    }

    @GetMapping("/all/material")
    public ResponseEntity<List<Don>> getAllMaterialDons() {
        List<Don> materialDons = donService.getAllDon().stream()
                .filter(don -> don.getTypeDon() == TypeDon.MATERIEL)
                .collect(Collectors.toList());
        return ResponseEntity.ok(materialDons);
    }

    @GetMapping("/material/category/{category}")
    public ResponseEntity<List<Don>> getDonsByCategory(@PathVariable("category") String category) {
        List<Don> dons = donService.findByCategory(category.toUpperCase());
        return ResponseEntity.ok(dons);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> removeDon(@PathVariable("id") Long idDon) {
        donService.deleteDon(idDon);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Don> modifyDon(@PathVariable("id") Long idDon, @RequestBody Don d) {
        d.setIdDon(idDon);
        Don updatedDon = donService.modifyDon(d);
        return ResponseEntity.ok(updatedDon);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Don>> getAllDons() {
        List<Don> dons = donService.getAllDon();
        return ResponseEntity.ok(dons);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Don> getDonById(@PathVariable("id") Long idDon) {
        Don don = donService.retrieveallDonbyid(idDon);
        return ResponseEntity.ok(don);
    }
}
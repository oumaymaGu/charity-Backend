package tn.example.charity.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.example.charity.Entity.Refuge;
import tn.example.charity.Service.IRefugeService;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/refuge")
@CrossOrigin(origins = "http://localhost:4200")
public class RefugeRestController {

    IRefugeService refugeService;
    private final Path rootLocation = Paths.get("uploads/images/refuges");

    @PostMapping(value = "/add-ref", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addRefugeWithImage(
            @RequestPart("refuge") String refugeJson,
            @RequestPart("image") MultipartFile imageFile) {
        try {
            if (imageFile.isEmpty()) {
                return ResponseEntity.badRequest().body("Image file is required");
            }

            // Désérialisation JSON -> Refuge
            ObjectMapper mapper = new ObjectMapper();
            Refuge refuge = mapper.readValue(refugeJson, Refuge.class);

            // Sauvegarde Refuge sans image d'abord pour générer l'ID
            Refuge savedRefuge = refugeService.addRefuge(refuge);

            // Upload image et mise à jour du Refuge avec image + détection genre
            Refuge updatedRefuge = refugeService.uploadRefugeImage(savedRefuge.getIdRfg(), imageFile);

            return ResponseEntity.status(HttpStatus.CREATED).body(updatedRefuge);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing request: " + e.getMessage());
        }
    }

    @DeleteMapping("/remove-ref/{ref-id}")
    public void removeass(@PathVariable("ref-id") Long idRfg) {
        refugeService.deleteRefugeById(idRfg);
    }

    @PutMapping("/updateref")
    public Refuge updateref(@RequestBody Refuge refuge) {
        return refugeService.updateRefugeById(refuge);
    }

    @GetMapping("/get-ref/{idref}")
    public Refuge getref(@PathVariable("idref") Long idRfg) {
        return refugeService.getRefugeById(idRfg);
    }

    @GetMapping("/get-all-ref")
    public List<Refuge> getAllref() {
        return refugeService.getAllRefuge();
    }

    @GetMapping("/last")
    public ResponseEntity<Refuge> getLastRefuge() {
        try {
            Refuge lastRefuge = refugeService.getLastRefuge();
            if (lastRefuge == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(lastRefuge);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // ✅ Upload image with Swagger working properly
    @PostMapping(value = "/upload-image/{idref}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequestBody(
            content = @Content(
                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    schema = @Schema(type = "object", format = "binary")
            )
    )
    public ResponseEntity<?> uploadImage(
            @PathVariable("idref") Long idRfg,
            @RequestPart("image") MultipartFile imageFile) {
        try {
            if (imageFile.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please select a file to upload");
            }

            Refuge updatedRefuge = refugeService.uploadRefugeImage(idRfg, imageFile);
            return ResponseEntity.status(HttpStatus.OK).body(updatedRefuge);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/affect-to-logement/{idRefuge}/{idLogement}")
    public ResponseEntity<Refuge> affectToLogement(
            @PathVariable Long idRefuge,
            @PathVariable Long idLogement) {
        try {
            Refuge updatedRefuge = refugeService.affectRefugeToLogement(idRefuge, idLogement);
            return ResponseEntity.ok(updatedRefuge);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/men")
    public List<Refuge> getMaleRefugees() {
        return refugeService.getAllRefuge().stream()
                .filter(r -> "male".equalsIgnoreCase(r.getDetectedGender()))
                .collect(Collectors.toList());
    }

    @GetMapping("/women")
    public List<Refuge> getFemaleRefugees() {
        return refugeService.getAllRefuge().stream()
                .filter(r -> "female".equalsIgnoreCase(r.getDetectedGender()))
                .collect(Collectors.toList());
    }


}

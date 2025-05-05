package tn.example.charity.Controller;

import lombok.AllArgsConstructor;
import org.hibernate.engine.profile.Association;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.example.charity.Entity.Associations;
import tn.example.charity.Entity.Don;
import tn.example.charity.Service.AssociationServiceImpl;
import tn.example.charity.Service.IAssociationService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/association")
@CrossOrigin(origins = "http://localhost:4200")
public class AssociationRestController {

     IAssociationService associationService;
    private static final String UPLOAD_DIRECTORY = "uploads/";

//upload photo
    @PostMapping("/upload-photo")
    public ResponseEntity<String> uploadPhoto(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Aucun fichier uploadé.");
        }

        try {
            // Vérifier et créer le répertoire si nécessaire
            Path uploadPath = Paths.get(UPLOAD_DIRECTORY);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Sauvegarde du fichier sur le serveur
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, file.getBytes());

            return ResponseEntity.ok("http://localhost:8089/uploads/" + fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erreur lors de l'upload du fichier.");
        }
    }




    @PostMapping("/add-ass")
    public Associations addAss(@RequestBody Associations associations) {
        Associations association1= associationService.addAssociations(associations);
        return association1;
    }

    @DeleteMapping("/remove-ass/{ass-id}")
    public void removeass(@PathVariable("ass-id") Long idAss) {

        associationService.deleteAssociations(idAss);
    }

    @PutMapping("/updateAss")
    public Associations modifyAss(@RequestBody Associations associations) {
        Associations associations1 = associationService.updateAssociations(associations);
        return associations1;
    }

    @GetMapping("/get-ass/{idass}")
    public Associations getass(@PathVariable("idass") Long idAss) {
        Associations associations = associationService.getAssociationsById(idAss);
        return associations;
    }

    @GetMapping("/get-all-ass")
    public List<Associations> getAllAss() {
        List<Associations> associations = associationService.getAllAssociations();
        return associations;
    }













}

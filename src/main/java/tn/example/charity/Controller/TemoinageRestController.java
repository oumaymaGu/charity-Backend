package tn.example.charity.Controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.example.charity.Entity.Temoinage;
import tn.example.charity.Entity.TemoinageStatut;
import tn.example.charity.Repository.TemoinageRepository;
import tn.example.charity.Service.BadWordsService;
import tn.example.charity.Service.ITemoinageService;
import tn.example.charity.Service.TranslationService;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/temoinage")
public class TemoinageRestController {

    @Autowired
    private BadWordsService badWordsService;
    @Autowired
    private TemoinageRepository temoinageRepository;
    @Autowired
    private ITemoinageService temoinageService;
    @Autowired
    private final TranslationService translationService;

    private static final String UPLOAD_DIR = "uploads";
    private static final String AUDIO_DIR = UPLOAD_DIR + "/audio";
    private static final String PHOTO_DIR = UPLOAD_DIR + "/photos";

    @PostMapping("/temoinages")
    public ResponseEntity<?> addTemoinage(
            @RequestParam("nom") String nom,
            @RequestParam("description") String description,
            @RequestParam("description_en") String descriptionEn,
            @RequestParam("statut") String statut,
            @RequestParam("typeTemoinage") String typeTemoinage,
            @RequestParam(value = "photoUrl", required = false) String photoUrl,
            @RequestParam("date") String date,
            @RequestParam("localisation") String localisation,
            @RequestParam("note") int note,
            @RequestParam("categorie") String categorie,
            @RequestParam("contact") String contact,
            @RequestParam(value = "audio", required = false) MultipartFile audioFile,
            @RequestParam(value = "photo", required = false) MultipartFile photoFile) {

        // Vérification des mots interdits
        if (badWordsService.containsForbiddenWords(description)) {
            return ResponseEntity.badRequest().body("Contenu inapproprié : mot interdit détecté.");
        }

        Temoinage temoinage = new Temoinage();
        temoinage.setNom(nom);
        temoinage.setDescription(description);
        temoinage.setDescription_en(descriptionEn);
        temoinage.setStatut(TemoinageStatut.valueOf(statut));
        temoinage.setTypeTemoinage(typeTemoinage);
        temoinage.setDate(date);
        temoinage.setLocalisation(localisation);
        temoinage.setNote(note);
        temoinage.setCategorie(categorie);
        temoinage.setContact(contact);

        // Sauvegarde de la photo
        if (photoFile != null && !photoFile.isEmpty()) {
            try {
                String photoFileName = "temoinage_photo_" + System.currentTimeMillis() + ".jpg";
                Path photoPath = Paths.get(PHOTO_DIR, photoFileName);
                Files.createDirectories(photoPath.getParent());
                Files.copy(photoFile.getInputStream(), photoPath, StandardCopyOption.REPLACE_EXISTING);
                temoinage.setPhotoUrl(photoFileName); // Nom du fichier (stocké en base)
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors du stockage de la photo.");
            }
        } else {
            // Si le champ texte photoUrl est présent
            temoinage.setPhotoUrl(photoUrl);
        }

        // Sauvegarde de l'audio
        if (audioFile != null && !audioFile.isEmpty()) {
            try {
                String audioFileName = "temoinage_audio_" + System.currentTimeMillis() + ".wav";
                Path audioPath = Paths.get(AUDIO_DIR, audioFileName);
                Files.createDirectories(audioPath.getParent());
                Files.copy(audioFile.getInputStream(), audioPath, StandardCopyOption.REPLACE_EXISTING);
                temoinage.setAudioPath(audioFileName); // Nom du fichier (stocké en base)
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors du stockage audio.");
            }
        }

        // Enregistrement du témoignage
        Temoinage saved = temoinageRepository.save(temoinage);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/remove-temoinage/{temoinage-id}")
    public void removeTemoinage(@PathVariable("temoinage-id") Long temoinageId) {
        temoinageService.deleteTemoinage(temoinageId);
    }

    @GetMapping("/retrieve-all-temoinages")
    public List<Temoinage> getAllTemoinages() {
        return temoinageService.getAllTemoinages();
    }

    @GetMapping("/get-temoinage/{temoinage-id}")
    public Temoinage getTemoinage(@PathVariable("temoinage-id") Long id) {
        return temoinageService.getTemoinageById(id);
    }

    @PostMapping("/api/temoinages/audio")
    public ResponseEntity<?> uploadAudio(@RequestParam("file") MultipartFile file,
                                         @RequestParam("temoinageId") Long temoinageId) {
        try {
            Optional<Temoinage> optionalTemoinage = temoinageRepository.findById(temoinageId);
            if (optionalTemoinage.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Témoignage non trouvé !");
            }

            String fileName = "temoinage_" + temoinageId + ".wav";
            Path audioPath = Paths.get(AUDIO_DIR, fileName);
            Files.createDirectories(audioPath.getParent());
            Files.copy(file.getInputStream(), audioPath, StandardCopyOption.REPLACE_EXISTING);

            Temoinage temoinage = optionalTemoinage.get();
            temoinage.setAudioPath(fileName);
            temoinageRepository.save(temoinage);

            return ResponseEntity.ok("Fichier audio reçu et stocké !");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors du stockage du fichier.");
        }
    }

    @PutMapping("/temoinage/update-temoinage")
    public ResponseEntity<Temoinage> updateTemoinage(@RequestBody Temoinage temoinage) {
        Temoinage updatedTemoinage = temoinageService.modifyTemoinage(temoinage);
        return ResponseEntity.ok(updatedTemoinage);
    }

    @GetMapping("/public")
    public List<Temoinage> getTemoinagesPublic() {
        return temoinageService.getTemoinagesByStatut(TemoinageStatut.ACCEPTE);
    }

    @GetMapping("/by-statut/{statut}")
    public List<Temoinage> getTemoinagesByStatut(@PathVariable String statut) {
        TemoinageStatut statutEnum = TemoinageStatut.valueOf(statut);
        return temoinageRepository.findByStatut(statutEnum);
    }




    @GetMapping
    public String translate(@RequestParam String word,
                            @RequestParam(defaultValue = "fr") String source,
                            @RequestParam(defaultValue = "en") String target) {
        return translationService.translate(word, source, target);
    }

}

package tn.example.charity.Controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.example.charity.Entity.Temoinage;
import tn.example.charity.Repository.TemoinageRepository;
import tn.example.charity.Service.BadWordsService;
import tn.example.charity.Service.ITemoinageService;

import java.util.List;

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

    @PostMapping("/temoinages")
    public ResponseEntity<?> addTemoinage(@RequestBody Temoinage temoinage) {
        System.out.println("Description reçue : " + temoinage.getDescription());

        if (badWordsService.containsForbiddenWords(temoinage.getDescription())) {
            System.out.println("⚠ Mot interdit détecté !");
            return ResponseEntity.badRequest().body("Contenu inapproprié : mot interdit détecté.");
        }

        Temoinage saved = temoinageRepository.save(temoinage);
        return ResponseEntity.ok(saved);
    }


    @DeleteMapping("/remove-temoinage/{temoinage-id}")
    public void removeTemoinage(@PathVariable("temoinage-id") Long temoinageId) {
        temoinageService.deleteTemoinage(temoinageId);
    }

    @PutMapping("/modify-temoinage")
    public Temoinage modifyTemoinage(@RequestBody Temoinage t) {
        return temoinageService.modifyTemoinage(t);
    }

    @GetMapping("/retrieve-all-temoinages")
    public List<Temoinage> getAllTemoinages() {
        return temoinageService.getAllTemoinages();
    }

    @GetMapping("/get-temoinage/{temoinage-id}")
    public Temoinage getTemoinage(@PathVariable("temoinage-id") Long id) {
        return temoinageService.getTemoinageById(id);
    }

}

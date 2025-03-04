package tn.example.charity.Controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.example.charity.Entity.Temoinage;
import tn.example.charity.Service.ITemoinageService;

import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/temoinage")
public class TemoinageRestController {
    @Autowired
    private ITemoinageService temoinageService;

    @PostMapping("/add-temoinage")
    public Temoinage addTemoinage(@RequestBody Temoinage t) {
        return temoinageService.addTemoinage(t);
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

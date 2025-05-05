package tn.example.charity.Controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.example.charity.Entity.RetraitStock;
import tn.example.charity.Service.IRetraitStockService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/retrait")
@CrossOrigin(origins = "http://localhost:4200")
public class RetraitStockController {

    private final IRetraitStockService retraitStockService;

    @PostMapping("/ajouter/{stockId}/{quantite}")
    public RetraitStock ajouterRetrait(@PathVariable Long stockId, @PathVariable int quantite) {
        return retraitStockService.ajouterRetrait(stockId, quantite);
    }

    @GetMapping("/all")
    public List<RetraitStock> getAllRetraits() {
        return retraitStockService.getAllRetraits();
    }

    @DeleteMapping("/supprimer/{idRetrait}")
    public void supprimerRetrait(@PathVariable Long idRetrait) {
        retraitStockService.supprimerRetrait(idRetrait);
    }
}

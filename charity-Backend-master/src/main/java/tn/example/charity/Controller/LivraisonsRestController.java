package tn.example.charity.Controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.example.charity.Entity.Don;
import tn.example.charity.Entity.Livraisons;
import tn.example.charity.Service.ILivraisonService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/livraison")
public class LivraisonsRestController {
    @Autowired
   private ILivraisonService livraisonService;

    @PostMapping("/add-livraison")
    public Livraisons addLivraison(@RequestBody Livraisons l) {
        Livraisons livraison = livraisonService.addLivraison(l);
        return livraison;

    }

    @DeleteMapping("/remove-livraison/{livraison-id}")
    public void removeLivraison(@PathVariable("livraison-id") Long livraison) {

        livraisonService.deleteLivraison(livraison);
    }

    @PutMapping("/modifyLivraison")
    public Livraisons modifyLivraison(@RequestBody Livraisons l) {
        Livraisons livraison = livraisonService.modifyLivraison(l);
        return livraison;
    }
    @GetMapping("/retrieve-all-Livraison")

    public List< Livraisons> getLivraisons() {
        List<Livraisons> listLivraisons = livraisonService.getAllLivraison();
        return listLivraisons;

    }
    @GetMapping("/get-livraison/{livraison-id}")
    public Livraisons getlivraison(@PathVariable("livraison-id") Long l) {
        Livraisons livraison = livraisonService.retrieveallLivraisonbyid(l);
        return livraison;
    }
}
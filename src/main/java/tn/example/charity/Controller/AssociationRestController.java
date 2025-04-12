package tn.example.charity.Controller;

import lombok.AllArgsConstructor;
import org.hibernate.engine.profile.Association;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.example.charity.Entity.Associations;
import tn.example.charity.Entity.Don;
import tn.example.charity.Service.AssociationServiceImpl;
import tn.example.charity.Service.IAssociationService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/association")
@CrossOrigin(origins = "http://localhost:4200")
public class AssociationRestController {

     IAssociationService associationService;

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

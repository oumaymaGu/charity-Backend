package tn.example.charity.Controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.example.charity.Entity.Associations;
import tn.example.charity.Entity.Refuge;
import tn.example.charity.Service.IAssociationService;
import tn.example.charity.Service.IRefugeService;

import java.util.List;
@RestController
@AllArgsConstructor
@RequestMapping("/refuge")
public class RefugeRestController {

    IRefugeService refugeService;


    @PostMapping("/add-ref")
    public Refuge addref(@RequestBody Refuge refuge) {
        Refuge refuge1= refugeService.addRefuge(refuge);
        return refuge1;
    }

    @DeleteMapping("/remove-ref/{ref-id}")
    public void removeass(@PathVariable("ref-id") Long idRfg) {

        refugeService.deleteRefugeById(idRfg);
    }

    @PutMapping("/updateref")
    public Refuge updateref(@RequestBody Refuge refuge) {
        Refuge refuge1 = refugeService.updateRefugeById(refuge);
        return refuge1;
    }

    @GetMapping("/get-ref/{idref}")
    public Refuge getref(@PathVariable("idref") Long idRfg) {
        Refuge refuge = refugeService.getRefugeById(idRfg);
        return refuge;
    }

    @GetMapping("/get-all-ref")
    public List<Refuge> getAllref() {
        List<Refuge> refuges = refugeService.getAllRefuge();
        return refuges;
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
            // Log the exception for debugging purposes
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}

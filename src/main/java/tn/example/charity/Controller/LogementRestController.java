package tn.example.charity.Controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.example.charity.Entity.Logement;
import tn.example.charity.Entity.Refuge;
import tn.example.charity.Service.ILogementService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/logement")

public class LogementRestController {

     ILogementService logementService;


    @PostMapping("/add-log")
    public Logement addlog(@RequestBody Logement logement) {
        Logement logement1= logementService.addLogement(logement);
        return logement;
    }

    @DeleteMapping("/remove-log/{log-id}")
    public void removelog(@PathVariable("log-id") Long idLog) {

        logementService.deleteLogement(idLog);
    }

    @PutMapping("/updatelog")
    public Logement updatelog(@RequestBody Logement logement) {
        Logement logement1 = logementService.updateLogement(logement);
        return logement1;
    }

    @GetMapping("/get-log/{idlog}")
    public Logement getlogbyid(@PathVariable("idlog") Long idLog) {
        Logement logement = logementService.getLogementById(idLog);
        return logement;
    }

    @GetMapping("/get-all-log")
    public List<Logement> getAlllog() {
        List<Logement> logements = logementService.getAllLogement();
        return logements;
    }



}

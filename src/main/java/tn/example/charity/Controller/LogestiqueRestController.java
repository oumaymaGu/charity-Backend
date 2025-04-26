package tn.example.charity.Controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.example.charity.Entity.Logestique;
import tn.example.charity.Service.ILogestiqueService;

import java.util.List;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@AllArgsConstructor
@RequestMapping("/logestique")

public class LogestiqueRestController {
    @Autowired
    private ILogestiqueService logestiqueService;
    @PostMapping("/add-log")
    public Logestique addlog(@RequestBody  Logestique log) {
        Logestique logestique = logestiqueService.addlogestique(log);
        return logestique;
    }

    @DeleteMapping("/remove-log/{log-id}")
    public void removeLog(@PathVariable("log-id")Long idlogestique) {
        logestiqueService.deleteLogestique(idlogestique);
    }


    @PutMapping("/modifylog")
    public Logestique modifylog(@RequestBody Logestique log) {
        Logestique logestique = logestiqueService.modifylog(log);
        return logestique;
    }
    @GetMapping("/retrieve-all-log")

    public List<Logestique> getlog() {
        List<Logestique> listlogs = logestiqueService.getAlllogs();
        return listlogs;

    }

    @GetMapping("/get-log/{log-id}")
    public  Logestique getlog(@PathVariable("log-id")  Long log) {
        Logestique logestique= logestiqueService.retrievelogbyid(log);
        return logestique;
    }

    @GetMapping("/findByName")
    public List<Logestique> findByRessourceName(@RequestParam String ressourceName) {
        return logestiqueService.retrievelogbyname(ressourceName);
    }
    @PostMapping("/{idlogestique}/assign-to-event/{idEvent}")
    public ResponseEntity<Logestique> assignLogToEvent(
            @PathVariable("idlogestique") Long idlogestique,
            @PathVariable("idEvent") Long idEvent) {
        return ResponseEntity.ok(logestiqueService.assignLogestiqueToEvent(idlogestique, idEvent));
    }



}

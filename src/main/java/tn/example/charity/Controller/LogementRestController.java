    package tn.example.charity.Controller;

    import lombok.AllArgsConstructor;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    import tn.example.charity.Entity.Logement;
    import tn.example.charity.Entity.Refuge;
    import tn.example.charity.Service.ILogementService;
    import tn.example.charity.Service.IRefugeService;

    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;

    @RestController
    @AllArgsConstructor
    @RequestMapping("/logement")
    @CrossOrigin(origins = "http://localhost:4200")


    public class LogementRestController {

         ILogementService logementService;
         IRefugeService refugeService;


        @PostMapping("/add-log")
        public Logement addlog(@RequestBody Logement logement) {
            Logement logement1= logementService.addLogement(logement);
            return logement1;
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
        public ResponseEntity<Logement> getLogementById(@PathVariable("idlog") Long idLog) {
            try {
                Logement logement = logementService.getLogementById(idLog);
                return new ResponseEntity<>(logement, HttpStatus.OK);
            } catch (RuntimeException e) {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); // Vous pouvez ajuster selon vos besoins
            }
        }


        @GetMapping("/get-all-log")
        public List<Logement> getAlllog() {
            List<Logement> logements = logementService.getAllLogement();
            return logements;
        }

        @GetMapping("/search")
        public List<Logement> searchLogements(@RequestParam String nom) {
            return logementService.searchLogementByNom(nom);
        }


        @GetMapping("/get-log-with-refuge/{idlog}")
        public ResponseEntity<?> getLogementWithRefuge(@PathVariable("idlog") Long idLog) {
            Logement logement = logementService.getLogementById(idLog);
            Map<String, Object> response = new HashMap<>();
            response.put("logement", logement);

            // Get the associated refuge if any
            if (logement.getRefuge() != null) {
                Refuge refuge = refugeService.getRefugeById(logement.getRefuge().getIdRfg());
                response.put("refuge", refuge);
            }

            return ResponseEntity.ok(response);
        }



    }

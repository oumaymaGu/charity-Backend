package tn.example.charity.Controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.example.charity.Entity.Don;
import tn.example.charity.Entity.Temoinage;
import tn.example.charity.Service.IDonService;
import tn.example.charity.Service.ITemoinageService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/temoinage")
public class TemoinageRestController {
    @Autowired
    private ITemoinageService temoinageService;

    @PostMapping("/add-temoinage")
    public Temoinage addTemoinage(@RequestBody Temoinage t) {
        Temoinage temoinage = temoinageService.addTemoinage(t);
        return temoinage;
    }

    @DeleteMapping("/remove-temoinage/{temoinage-id}")
    public void removeTemoinage(@PathVariable("temoinage-id")Long temoinage) {

        temoinageService.deleteTemoinage(temoinage);
    }


    @PutMapping("/modifyTemoinage")
    public Temoinage modifyTemoinage(@RequestBody Temoinage t) {
        Temoinage temoinage = temoinageService.modifyTemoinage(t);
        return temoinage;
    }
    @GetMapping("/retrieve-all-Temoinages")

    public List<Temoinage> getTemoinages() {
        List<Temoinage> listTemoinages = temoinageService.getAllTemoingage();
        return listTemoinages;

    }

    @GetMapping("/get-temoinage/{temoinage-id}")
    public Temoinage gettemoinage(@PathVariable("temoinage-id")  Long t) {
        Temoinage temoinage= temoinageService.retrieveallTemoignagebyid(t);
        return temoinage;
    }
}

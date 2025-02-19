package tn.example.charity.Controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.example.charity.Entity.Don;
import tn.example.charity.Entity.SuiviDon;
import tn.example.charity.Service.IDonService;
import tn.example.charity.Service.ISuiviDonService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/suividon")
public class SuiviDonRestController {
    ISuiviDonService suiviDonService;
    @PostMapping("/add-suividon")
    public SuiviDon addSuiviDon(@RequestBody SuiviDon svd) {
        SuiviDon suiviDon = suiviDonService.addSuiviDon(svd);
        return suiviDon;
    }

    @DeleteMapping("/remove-suividon/{suividon-id}")
    public void removeSuiviDon(@PathVariable("suividon-id")Long svd) {

        suiviDonService.deleteSuiviDon(svd);
    }


    @PutMapping("/modifySuiviDon")
    public SuiviDon modifySuiviDon(@RequestBody SuiviDon svd) {
        SuiviDon suiviDon = suiviDonService.modifySuiviDon(svd);
        return suiviDon;
    }
    @GetMapping("/retrieve-all-suiviDons")

    public List<SuiviDon> getSuiviDon() {
        List<SuiviDon> listSuiviDons = suiviDonService.getAllSuiviDon();
        return listSuiviDons;

    }

    @GetMapping("/get-suividon/{suividon-id}")
    public SuiviDon getSuiviDon(@PathVariable("suividon-id") Long svd) {
        SuiviDon suividon= suiviDonService.retrieveallSuiviDonbyid(svd);
        return suividon;
    }
}



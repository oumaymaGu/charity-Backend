package tn.example.charity.Controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.example.charity.Entity.Don;
import tn.example.charity.Service.IDonService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/don")
public class DonRestController {

    IDonService donService;

    @PostMapping("/add-don")
    public Don addDon(@RequestBody Don d) {
        Don don = donService.addDon(d);
        return don;
    }

    @DeleteMapping("/remove-don/{don-id}")
    public void removeDon(@PathVariable("don-id")Long don) {

       donService.deleteDon(don);
    }


    @PutMapping("/modifyDon")
    public Don modifyDon(@RequestBody Don d) {
        Don don = donService.modifyDon(d);
        return don;
    }
    @GetMapping("/retrieve-all-Dons")

    public List<Don> getDon() {
        List<Don> listDons = donService.getAllDon();
        return listDons;

    }

    @GetMapping("/get-don/{don-id}")
    public Don getdon(@PathVariable("don-id")  Long d) {
        Don don= donService.retrieveallDonbyid(d);
        return don;
    }
}

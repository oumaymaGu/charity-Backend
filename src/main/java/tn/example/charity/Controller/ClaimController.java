package tn.example.charity.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.example.charity.Entity.Claim;
import tn.example.charity.Repository.ClaimRepository;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200") // important pour Angular
@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    @Autowired
    private ClaimRepository claimRepository;

    @PostMapping
    public Claim submitClaim(@RequestBody Claim claim) {
        return claimRepository.save(claim);

    }

    @GetMapping
    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }
}
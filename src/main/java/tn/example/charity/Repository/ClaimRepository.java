package tn.example.charity.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import tn.example.charity.Entity.Claim;

public interface ClaimRepository extends JpaRepository<Claim, Long> {}
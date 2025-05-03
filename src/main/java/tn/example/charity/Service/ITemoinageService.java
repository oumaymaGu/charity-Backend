package tn.example.charity.Service;

import tn.example.charity.Entity.Temoinage;
import tn.example.charity.Entity.TemoinageStatut;

import java.util.List;

public interface ITemoinageService {
    Temoinage addTemoinage(Temoinage temoinage);
    void deleteTemoinage(Long idTemoin);
    Temoinage modifyTemoinage(Temoinage temoinage);
    List<Temoinage> getAllTemoinages();
    Temoinage getTemoinageById(Long idTemoin);

    List<Temoinage> getTemoinagesByStatut(TemoinageStatut temoinageStatut);
}
package tn.example.charity.Service;

import tn.example.charity.Entity.Logement;

import java.util.List;

public interface ILogementService {

    Logement addLogement(Logement logement);
    List<Logement> getAllLogement();
    Logement getLogementById(Long idLog);
    Logement updateLogement(Logement logement);
    void deleteLogement(Long idLog);
    List<Logement> searchLogementByNom(String nom);
}

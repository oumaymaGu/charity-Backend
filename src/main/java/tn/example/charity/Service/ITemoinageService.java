package tn.example.charity.Service;

import tn.example.charity.Entity.Livraisons;
import tn.example.charity.Entity.Temoinage;

import java.util.List;

public interface ITemoinageService {
    Temoinage addTemoinage(Temoinage Temoinage);
    void deleteTemoinage(Long idtemoinage);
    Temoinage modifyTemoinage(Temoinage temoinage);
    List<Temoinage> getAllTemoingage();
    List<Temoinage> retreiveallTemoignage(Temoinage temoignage);
    Temoinage retrieveallTemoignagebyid(Long idTemoinage);
}
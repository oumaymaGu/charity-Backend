package tn.example.charity.Service;

import tn.example.charity.Entity.Don;
import tn.example.charity.Entity.Livraisons;

import java.util.List;

public interface ILivraisonService {
    Livraisons addLivraison(Livraisons livraison);
    void deleteLivraison(Long idLivraison);
    Livraisons modifyLivraison(Livraisons livraison);
    List<Livraisons> getAllLivraison();
    List<Livraisons> retreiveallLivraison(Livraisons livraison);
    Livraisons retrieveallLivraisonbyid(Long idLivraison);
}

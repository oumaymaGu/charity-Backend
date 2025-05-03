package tn.example.charity.Service;

import tn.example.charity.Entity.EtatLivraisons;
import tn.example.charity.Entity.Livraisons;

import java.util.List;
import java.util.Optional;

public interface ILivraisonService {
    Livraisons addLivraison(Livraisons livraison);
    void deleteLivraison(Long idLivraison);
    Livraisons modifyLivraison(Livraisons livraison);
    List<Livraisons> getAllLivraison();
    List<Livraisons> retreiveallLivraison(Livraisons livraison);
    Livraisons retrieveallLivraisonbyid(Long idLivraison);

    byte[] generatePDF();


    Livraisons updateDeliveryStatus(Long id, EtatLivraisons newStatus);

    Optional<Livraisons> findById(Long id);


    void save(Livraisons livraison);
    /*String estimateDeliveryTime(double originLat, double originLon, double destLat, double destLon);*/
    void enableTracking(Long livraisonId); // New method
    void updateDriverLocation(Long livraisonId, double latitude, double longitude);
}


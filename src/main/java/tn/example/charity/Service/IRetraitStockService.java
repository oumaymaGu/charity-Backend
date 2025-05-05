package tn.example.charity.Service;

import tn.example.charity.Entity.RetraitStock;

import java.util.List;

public interface IRetraitStockService {
    RetraitStock ajouterRetrait(Long stockId, int quantite);
    List<RetraitStock> getAllRetraits();
    void supprimerRetrait(Long idRetrait);
}

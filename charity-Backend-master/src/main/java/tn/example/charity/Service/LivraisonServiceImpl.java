package tn.example.charity.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.example.charity.Entity.Livraisons;
import tn.example.charity.Repository.LivraisonRepository;

import java.util.List;
@Service
@AllArgsConstructor
@Slf4j
public class LivraisonServiceImpl implements ILivraisonService {
    LivraisonRepository livraisonRepository;
    @Override
    public Livraisons addLivraison(Livraisons livraison) {
        return livraisonRepository.save(livraison);
    }
    @Override
    public void deleteLivraison(Long idLivraison) {
        livraisonRepository.deleteById(idLivraison);

    }
    @Override
    public Livraisons modifyLivraison(Livraisons livraison) {
        return livraisonRepository.save(livraison);
    }
    @Override
    public List<Livraisons> getAllLivraison() {
        return livraisonRepository.findAll();
    }
    @Override
    public List<Livraisons> retreiveallLivraison(Livraisons livraison) {
        return List.of();
    }
    @Override
    public Livraisons retrieveallLivraisonbyid(Long idLivraison) {
        return livraisonRepository.findById(idLivraison).get();
    }
}

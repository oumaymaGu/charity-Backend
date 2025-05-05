package tn.example.charity.Service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.example.charity.Entity.RetraitStock;
import tn.example.charity.Entity.Stock;
import tn.example.charity.Repository.RetraitStockRepository;
import tn.example.charity.Repository.StockRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class RetraitStockServiceImpl implements IRetraitStockService {

    private final RetraitStockRepository retraitStockRepository;
    private final StockRepository stockRepository;

    @Override
    public RetraitStock ajouterRetrait(Long stockId, int quantite) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new IllegalArgumentException("Stock non trouvé"));

        if (quantite <= 0 || quantite > stock.getCapaciteDisponible()) {
            throw new IllegalArgumentException("Quantité invalide pour le retrait !");
        }

        stock.setCapaciteDisponible(stock.getCapaciteDisponible() - quantite);
        stockRepository.save(stock);

        RetraitStock retrait = new RetraitStock();
        retrait.setStock(stock);
        retrait.setQuantite(quantite);
        retrait.setDateRetrait(LocalDateTime.now());

        return retraitStockRepository.save(retrait);
    }

    @Override
    public List<RetraitStock> getAllRetraits() {
        return retraitStockRepository.findAll();
    }

    @Override
    public void supprimerRetrait(Long idRetrait) {
        retraitStockRepository.deleteById(idRetrait);
    }
}

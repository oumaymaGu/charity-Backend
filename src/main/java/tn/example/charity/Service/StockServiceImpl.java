package tn.example.charity.Service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.example.charity.Entity.Associations;
import tn.example.charity.Entity.Stock;
import tn.example.charity.Repository.AssociationsRepository;
import tn.example.charity.Repository.StockRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class StockServiceImpl implements IStockService {
    StockRepository stockRepository;
    AssociationsRepository associationsRepository;

    @Override
    public Stock addStock(Stock stock) {
        stock.setDateCreation(LocalDateTime.now());

        // S'assurer que l'association est bien récupérée depuis la DB
        if (stock.getAssociations() != null && stock.getAssociations().getIdAss() != 0) {
            Associations assoc = associationsRepository.findById(stock.getAssociations().getIdAss())
                    .orElseThrow(() -> new IllegalArgumentException("Association non trouvée"));
            stock.setAssociations(assoc);

            // Vérifie s'il existe déjà un stock avec même type + lieu + association
            Optional<Stock> existingStockOpt = stockRepository.findByTypeStockAndLieuAndAssociations(
                    stock.getTypeStock(), stock.getLieu(), assoc
            );

            if (existingStockOpt.isPresent()) {
                Stock existingStock = existingStockOpt.get();
                existingStock.setCapaciteTotale(existingStock.getCapaciteTotale() + stock.getCapaciteTotale());
                existingStock.setCapaciteDisponible(existingStock.getCapaciteDisponible() + stock.getCapaciteTotale());
                return stockRepository.save(existingStock);
            }
        }

        // Sinon, c’est un nouveau stock
        stock.setCapaciteDisponible(stock.getCapaciteTotale());
        return stockRepository.save(stock);
    }




    @Override
    public Stock updateStock(Stock stock) {
        if (stock.getCapaciteDisponible() > stock.getCapaciteTotale()) {
            throw new IllegalArgumentException("La capacité disponible ne peut pas dépasser la capacité totale !");
        }
        return stockRepository.save(stock);
    }


    public void deleteStock(Long idStock) {
        stockRepository.deleteById(idStock);

    }

    public Stock getStockById(Long idStock) {
        Stock stock = stockRepository.findById(idStock).get();
        return stock;
    }

    public List<Stock> getAllStock() {
        List<Stock> stocks = stockRepository.findAll();
        return stocks;
    }

    @Override
    public Stock affecterAssociationAStock(Long idStock, Long idAss) {
        Stock stock = stockRepository.findById(idStock)
                .orElseThrow(() -> new IllegalArgumentException("Stock non trouvé"));
        Associations association = associationsRepository.findById(idAss)
                .orElseThrow(() -> new IllegalArgumentException("Association non trouvée"));

        stock.setAssociations(association);
        return stockRepository.save(stock);
    }



}

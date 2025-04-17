package tn.example.charity.Service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.example.charity.Entity.Stock;
import tn.example.charity.Repository.StockRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class StockServiceImpl implements IStockService {
    StockRepository stockRepository;

    public Stock addStock(Stock stock) {
        return stockRepository.save(stock);
    }

    public Stock updateStock(Stock stock) {
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
}

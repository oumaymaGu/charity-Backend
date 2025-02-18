package tn.example.charity.Service;

import tn.example.charity.Entity.Stock;

import java.util.List;

public interface IStockService {
    Stock addStock(Stock stock);
    Stock updateStock(Stock stock);
    void deleteStock(Long idStock);
    Stock getStockById(Long idStock);
    List<Stock> getAllStock();
}

package tn.example.charity.Controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.example.charity.Entity.Associations;
import tn.example.charity.Entity.Stock;
import tn.example.charity.Service.IStockService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/stock")
public class StockRestController {

    IStockService stockService;


    @PostMapping("/add-stock")
    public Stock addstock(@RequestBody Stock stock) {
        Stock stock1= stockService.addStock(stock);
        return stock1;
    }

    @DeleteMapping("/remove-stock/{stock-id}")
    public void removestock(@PathVariable("stock-id") Long idStock) {

        stockService.deleteStock(idStock);
    }

    @PutMapping("/update-stock")
    public Stock updatestock(@RequestBody Stock stock) {
        Stock stock1 = stockService.updateStock(stock);
        return stock1;
    }

    @GetMapping("/get-stock/{idstock}")
    public Stock getstock(@PathVariable("idstock") Long idStock) {
        Stock stock = stockService.getStockById(idStock);
        return stock;
    }

    @GetMapping("/get-all-stock")
    public List<Stock> getAllstock() {
        List<Stock> stocks = stockService.getAllStock();
        return stocks;
    }
}

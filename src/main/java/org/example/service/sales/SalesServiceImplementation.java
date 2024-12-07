package org.example.service.sales;

import org.example.model.Sales;
import org.example.repository.sales.SalesRepository;

import java.util.List;

public class SalesServiceImplementation implements SalesService{
    private final SalesRepository salesRepository ;

    public SalesServiceImplementation(SalesRepository salesRepository) {
        this.salesRepository = salesRepository;
    }

    @Override
    public List<Sales> getAllSales() {
        return salesRepository.findAll();
    }

    @Override
    public boolean save(Sales sales) {
        return salesRepository.save(sales);
    }

    @Override
    public List<Sales> findSalesByUserID(Long id) {
        return salesRepository.findSalesByUserID(id);
    }
}

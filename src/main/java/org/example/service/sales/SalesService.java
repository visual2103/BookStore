package org.example.service.sales;

import org.example.model.Sales;

import java.util.List;

public interface SalesService {
    List<Sales> getAllSales();
    boolean save (Sales sales) ;
    List<Sales> findSalesByUserID(Long id);
}

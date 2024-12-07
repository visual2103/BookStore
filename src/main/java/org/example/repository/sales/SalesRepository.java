package org.example.repository.sales;

import org.example.model.Book;
import org.example.model.Sales;

import java.util.List;

public interface SalesRepository {
    boolean save(Sales sales) ;
    List<Sales> findAll() ;
    List<Sales> findSalesByUserID(Long id);
}

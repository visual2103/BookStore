package org.example.model.builder;

import org.example.model.Book;
import org.example.model.Sales;

import java.time.LocalDate;

public class SalesBuilder {
    private Sales sales;
    public SalesBuilder(){sales = new Sales();}

    public SalesBuilder setId (Long id){
        sales.setId(id);
        return this;
    }

    public SalesBuilder setBookTitle(String title){
        sales.setBookTitle(title);
        return this ;
    }

    public SalesBuilder setBookAuthor(String author){
        sales.setBookAuthor(author);
        return this;
    }

    public SalesBuilder setQuantity(int quantity){
        sales.setQuantity(quantity);
        return this;
    }

    public SalesBuilder setTotalPrice (float totalPrice){
        sales.setTotalPrice(totalPrice);
        return this ;
    }

    public SalesBuilder setSaleDate(LocalDate saleDate){
        sales.setSaleDate(saleDate);
        return this;
    }

    public SalesBuilder setEmployeeId(Long employeeId) {
        sales.setEmployeeid(employeeId);
        return this;
    }



    public Sales build(){

        return sales;
    }
}

package org.example.view.model.builder;

import org.example.view.model.SalesDTO;
import java.time.LocalDate;

public class SalesDTOBuilder {
    private SalesDTO salesDTO;

    public SalesDTOBuilder() {
        salesDTO = new SalesDTO();
    }

    public SalesDTOBuilder setTitle(String title) {
        salesDTO.setTitle(title);
        return this;
    }
    public SalesDTOBuilder setAuthor(String author) {
        salesDTO.setAuthor(author);
        return this;
    }

    public SalesDTOBuilder setQuantity(int quantity) {
        salesDTO.setQuantity(quantity);
        return this;
    }

    public SalesDTOBuilder setTotalPrice(float totalPrice) {
        salesDTO.setTotalPrice(totalPrice);
        return this;
    }

    public SalesDTOBuilder setSaleDate(LocalDate saleDate) {
        salesDTO.setSaleDate(saleDate);
        return this;
    }
    public SalesDTOBuilder setEmployeeId(Long id) {
        salesDTO.setEmployeeId(id);
        return this;
    }


    public SalesDTO build() {
        return salesDTO;
    }
}

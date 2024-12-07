package org.example.mapper;

import org.example.model.Sales;
import org.example.model.builder.SalesBuilder;
import org.example.view.model.SalesDTO;
import org.example.view.model.builder.SalesDTOBuilder;

import java.time.LocalDate;
import java.util.List;

public class SalesMapper {

    // Mapare între Sale -> SalesDTO
    public static SalesDTO convertSaleToSalesDTO(Sales sale) {
        return new SalesDTOBuilder()
                .setTitle(sale.getBookTitle())
                .setAuthor(sale.getBookAuthor())
                .setQuantity(sale.getQuantity())
                .setTotalPrice(sale.getTotalPrice())
                .setSaleDate(sale.getSaleDate())
                .setEmployeeId(sale.getEmployeeid())
                .build();
    }

    public static Sales convertSalesDTOToSale(SalesDTO salesDTO) {
        return new SalesBuilder()
                .setBookTitle(salesDTO.getTitle())
                .setBookAuthor(salesDTO.getAuthor())
                .setQuantity(salesDTO.getQuantity())
                .setTotalPrice(salesDTO.getTotalPrice())
                .setSaleDate(salesDTO.getSaleDate())
                .setEmployeeId(salesDTO.getEmployeeId())
                .build();
    }

    public static List<SalesDTO> convertSaleListToSalesDTOList(List<Sales> sales) {
        return sales.parallelStream()
                .map(SalesMapper::convertSaleToSalesDTO)
                .toList();
    }

    public static List<Sales> convertSalesDTOListToSaleList(List<SalesDTO> salesDTOs) {
        return salesDTOs.parallelStream()
                .map(SalesMapper::convertSalesDTOToSale)
                .toList();
    }
}

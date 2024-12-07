package org.example;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.view.SalesView;
import org.example.view.model.SalesDTO;
import org.example.view.model.builder.SalesDTOBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Creează date de probă
        List<SalesDTO> salesList = createSampleData();

        // Inițializează și afișează SalesView
        new SalesView(primaryStage, salesList);
    }

    private List<SalesDTO> createSampleData() {
        List<SalesDTO> salesList = new ArrayList<>();

        SalesDTO sale1 = new SalesDTOBuilder()
                .setTitle("Titlu 1")
                .setAuthor("Autor 1")
                .setQuantity(5)
                .setTotalPrice(99.99f)
                .setSaleDate(LocalDate.of(2023, 10, 1))
                .build();

        SalesDTO sale2 = new SalesDTOBuilder()
                .setTitle("Titlu 2")
                .setAuthor("Autor 2")
                .setQuantity(3)
                .setTotalPrice(59.99f)
                .setSaleDate(LocalDate.of(2023, 10, 5))
                .build();

        SalesDTO sale3 = new SalesDTOBuilder()
                .setTitle("Titlu 3")
                .setAuthor("Autor 3")
                .setQuantity(10)
                .setTotalPrice(199.99f)
                .setSaleDate(LocalDate.of(2023, 10, 10))
                .build();

        salesList.add(sale1);
        salesList.add(sale2);
        salesList.add(sale3);

        return salesList;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

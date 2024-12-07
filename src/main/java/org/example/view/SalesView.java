package org.example.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import org.example.view.model.BookDTO;
import org.example.view.model.SalesDTO;


import java.time.LocalDate;
import java.util.List;

public class SalesView {

    private TableView<SalesDTO> salesTableView;
    private final ObservableList<SalesDTO> salesObservableList;
    private Button libraryButton ;

    private Button logoutButton;
    private GridPane gridPane ;

    public SalesView(Stage primaryStage, List<SalesDTO> sales) {
        primaryStage.setTitle("Sales");
        gridPane = new GridPane();
        initializeGridPage(gridPane);
        Scene scene = new Scene(gridPane, 720, 480);
        primaryStage.setScene(scene);

        salesObservableList = FXCollections.observableArrayList(sales);
        initTableView(gridPane);
        initSaveOptions(gridPane);
        primaryStage.show();
    }

    private void initializeGridPage(GridPane gridPane) {
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(25, 25, 25, 25));
    }

    private void initTableView(GridPane gridPane) {
        salesTableView = new TableView<>();
        salesTableView.setPrefWidth(700); // Setează o lățime mai mare
        salesTableView.setPrefHeight(500); // Setează o înălțime mai mare
        salesTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // Ajustează automat lățimile coloanelor
        salesTableView.setPlaceholder(new Label("No sales to display"));

        TableColumn<SalesDTO, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<SalesDTO, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));

        TableColumn<SalesDTO, Integer> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<SalesDTO, Float> totalPriceColumn = new TableColumn<>("Total Price");
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        TableColumn<SalesDTO, LocalDate> saleDateColumn = new TableColumn<>("Sale Date");
        saleDateColumn.setCellValueFactory(new PropertyValueFactory<>("saleDate"));

        salesTableView.getColumns().addAll(titleColumn, authorColumn, quantityColumn, totalPriceColumn, saleDateColumn);
        salesTableView.setItems(salesObservableList);
        gridPane.add(salesTableView, 0, 1, 6, 1);
    }

    public void initSaveOptions (GridPane gridPane){
        libraryButton = new Button("LIBRARY") ;
        gridPane.add(libraryButton , 7,3);
        logoutButton = new Button("Logout");
        gridPane.add(logoutButton, 8, 5);
    }

    public void LibraryButtonListener(EventHandler<ActionEvent> libraryButtonListener ){
        libraryButton.setOnAction(libraryButtonListener) ;
    }


    public void addLogoutButtonListener(EventHandler<ActionEvent> logoutListener) {
        logoutButton.setOnAction(logoutListener);
    }

    public void addDisplayAlertMessage(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void addBooksToObservableList(SalesDTO salesDTO){
        this.salesObservableList.add(salesDTO) ;
    }

    public void removeBooksToObservableList(SalesDTO salesDTO){
        this.salesObservableList.remove(salesDTO) ;
    }
    public Parent getRoot() {
        return gridPane;
    }

    public TableView<SalesDTO> getSalesTableView() {
        return salesTableView;
    }



}

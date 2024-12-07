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
import org.example.model.Book;
import org.example.view.model.BookDTO;

import javafx.event.*;

import java.util.List;


public class BookView {
    private TableView bookTableView ; //tabelul in care adaugam toate cartile ;
    private GridPane gridPane ;
    private final ObservableList<BookDTO> booksObservabelList ;
    private TextField titleTextField ;
    private TextField authorTextField ;
    private TextField quantityTextField ;
    private TextField priceTextField ;
    private Label titleLabel ;
    private Label authorLabel ;
    private Label quantityLabel ;
    private Label priceLabel ;
    private Button saveButton ;
    private Button deleteButton ;
    private Button sellButton ;
    private Button salesButton ;
    private Button adminButton ;
    private Button logoutButton;
    public BookView(Stage primaryStage , List<BookDTO> books) {
        primaryStage.setTitle("Library");
        gridPane = new GridPane();
        initializeGridPage(gridPane);

        Scene scene = new Scene(gridPane, 720, 480);
        primaryStage.setScene(scene);
        booksObservabelList = FXCollections.observableArrayList(books) ;
        initTableView (gridPane) ;
        initSaveOptions(gridPane) ;
        primaryStage.show() ;
    }

    private void initializeGridPage(GridPane gridPane) {
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(25,25,25,25));
    }
    private void initTableView(GridPane gridPane){
        bookTableView = new TableView<BookDTO>() ;
        bookTableView.setPlaceholder(new Label(("No books to display")));

        TableColumn<BookDTO , String> titleColumn = new TableColumn<BookDTO ,String>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<BookDTO,String> authorColumn = new TableColumn<BookDTO ,String>("Author");
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));

        TableColumn<BookDTO , Integer> quantityColumn = new TableColumn<BookDTO ,Integer>("Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<BookDTO,Float> priceColumn = new TableColumn<BookDTO ,Float>("Price");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        bookTableView.getColumns().addAll(titleColumn,authorColumn , quantityColumn , priceColumn) ;
        bookTableView.setItems(booksObservabelList); //orice modificare in lista va modifica si in interfata
        gridPane.add(bookTableView,0,0,5,1);
    }

    public void initSaveOptions (GridPane gridPane){
        titleLabel = new Label("Title") ;
        gridPane.add(titleLabel,1,1) ;

        titleTextField = new TextField();
        gridPane.add(titleTextField,2,1);

        authorLabel = new Label("Author") ;
        gridPane.add(authorLabel,3,1);

        authorTextField = new TextField();
        gridPane.add(authorTextField,4,1);

        quantityLabel = new Label("Stock") ;
        gridPane.add(quantityLabel,1,2) ;

        quantityTextField = new TextField();
        gridPane.add(quantityTextField,2,2);

        priceLabel = new Label("Price") ;
        gridPane.add(priceLabel,3,2);

        priceTextField = new TextField();
        gridPane.add(priceTextField,4,2);

        saveButton = new Button("Save") ;
        gridPane.add(saveButton , 5,1);

        deleteButton = new Button("Delete") ;
        gridPane.add(deleteButton , 6,1);

        sellButton = new Button("Sell") ;
        gridPane.add(sellButton , 7,1);

        salesButton = new Button("SALES");
        gridPane.add(salesButton, 5, 2);
        logoutButton = new Button("Logout");
        gridPane.add(logoutButton, 4, 3);
        adminButton = new Button("ADMIN") ;
        gridPane.add(adminButton , 6,2);
    }

    public void addSaveButtonListener(EventHandler<ActionEvent> saveButtonListener){
        saveButton.setOnAction(saveButtonListener) ;
    }
    public void addDeleteButtonListener(EventHandler<ActionEvent> deleteButtonListener){
        deleteButton.setOnAction(deleteButtonListener) ;
    }

    public void addSellButtonListener(EventHandler<ActionEvent> sellButtonListener){
        sellButton.setOnAction(sellButtonListener) ;
    }
     public void addSalesButtonListener(EventHandler<ActionEvent> salesButtonListener){
        salesButton.setOnAction(salesButtonListener) ;
    }

    public void adminButtonlistener(EventHandler<ActionEvent> adminButtonListener ){
        adminButton.setOnAction(adminButtonListener) ;
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

    public String getTitle() {
        return titleTextField.getText();
    }

    public String getAuthor() {
        return authorTextField.getText();
    }

    public String getQuantity() {
        return quantityTextField.getText();
    }

    public String getPrice() {
        return priceTextField.getText();
    }




    public void addBooksToObservableList(BookDTO bookDTO){
        this.booksObservabelList.add(bookDTO) ; //nu schimbam referinta pt a nu pierde legatura cu tabelul
    }

    public void removeBooksToObservableList(BookDTO bookDTO){
        this.booksObservabelList.remove(bookDTO) ; //nu schimbam referinta pt a nu pierde legatura cu tabelul
    }

    public void refreshBookInObservableList(BookDTO updatedBookDTO){
            for (int i = 0; i < booksObservabelList.size(); i++) {
                BookDTO bookDTO = booksObservabelList.get(i);
                if (bookDTO.getTitle().equals(updatedBookDTO.getTitle())) { // s ar putea sa fie necesar ID fiindca e primary key si e 100% unic ????
                    // Actualizăm cartea din listă
                    booksObservabelList.set(i, updatedBookDTO);
                    break;
                }
            }
            // update view
            bookTableView.refresh();
    }
    public void updateBookInObservableList(BookDTO updatedBookDTO) {
        for (int i = 0; i < booksObservabelList.size(); i++) {
            BookDTO bookDTO = booksObservabelList.get(i);
            if (bookDTO.getTitle().equals(updatedBookDTO.getTitle()) &&
                    bookDTO.getAuthor().equals(updatedBookDTO.getAuthor()) &&
                    bookDTO.getPrice().equals(updatedBookDTO.getPrice())) {
                booksObservabelList.set(i, updatedBookDTO);
                bookTableView.refresh();
                return;
            }
        }
    }

    public Parent getRoot() {
        System.out.println("BookView: getRoot() called. gridPane is " + (gridPane != null ? "not null" : "null"));
        return gridPane;
    }
    public TableView getBookTableView(){
        return bookTableView ;
    }

    public boolean updateBookQuantityIfExists(BookDTO bookDTO) {
        for (BookDTO existingBook : booksObservabelList){
            if (existingBook.getTitle().equals(bookDTO.getTitle()) &&
                    existingBook.getAuthor().equals(bookDTO.getAuthor()) &&
                    existingBook.getPrice().equals(bookDTO.getPrice()) ){
                //update quantity
                existingBook.setQuantity(existingBook.getQuantity() + bookDTO.getQuantity());
                bookTableView.refresh();
                return true;
            }
        }
        return false;
        }
    public void hideAdminButton() {
        adminButton.setVisible(false);
    }

    public void hideSaveButton() {
        saveButton.setVisible(false);
    }

    public void hideDeleteButton() {
        deleteButton.setVisible(false);
    }

    public void hideSalesButton() {
        salesButton.setVisible(false);
    }
}


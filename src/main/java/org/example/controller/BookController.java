package org.example.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import org.example.database.DataBaseConnectionFactory;
import org.example.mapper.BookMapper;
import org.example.mapper.SalesMapper;
import org.example.mapper.UsersMapper;
import org.example.model.CurrentUser;
import org.example.model.User;
import org.example.model.validator.Notification;
import org.example.repository.security.RightsRolesRepository;
import org.example.repository.security.RightsRolesRepositoryMySQL;
import org.example.repository.user.UserRepository;
import org.example.repository.user.UserRepositoryMySQL;
import org.example.service.book.BookService;
import org.example.service.sales.SalesService;
import org.example.service.security.RightsRolesService;
import org.example.service.security.RightsRolesServiceImplementation;
import org.example.service.user.AuthenticationService;
import org.example.service.user.AuthenticationServiceMySQL;
import org.example.service.user.UserService;
import org.example.service.user.UserServiceImplementation;
import org.example.view.AdminView;
import org.example.view.BookView;
import org.example.view.LoginView;
import org.example.view.SalesView;
import org.example.view.model.BookDTO;
import org.example.view.model.SalesDTO;
import org.example.view.model.UserDTO;
import org.example.view.model.builder.BookDTOBuilder;
import org.example.view.model.builder.SalesDTOBuilder;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

public class BookController {
    private final BookView bookView;
    private final BookService bookService ;
    private final SalesService salesService ;
    private final SalesView salesView;
    private final RightsRolesService rightsRolesService ;
    private final AuthenticationService authenticationService ;
    private final UserService userService ;
    public BookController(BookView bookView , BookService bookService , SalesView salesView, RightsRolesService rightsRolesService , UserService userService  ,SalesService salesService , AuthenticationService authenticationService){
        this.bookView = bookView ;
        this.salesView = salesView ;
        this.bookService = bookService ;
        this.salesService = salesService ;
        this.userService = userService ;
        this.authenticationService = authenticationService ;
        this.rightsRolesService = rightsRolesService ;
        this.bookView.addSaveButtonListener(new SaveButtonListener() );
        this.bookView.addDeleteButtonListener(new DeleteButtonListener() );
        this.bookView.addSellButtonListener(new SellButtonListener()) ;
        this.bookView.addSalesButtonListener(new SalesButtonListener());
        this.bookView.addLogoutButtonListener(new LogoutButtonListener());
        this.bookView.adminButtonlistener(new AdminButtonListener());
    }

    private class SaveButtonListener implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event) {
            String title = bookView.getTitle() ;
            String author = bookView.getAuthor();
            String quantityText = bookView.getQuantity();
            String priceText = bookView.getPrice();
            int quantity = 0;
            float price = 0.0f;

            try {
                quantity = Integer.parseInt(quantityText);
            } catch (NumberFormatException e) {
                bookView.addDisplayAlertMessage("Error", "Invalid Quantity", "Please enter a valid integer for quantity.");
                return;
            }

            try {
                price = Float.parseFloat(priceText);
            } catch (NumberFormatException e) {
                bookView.addDisplayAlertMessage("Error", "Invalid Price", "Please enter a valid number for price.");
                return;
            }
            if (title.isEmpty() || author.isEmpty() ){
                bookView.addDisplayAlertMessage("Save Error" , "Problem at Input fields","Can't have an empty Title or Author fiels !");
            } else{
                BookDTO bookDTO = new BookDTOBuilder()
                        .setTitle(title)
                        .setAuthor(author)
                        .setQuantity(quantity)
                        .setPrice(price)
                        .build() ;
                boolean savedBook = bookService.save(BookMapper.convertBookDTOToBook(bookDTO));
                if (savedBook) {
                    // already exists in observable list
                    boolean updated = bookView.updateBookQuantityIfExists(bookDTO);

                    if (!updated) {
                        bookView.addBooksToObservableList(bookDTO);
                    }
                    bookView.addDisplayAlertMessage("Success", "Book Saved", "The book was successfully added or updated.");
                } else {
                    bookView.addDisplayAlertMessage("Error", "Save Failed", "There was a problem saving the book. Please try again.");
                }
            }

        }
    }

    private class DeleteButtonListener implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event) {
            BookDTO bookDTO = (BookDTO) bookView.getBookTableView().getSelectionModel().getSelectedItem();
            if (bookDTO != null) {
                boolean deletionSuccessful = bookService.delete(BookMapper.convertBookDTOToBook(bookDTO));

                if (deletionSuccessful) {
                    bookView.addDisplayAlertMessage("Delete Successful", "Book Deleted", "The book was successfully deleted.");
                    bookView.removeBooksToObservableList(bookDTO);
                } else {
                    bookView.addDisplayAlertMessage("Delete Error", "Problem at Deletion", "The book could not be deleted.");
                }
            } else {
                bookView.addDisplayAlertMessage("Delete Error", "No Selection", "Please select a book to delete.");
            }
        }
    }

    private class SellButtonListener implements  EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event) {

            BookDTO selectedBookDTO = (BookDTO) bookView.getBookTableView().getSelectionModel().getSelectedItem();
            if (selectedBookDTO == null) {
                bookView.addDisplayAlertMessage("Sell Error", "No Selection", "Please select a book to sell.");
                return;
            }
//            Notification<User> currentUserNotification = authenticationService.getLoggedInUser();
//            if (currentUserNotification.hasErrors() || currentUserNotification.getResult() == null) {
//                System.out.println("Error retrieving logged-in user: " + currentUserNotification.getFormattedErrors());
//                bookView.addDisplayAlertMessage("Error", "Authentication Error", "Unable to retrieve logged-in user.");
//                return;
//            }
//            User currentUser = currentUserNotification.getResult();
//            System.out.println("Logged-in user retrieved: " + currentUser.getUsername());

            User currentUser = CurrentUser.get();
            if (currentUser == null) {
                bookView.addDisplayAlertMessage("Error", "Authentication Error", "No user is logged in.");
                return;
            }
            System.out.println("Current user: " + currentUser.getUsername());

            // iau cantittaea de vandut din quantityTextField STOCK
            String quantityText = bookView.getQuantity();
            int quantityToSell;
            try {
                quantityToSell = Integer.parseInt(quantityText);
            } catch (NumberFormatException e) {
                bookView.addDisplayAlertMessage("Error", "Invalid Quantity", "Please enter a valid integer for quantity.");
                return;
            }

            if (quantityToSell <= 0) {
                bookView.addDisplayAlertMessage("Error", "Invalid Quantity", "Quantity must be greater than zero.");
                return;
            }

            // suficient stoc
            if (quantityToSell > selectedBookDTO.getQuantity()) {
                bookView.addDisplayAlertMessage("Error", "Insufficient Stock", "Not enough books in stock.");
                return;
            }

            // prețul total
            float totalPrice = quantityToSell * selectedBookDTO.getPrice();

            // Înregistrează vânzarea
            SalesDTO salesDTO = new SalesDTOBuilder()
                    .setTitle(selectedBookDTO.getTitle())
                    .setAuthor(selectedBookDTO.getAuthor())
                    .setQuantity(quantityToSell)
                    .setTotalPrice(totalPrice)
                    .setSaleDate(LocalDate.now())
                    .setEmployeeId(currentUser.getId())
                    .build();

            boolean saleRecorded = salesService.save(SalesMapper.convertSalesDTOToSale(salesDTO));

            if (!saleRecorded) {
                bookView.addDisplayAlertMessage("Error", "Sale Failed", "Could not record the sale.");
                return;
            }

            // Actualizează cantitatea în tabela book
            selectedBookDTO.setQuantity(selectedBookDTO.getQuantity() - quantityToSell);
            System.out.println("Book ID: " + selectedBookDTO.getId());

            boolean updated = bookService.update(BookMapper.convertBookDTOToBook(selectedBookDTO));

            if (!updated) {
                bookView.addDisplayAlertMessage("Error", "Update Failed", "Could not update book quantity.");
                return;
            }

            // Actualizează interfața
            bookView.refreshBookInObservableList(selectedBookDTO);
            bookView.addDisplayAlertMessage("Success", "Sale Recorded", "The sale has been recorded successfully.");

        }
    }


    private class SalesButtonListener implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            Stage salesStage = new Stage();

//            // lista provizorie cat sa vad interfata -> trebuie modificat URGENT pentru ca eu vreau sa vad vanzarea care s a inregistrat
//            List<SalesDTO> salesList = List.of(
//                    new SalesDTOBuilder()
//                            .setTitle("Book 1")
//                            .setAuthor("Author 1")
//                            .setQuantity(5)
//                            .setTotalPrice(50.0f)
//                            .setSaleDate(LocalDate.now())
//                            .build(),
//                    new SalesDTOBuilder()
//                            .setTitle("Book 2")
//                            .setAuthor("Author 2")
//                            .setQuantity(3)
//                            .setTotalPrice(30.0f)
//                            .setSaleDate(LocalDate.now())
//                            .build(),
//                    new SalesDTOBuilder()
//                            .setTitle("Book 3")
//                            .setAuthor("Author 3")
//                            .setQuantity(7)
//                            .setTotalPrice(70.0f)
//                            .setSaleDate(LocalDate.now())
//                            .build()
//            );
            List<SalesDTO> salesList = SalesMapper.convertSaleListToSalesDTOList(salesService.getAllSales());

            SalesView salesView = new SalesView(salesStage, salesList);
            new SalesController(salesView, salesService ,bookService , authenticationService,userService,rightsRolesService); // SalesService nu este necesar momentan
        }
    }

    private class LogoutButtonListener implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            authenticationService.logout();
            CurrentUser.clear();

            Stage stage = (Stage) bookView.getRoot().getScene().getWindow();

            LoginView loginView = new LoginView(stage);

            // necessary services
            Connection connection = DataBaseConnectionFactory.getConnectionWrapper(false).getConnection();

            RightsRolesRepository rightsRolesRepository = new RightsRolesRepositoryMySQL(connection);
            RightsRolesService rightsRolesService = new RightsRolesServiceImplementation(rightsRolesRepository);
            UserRepository userRepository = new UserRepositoryMySQL(connection, rightsRolesRepository);
            UserService userService = new UserServiceImplementation(userRepository, rightsRolesRepository);
            AuthenticationService authenticationService = new AuthenticationServiceMySQL(userRepository, rightsRolesRepository);

            new LoginController(loginView, authenticationService, rightsRolesService);

        }
    }

    private class AdminButtonListener implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            // Verifică dacă userService și rightsRolesService nu sunt null
            if (userService == null || rightsRolesService == null) {
                bookView.addDisplayAlertMessage("Error", "Access Denied", "Nu ai permisiunea de a accesa panoul de administrare.");
                return;
            }

            // Obține Stage-ul curent
            Stage stage = (Stage) bookView.getRoot().getScene().getWindow();

            // Obține lista de utilizatori
            Notification<List<User>> notification = userService.getAllUsers();
            if (notification.hasErrors()) {
                bookView.addDisplayAlertMessage("Error", "Failed to Retrieve Users", notification.getFormattedErrors());
                return;
            }

            List<User> users = notification.getResult();
            List<UserDTO> usersList = UsersMapper.convertUserListToUserDTOList(users);

            AdminView adminView = new AdminView(stage, usersList);
            new AdminController(adminView, authenticationService, userService, salesService, bookService, salesView,rightsRolesService);

            // new root for scene
            stage.getScene().setRoot(adminView.getRoot());
        }
    }

}



package org.example.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import org.example.database.DataBaseConnectionFactory;
import org.example.mapper.BookMapper;
import org.example.mapper.UsersMapper;
import org.example.model.Book;
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
import org.example.view.BookView;
import org.example.view.LoginView;
import org.example.view.SalesView;
import org.example.view.model.BookDTO;
import org.example.view.model.SalesDTO;
import javafx.scene.Parent;

import java.sql.Connection;
import java.util.List;

public class SalesController {
    private final SalesView salesView;
    private final SalesService salesService;
    private final BookService bookService ;
    private final UserService userService ;
    private final RightsRolesService rightsRolesService ;
    private final AuthenticationService authenticationService ;

    public SalesController(SalesView salesView, SalesService salesService , BookService bookService ,AuthenticationService authenticationService ,UserService userService ,RightsRolesService rightsRolesService) {
        this.salesView = salesView;
        this.salesService = salesService;
        this.bookService = bookService ;
        this.userService = userService ;
        this.rightsRolesService = rightsRolesService ;
        this.authenticationService = authenticationService ;
        this.salesView.LibraryButtonListener(new LibraryButtonListener());
        this.salesView.addLogoutButtonListener(new LogoutButtonListener());
    }

    private class LibraryButtonListener implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            // Obține Stage-ul curent
            Stage primaryStage = (Stage) salesView.getSalesTableView().getScene().getWindow();

            // Obține lista cărților din baza de date
            List<Book> books = bookService.findAll();
            List<BookDTO> bookDTOs = BookMapper.convertBookListToBookDTOList(books);

            // Creează un nou BookView cu același Stage
            BookView bookView = new BookView(primaryStage, bookDTOs);
            // Creează un nou BookController
            new BookController(bookView, bookService, salesView,rightsRolesService,userService,salesService, authenticationService );

            // Verifică nodul rădăcină
            Parent root = bookView.getRoot();
            System.out.println("LibraryButtonListener: root is " + (root != null ? "not null" : "null"));

            // Schimbă scena cu noul conținut
            primaryStage.getScene().setRoot(bookView.getRoot());
        }

    }

    private class LogoutButtonListener implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            authenticationService.logout();
            Stage stage = (Stage) salesView.getRoot().getScene().getWindow();

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



}

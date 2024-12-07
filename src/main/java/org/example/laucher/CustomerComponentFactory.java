package org.example.laucher;

import javafx.stage.Stage;
import org.example.controller.BookController;
import org.example.database.DataBaseConnectionFactory;
import org.example.mapper.BookMapper;
import org.example.model.Book;
import org.example.repository.book.BookRepository;
import org.example.repository.book.BookRepositoryCacheDecorator;
import org.example.repository.book.BookRepositoryMySQL;
import org.example.repository.book.Cache;
import org.example.repository.sales.SalesRepository;
import org.example.repository.sales.SalesRepositoryMySQL;
import org.example.repository.security.RightsRolesRepository;
import org.example.repository.security.RightsRolesRepositoryMySQL;
import org.example.repository.user.UserRepository;
import org.example.repository.user.UserRepositoryMySQL;
import org.example.service.book.BookService;
import org.example.service.book.BookServiceImplementation;
import org.example.service.sales.SalesService;
import org.example.service.sales.SalesServiceImplementation;
import org.example.service.user.AuthenticationService;
import org.example.service.user.AuthenticationServiceMySQL;
import org.example.view.BookView;
import org.example.view.model.BookDTO;

import java.sql.Connection;
import java.util.List;

public class CustomerComponentFactory {

    private final BookView bookView;
    private final BookController bookController;
    private final BookService bookService;
    private final SalesService salesService;
    private final AuthenticationService authenticationService;
    private static volatile CustomerComponentFactory instance;
    private final Stage stage ;

    public static CustomerComponentFactory getInstance(Stage stage) {
        if (instance == null) {
            synchronized (CustomerComponentFactory.class) {
                if (instance == null) {
                    instance = new CustomerComponentFactory(stage);
                }
            }
        }
        return instance;
    }

    private CustomerComponentFactory(Stage stage) {
        this.stage = stage ;
        Connection connection = DataBaseConnectionFactory.getConnectionWrapper(false).getConnection();
        RightsRolesRepository rightsRolesRepository = new RightsRolesRepositoryMySQL(connection);

        // Initialize repositories and services
        BookRepository bookRepository = new BookRepositoryCacheDecorator(new BookRepositoryMySQL(connection), new Cache<>());
        bookService = new BookServiceImplementation(bookRepository);

        UserRepository userRepository = new UserRepositoryMySQL(connection, rightsRolesRepository);
        authenticationService = new AuthenticationServiceMySQL(userRepository, rightsRolesRepository);

        // Initialize salesService
        SalesRepository salesRepository = new SalesRepositoryMySQL(connection);
        salesService = new SalesServiceImplementation(salesRepository);

        // Get book list
        List<Book> books = bookService.findAll();
        List<BookDTO> bookDTOs = BookMapper.convertBookListToBookDTOList(books);

        // Create BookView
        bookView = new BookView(stage, bookDTOs);

        // Create BookController with initialized salesService
        bookController = new BookController(bookView, bookService, null ,null ,null, salesService ,authenticationService);

        // Hide certain buttons if needed
        bookView.hideAdminButton();
        bookView.hideDeleteButton();
        bookView.hideSalesButton();
        bookView.hideSaveButton();

        // Set the scene with the new content
        stage.getScene().setRoot(bookView.getRoot());
    }

    public BookView getBookView() {
        return bookView;
    }

    public BookController getBookController() {
        return bookController;
    }

    public static void resetInstance() {
        instance = null;
    }
}

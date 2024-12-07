package org.example.laucher;



import org.example.controller.BookController;
import org.example.controller.SalesController;
import org.example.database.DataBaseConnectionFactory;
import javafx.stage.Stage;
import org.example.mapper.BookMapper;
import org.example.mapper.SalesMapper;
import org.example.repository.book.BookRepository;
import org.example.repository.book.BookRepositoryCacheDecorator;
import org.example.repository.book.BookRepositoryMySQL;
import org.example.repository.book.Cache;
import org.example.repository.sales.SalesRepositoryMySQL;
import org.example.repository.security.RightsRolesRepository;
import org.example.repository.security.RightsRolesRepositoryMySQL;
import org.example.repository.user.UserRepositoryMySQL;
import org.example.service.book.BookService;
import org.example.service.book.BookServiceImplementation;
import org.example.service.book.BookServiceImplementation;
import org.example.service.sales.SalesService;
import org.example.service.sales.SalesServiceImplementation;
import org.example.service.user.AuthenticationService;
import org.example.service.user.AuthenticationServiceMySQL;
import org.example.view.BookView;
import org.example.view.SalesView;
import org.example.view.model.BookDTO;
import org.example.view.model.SalesDTO;
import org.example.view.model.builder.SalesDTOBuilder;

import java.sql.Connection;
import java.util.List;

public class EmployeeComponentFactory {

    private final BookView bookView;
    private final SalesView salesView ;
    private final BookController bookController;
    private final SalesController salesController;
    private final BookRepository bookRepository;
    private final BookService bookService;
    private final SalesService salesService;
    private final AuthenticationService authenticationService ;
    private static volatile EmployeeComponentFactory instance;
    private final Stage stage ;
    private static RightsRolesRepository rightsRolesRepository ;

    public static EmployeeComponentFactory getInstance(Boolean componentsForTest, Stage stage){
        if (instance == null) {
            synchronized (EmployeeComponentFactory.class) {
                if (instance == null) {
                    instance = new EmployeeComponentFactory(componentsForTest,stage);
                }
            }
        }
        return instance;
    }

    public EmployeeComponentFactory(Boolean componentsForTest, Stage stage){
        this.stage =stage ;
        Connection connection = DataBaseConnectionFactory.getConnectionWrapper(componentsForTest).getConnection();
        this.bookRepository = new BookRepositoryCacheDecorator(new BookRepositoryMySQL(connection), new Cache<>());
        this.rightsRolesRepository = new RightsRolesRepositoryMySQL(connection) ;
        this.bookService = new BookServiceImplementation(bookRepository);
        this.salesService = new SalesServiceImplementation(new SalesRepositoryMySQL(connection));
        this.authenticationService = new AuthenticationServiceMySQL(new UserRepositoryMySQL(connection,rightsRolesRepository),rightsRolesRepository) ;
        List<BookDTO> bookDTOs = BookMapper.convertBookListToBookDTOList(this.bookService.findAll());
        this.bookView = new BookView(stage, bookDTOs);
        bookView.hideAdminButton();
        List<SalesDTO> salesDTOS = SalesMapper.convertSaleListToSalesDTOList(this.salesService.getAllSales()) ;
        this.salesView = new SalesView(stage , salesDTOS) ;
        this.bookController = new BookController(bookView,bookService,salesView , null,null,salesService , authenticationService );
        this.salesController = new SalesController(salesView,salesService,bookService,authenticationService,null,null) ;

    }
    public BookView getBookView() {
        return bookView;
    }

    public BookController getBookController() {
        return bookController;
    }

    public BookRepository getBookRepository() {
        return bookRepository;
    }

    public BookService getBookService() {
        return bookService;
    }

    public static EmployeeComponentFactory getInstance() {
        return instance;
    }
    public static void resetInstance() {
        instance = null;
    }
}

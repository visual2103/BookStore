package org.example.laucher;

import org.example.controller.AdminController;
import org.example.database.DataBaseConnectionFactory;
import javafx.stage.Stage;
import org.example.mapper.BookMapper;
import org.example.mapper.SalesMapper;
import org.example.mapper.UsersMapper;
import org.example.model.User;
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
import org.example.service.security.RightsRolesService;
import org.example.service.sales.SalesService;
import org.example.service.sales.SalesServiceImplementation;
import org.example.service.security.RightsRolesServiceImplementation;
import org.example.service.user.AuthenticationService;
import org.example.service.user.AuthenticationServiceMySQL;
import org.example.service.user.UserService;
import org.example.service.user.UserServiceImplementation;
import org.example.view.AdminView;
import org.example.view.SalesView;
import org.example.view.model.SalesDTO;
import org.example.view.model.UserDTO;

import java.sql.Connection;
import java.util.List;

public class AdminComponentFactory {

    private final AdminView adminView;
    private final AdminController adminController;
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final RightsRolesService rightsRolesService;
    private final SalesService salesService;
    private final BookService bookService;
    private final SalesView salesView;

    private static volatile AdminComponentFactory instance;
    private final Stage stage ;

    public static AdminComponentFactory getInstance(Stage stage){
        if (instance == null) {
            synchronized (AdminComponentFactory.class) {
                if (instance == null) {
                    instance = new AdminComponentFactory(stage);
                }
            }
        }
        return instance;
    }

    private AdminComponentFactory(Stage stage){
        this.stage = stage ;
        Connection connection = DataBaseConnectionFactory.getConnectionWrapper(false).getConnection();

        RightsRolesRepository rightsRolesRepository = new RightsRolesRepositoryMySQL(connection);
        rightsRolesService = new RightsRolesServiceImplementation(rightsRolesRepository);

        UserRepository userRepository = new UserRepositoryMySQL(connection, rightsRolesRepository);
        userService = new UserServiceImplementation(userRepository,rightsRolesRepository);

        authenticationService = new AuthenticationServiceMySQL(userRepository, rightsRolesRepository);

        BookRepository bookRepository = new BookRepositoryCacheDecorator(new BookRepositoryMySQL(connection), new Cache<>());
        bookService = new BookServiceImplementation(bookRepository);

        SalesRepository salesRepository = new SalesRepositoryMySQL(connection);
        salesService = new SalesServiceImplementation(salesRepository);

        List<SalesDTO> salesList = SalesMapper.convertSaleListToSalesDTOList(salesService.getAllSales());
        salesView = new SalesView(stage, salesList);

        List<User> users = userService.getAllUsers().getResult();
        List<UserDTO> userDTOs = UsersMapper.convertUserListToUserDTOList(users);

        adminView = new AdminView(stage, userDTOs);
        adminController = new AdminController(
                adminView,
                authenticationService,
                userService,
                salesService,
                bookService,
                salesView,
                rightsRolesService
        );

        stage.getScene().setRoot(adminView.getRoot());
    }
    public AdminView getAdminView() {
        return adminView;
    }

    public AdminController getAdminController() {
        return adminController;
    }
    public static void resetInstance() {
        instance = null;
    }

}

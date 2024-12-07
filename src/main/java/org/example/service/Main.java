package org.example.service;
import org.example.repository.user.UserRepositoryMySQL;
import org.example.database.DataBaseConnectionFactory;
import org.example.model.User;
import org.example.repository.book.BookRepository;
import org.example.repository.book.BookRepositoryCacheDecorator;
import org.example.repository.book.BookRepositoryMySQL;
import org.example.repository.book.Cache;
import org.example.repository.security.RightsRolesRepository;
import org.example.repository.security.RightsRolesRepositoryMySQL;
import org.example.repository.user.UserRepository;
import org.example.service.book.BookService;
import org.example.service.book.BookServiceImplementation;
import org.example.service.user.AuthenticationService;
import org.example.service.user.AuthenticationServiceMySQL;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        BookRepository bookRepository = new BookRepositoryCacheDecorator(
                new BookRepositoryMySQL(DataBaseConnectionFactory.getConnectionWrapper(true).getConnection()),
                new Cache<>()
        );

        Connection connection = DataBaseConnectionFactory.getConnectionWrapper(true).getConnection();

        BookService bookService = new BookServiceImplementation(bookRepository);
        RightsRolesRepository rightsRolesRepository = new RightsRolesRepositoryMySQL(DataBaseConnectionFactory.getConnectionWrapper(true).getConnection());

        UserRepository userRepository = new UserRepositoryMySQL(connection, rightsRolesRepository);

        AuthenticationService authenticationService = new AuthenticationServiceMySQL(userRepository, rightsRolesRepository);

        authenticationService.register("Alex", "parola123!");

        System.out.println(authenticationService.login("Alex", "parola123!"));


    }
}

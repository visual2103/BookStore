package org.example.service.book;

import org.example.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookService {
    List<Book> findAll() ;
    Book findById(Long id) ; // am renuntat la Optional pt ca e treaba lui repo sa se ocupe daca exista sau nu cartea , aii doar o "prezentam"
    boolean save(Book book) ;
    boolean delete(Book book);
    int getAgeOfBook(Long id) ; // are logica ei => tot ce tine de algoritmi si logica va fi implementat in service pentru ca fac parte din BLL
    boolean update (Book book) ; // pentru sell sa i dau update la qunatity i interfata
}

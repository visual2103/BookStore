package org.example.repository.book;

import org.example.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository {
    List<Book> findAll() ;
    Optional<Book> findById(Long id) ;
    boolean save(Book book) ;
    boolean delete(Book book);
    boolean update(Book book) ;
    void removeAll() ; //flash
    Optional<Book> findByTitleAuthorAndPrice(Book book) ;
}

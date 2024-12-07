package org.example.repository.book;

import org.example.model.Book;
import org.example.repository.book.BookRepository;

import java.util.*;
import java.util.Optional;

public class BookRepositoryMock implements BookRepository {
    private final List<Book> books ; // emulam un BD folosind o lista de carti

    public BookRepositoryMock() {
        books = new ArrayList<>();
    }

    @Override
    public List<Book> findAll() {
        return books;
    }

    @Override
    public Optional<Book> findById(Long id) {
        return  books.parallelStream() // mai rapid cand lista e big data
                .filter(x->x.getId().equals(id))
                .findFirst() ;
    }

    @Override
    public boolean save(Book book) {
        return books.add(book );
    }

    @Override
    public boolean delete(Book book) {
        return books.remove(book);
    }

    @Override
    public boolean update(Book book) {
        return false;
    }

    @Override
    public void removeAll() {
        books.clear();

    }

    @Override
    public Optional<Book> findByTitleAuthorAndPrice(Book book) {
        return Optional.empty();
    }
}

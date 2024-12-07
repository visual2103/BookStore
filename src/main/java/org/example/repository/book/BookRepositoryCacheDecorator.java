package org.example.repository.book;

import org.example.model.Book;

import java.util.List;
import java.util.Optional;

public class BookRepositoryCacheDecorator extends BookRepositoryDecorator {
    private final Cache<Book> cache;

    public BookRepositoryCacheDecorator(BookRepository bookRepository, Cache<Book> cache) {
        super(bookRepository);
        this.cache = cache;
    }

    @Override
    public List<Book> findAll() {
        if (cache.hasResult()) {
            System.out.println("Loading books from cache.");
            return cache.load();
        }

        System.out.println("Loading books from database.");
        List<Book> books = decoratedBookRepository.findAll();
        cache.save(books);
        return books;
    }

    @Override
    public Optional<Book> findById(Long id) {
        if (cache.hasResult()) {
            System.out.println("Searching for book with ID " + id + " in cache.");
            return cache.load().stream()
                    .filter(book -> book.getId().equals(id))
                    .findFirst();
        }

        System.out.println("Searching for book with ID " + id + " in database.");
        return decoratedBookRepository.findById(id);
    }

    @Override
    public boolean save(Book book) {
        System.out.println("Saving book: " + book);
        cache.invalidateCache(); // Invalidează cache-ul înainte de a salva
        return decoratedBookRepository.save(book); // Salvează cartea în baza de date
    }

    @Override
    public boolean delete(Book book) {
        System.out.println("Deleting book: " + book);
        cache.invalidateCache(); // Invalidează cache-ul înainte de a șterge
        return decoratedBookRepository.delete(book);
    }

    @Override
    public boolean update(Book book) {
        boolean result = decoratedBookRepository.update(book);
        if (result) {
            cache.invalidateCache();
        }
        return result;
    }


    @Override
    public void removeAll() {
        System.out.println("Removing all books.");
        cache.invalidateCache(); // Invalidează cache-ul
        decoratedBookRepository.removeAll();
    }

    @Override
    public Optional<Book> findByTitleAuthorAndPrice(Book book) {
        if (cache.hasResult()) {
            System.out.println("searching for book in cache ");
            return cache.load().stream()
                    .filter(b -> b.getTitle().equals(book.getTitle())
                            && b.getAuthor().equals(book.getAuthor())
                            && b.getPrice() == book.getPrice())
                    .findFirst();
        }
        System.out.println("searching for book in db");
        return  decoratedBookRepository.findByTitleAuthorAndPrice(book) ;
    }
}

package org.example.service.book;
import org.example.model.Book;
import org.example.repository.book.BookRepository;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

public class BookServiceImplementation implements BookService {
    private final BookRepository bookRepository ;

    public BookServiceImplementation(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public List<Book> findAll( ) {
        return bookRepository.findAll();
    }

    @Override
    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Book with id %d wasn't found!", id)));
    }

    @Override
    public boolean save(Book book) {
        Optional<Book> existingBook = bookRepository.findByTitleAuthorAndPrice(book);
        if (existingBook.isPresent()){
            Book bookToUpdate = existingBook.get();
            bookToUpdate.setQuantity(bookToUpdate.getQuantity() + book.getQuantity());
            return bookRepository.update(bookToUpdate);
        }
        return bookRepository.save(book) ;
    }

    @Override
    public boolean delete(Book book) {
        return bookRepository.delete(book);
    }

    @Override
    public int getAgeOfBook(Long id) {
        Book book = this.findById(id) ;  // in findById se fac verficarile , reutilizez codul ca clasa sa aiba o singura "intrare"
        LocalDate now = LocalDate.now() ;
        return (int) ChronoUnit.YEARS.between(book.getPublishedDate(),now) ;
    }

    @Override
    public boolean update(Book book) {
        return bookRepository.update(book);
    }


}


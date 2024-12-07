import org.example.model.builder.BookBuilder;
import org.example.repository.book.BookRepository;
import org.example.repository.book.BookRepositoryMock;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.example.model.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BookRepositoryMockTest {
    private static BookRepository bookRepository ;
    @BeforeAll // se executa inainte de toat emetodele din clasa si o sngura dtaa la fiecare test
    public static void setup(){

        bookRepository = new BookRepositoryMock() ;
    }
    @Test
    public void findAll(){
        List<Book> books = bookRepository.findAll();
        assertEquals(0,books.size()) ;
    }

    @Test
    public void findById(){
        final Optional<Book> book = bookRepository.findById(1L);
        assertTrue(book.isEmpty());
    }

    @Test
    public void save(){
        assertTrue(bookRepository.save( new BookBuilder().setTitle("Mara").setAuthor("Ioan Slavici").setPublishedDate(LocalDate.of(1930,6,3)).build() ));
    }
}

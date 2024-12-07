import org.example.model.Book;
import org.example.model.builder.BookBuilder;
import org.example.repository.book.BookRepository;
import org.example.repository.book.BookRepositoryMySQL;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS) // pt met. non-statice pentru setup și teardown
public class BookRepositoryMySQLTest {

    private Connection connection;
    private BookRepository bookRepository;

    @BeforeAll
    void setUpDatabase() throws SQLException {
        connection = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/library?useSSL=false&serverTimezone=UTC",
                "root", "Alina21072003*"
        );
        bookRepository = new BookRepositoryMySQL(connection);

        // tabelul este resetat inainte de rularea testelor
        connection.createStatement().execute("DROP TABLE IF EXISTS book;");
        connection.createStatement().execute(
                "CREATE TABLE book (" +
                        "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                        "author VARCHAR(255) NOT NULL, " +
                        "title VARCHAR(255) NOT NULL, " +
                        "publishedDate DATE NOT NULL" +
                        ");"
        );
    }

    @BeforeEach
    void clearTable() throws SQLException {
        connection.createStatement().execute("DELETE FROM book;");
    }

    @AfterAll
    void closeDatabase() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    void testFindAll() {
        bookRepository.save(new BookBuilder()
                .setTitle("Book1")
                .setAuthor("Author1")
                .setPublishedDate(LocalDate.of(2024, 1, 1))
                .build());

        bookRepository.save(new BookBuilder()
                .setTitle("Book2")
                .setAuthor("Author2")
                .setPublishedDate(LocalDate.of(2023, 1, 1))
                .build());

        List<Book> books = bookRepository.findAll();

        assertEquals(2, books.size());
        assertEquals("Book1", books.get(0).getTitle());
        assertEquals("Author2", books.get(1).getAuthor());
    }

    @Test
    void testFindById() {
        Book book = new BookBuilder()
                .setTitle("Book3")
                .setAuthor("Author3")
                .setPublishedDate(LocalDate.of(2022, 1, 1))
                .build();

        boolean saved = bookRepository.save(book);
        assertTrue(saved, "Book should be saved successfully");

        assertNotNull(book.getId(), "Book ID shouldn't be null after save");
        assertTrue(book.getId() > 0, "Book ID should be greater than 0");

        Optional<Book> foundBook = bookRepository.findById(book.getId());
        assertTrue(foundBook.isPresent(), "Book should be found");
        assertEquals("Book3", foundBook.get().getTitle());
        assertEquals("Author3", foundBook.get().getAuthor());
    }

    @Test
    void testSave() {
        Book book = new BookBuilder()
                .setTitle("Book4")
                .setAuthor("Author4")
                .setPublishedDate(LocalDate.of(2021, 1, 1))
                .build();

        boolean result = bookRepository.save(book);

        assertTrue(result, "Book should be saved successfully");
        assertNotNull(book.getId(), "Book ID should not be null after save");
        assertTrue(book.getId() > 0, "Book ID should be greater than 0");

        List<Book> books = bookRepository.findAll();
        assertEquals(1, books.size(), "There should be exactly one book in the repository");
        assertEquals("Book4", books.get(0).getTitle(), "The title of the book should match");
    }

    @Test
    void testDelete() {
        Book book = new BookBuilder()
                .setTitle("Book5")
                .setAuthor("Author5")
                .setPublishedDate(LocalDate.of(2020, 1, 1))
                .build();

        boolean saved = bookRepository.save(book);
        assertTrue(saved, "Book should be saved successfully");
        assertNotNull(book.getId(), "Book ID should not be null after save");

        boolean existsBeforeDelete = bookRepository.findById(book.getId()).isPresent();
        assertTrue(existsBeforeDelete, "Book should exist before deletion");

        boolean deleted = bookRepository.delete(book);
        assertTrue(deleted, "Book should be deleted successfully");

        boolean existsAfterDelete = bookRepository.findById(book.getId()).isPresent();
        assertFalse(existsAfterDelete, "Book should not exist after deletion");
    }


    @Test
    void testRemoveAll() {
        bookRepository.save(new BookBuilder()
                .setTitle("Title1")
                .setAuthor("Author1")
                .setPublishedDate(LocalDate.of(2020, 1, 1))
                .build());
        bookRepository.save(new BookBuilder()
                .setTitle("Title2")
                .setAuthor("Author2")
                .setPublishedDate(LocalDate.of(2021, 1, 1))
                .build());

        bookRepository.removeAll();
        List<Book> books = bookRepository.findAll();
        assertTrue(books.isEmpty());
    }
}

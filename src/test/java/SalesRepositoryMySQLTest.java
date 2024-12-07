package org.example.repository.sales;

import org.example.model.Sales;
import org.example.model.builder.SalesBuilder;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SalesRepositoryMySQLTest {

    private Connection connection;
    private SalesRepository salesRepository;

    @BeforeAll
    void setUpDatabase() throws SQLException {
        connection = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/library?useSSL=false&serverTimezone=UTC",
                "root", "Alina21072003*"
        );
        salesRepository = new SalesRepositoryMySQL(connection);


        connection.createStatement().execute("DROP TABLE IF EXISTS sales;");
        connection.createStatement().execute(
                "CREATE TABLE sales (" +
                        "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                        "book_title VARCHAR(255) NOT NULL, " +
                        "book_author VARCHAR(255) NOT NULL, " +
                        "quantity INT NOT NULL, " +
                        "total_price FLOAT NOT NULL, " +
                        "sale_date DATE NOT NULL" +
                        ");"
        );
    }

    @BeforeEach
    void clearTable() throws SQLException {
        connection.createStatement().execute("DELETE FROM sales;");
    }

    @AfterAll
    void closeDatabase() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    void testFindAll() {
        salesRepository.save(new SalesBuilder()
                .setBookTitle("Titlu1")
                .setBookAuthor("Autor1")
                .setQuantity(5)
                .setTotalPrice(100.0f)
                .setSaleDate(LocalDate.of(2023, 10, 1))
                .build());

        salesRepository.save(new SalesBuilder()
                .setBookTitle("Titlu2")
                .setBookAuthor("Autor2")
                .setQuantity(3)
                .setTotalPrice(60.0f)
                .setSaleDate(LocalDate.of(2023, 10, 2))
                .build());

        List<Sales> salesList = salesRepository.findAll();

        assertEquals(2, salesList.size());
        assertEquals("Titlu1", salesList.get(0).getBookTitle());
        assertEquals("Autor2", salesList.get(1).getBookAuthor());
    }


}

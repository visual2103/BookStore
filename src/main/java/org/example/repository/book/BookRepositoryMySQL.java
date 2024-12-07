package org.example.repository.book;

import org.example.model.Book;
import org.example.model.builder.BookBuilder;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookRepositoryMySQL implements BookRepository {
    private final Connection connection;

    public BookRepositoryMySQL(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Book> findAll() {
        String sql = "SELECT * FROM book;";
        List<Book> books = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                books.add(getBookFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error fetching all books: " + e.getMessage());
        }
        return books;
    }

    @Override
    public Optional<Book> findById(Long id) {
        String sql = "SELECT * FROM book WHERE id = ?;";
        Optional<Book> book = Optional.empty();

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    book = Optional.of(getBookFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error fetching book by ID: " + e.getMessage());
        }

        return book;
    }

    @Override
    public boolean save(Book book) {
        String sql = "INSERT INTO book (author, title, publishedDate,quantity ,price) VALUES (?, ?, ? ,? ,?);";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            System.out.println("Saving book: " + book); // Log pentru carte

            preparedStatement.setString(1, book.getAuthor());
            preparedStatement.setString(2, book.getTitle());


            // publishedDate este null
            if (book.getPublishedDate() != null) {
                preparedStatement.setDate(3, java.sql.Date.valueOf(book.getPublishedDate()));
            } else {
                preparedStatement.setNull(3, Types.DATE);
            }

            preparedStatement.setInt(4, book.getQuantity());
            preparedStatement.setFloat(5, book.getPrice());
            int rowsInserted = preparedStatement.executeUpdate();
            System.out.println("Rows inserted: " + rowsInserted); //

            if (rowsInserted > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        book.setId(generatedKeys.getLong(1));
                        System.out.println("Generated ID: " + book.getId()); // Log pentru ID generat
                    }
                }
            }
            System.out.println("Saving book: " + book.getTitle() + ", " + book.getAuthor() + ", " + book.getPublishedDate() + "," +book.getQuantity() + "," +book.getPrice());
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error saving book: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            return false;
        }

    }

    @Override
    public boolean delete(Book book) {
        String sql = "DELETE FROM book WHERE author = ? AND title = ? AND quantity = ? AND price = ?;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, book.getAuthor());
            preparedStatement.setString(2, book.getTitle());
            preparedStatement.setInt(3, book.getQuantity());
            preparedStatement.setFloat(4, book.getPrice());
            int rowsDeleted = preparedStatement.executeUpdate();
            System.out.println("Rows deleted: " + rowsDeleted); // Log pentru rânduri șterse
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error deleting book: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(Book book) {
        String sql = "UPDATE book SET author = ?, title = ?, publishedDate = ?, quantity = ?, price = ? WHERE id = ?;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, book.getAuthor());
            preparedStatement.setString(2, book.getTitle());

            if (book.getPublishedDate() != null) {
                preparedStatement.setDate(3, java.sql.Date.valueOf(book.getPublishedDate()));
            } else {
                preparedStatement.setNull(3, Types.DATE);
            }

            preparedStatement.setInt(4, book.getQuantity());
            preparedStatement.setFloat(5, book.getPrice());

            preparedStatement.setLong(6, book.getId());

            int rowsUpdated = preparedStatement.executeUpdate();

            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error updating book: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void removeAll() {
        String sql = "DELETE FROM book WHERE id >= 0;"; // Alternativ: "TRUNCATE TABLE book;"
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int rowsDeleted = preparedStatement.executeUpdate();
            System.out.println("All rows deleted: " + rowsDeleted); // Log pentru ștergere
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error removing all books: " + e.getMessage());
        }
    }

    @Override
    public Optional<Book> findByTitleAuthorAndPrice(Book book) {
        String sql = "SELECT * FROM book WHERE title = ? AND author = ? AND price = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, book.getTitle());
            preparedStatement.setString(2, book.getAuthor());
            preparedStatement.setFloat(3, book.getPrice());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(getBookFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error finding book by title, author, and price: " + e.getMessage());
        }
        return Optional.empty();
    }


    private Book getBookFromResultSet(ResultSet resultSet) throws SQLException {
        BookBuilder builder = new BookBuilder()
                .setId(resultSet.getLong("id"))
                .setTitle(resultSet.getString("title"))
                .setAuthor(resultSet.getString("author"))
                .setQuantity(resultSet.getInt("quantity"))
                .setPrice(resultSet.getFloat("price")) ;

        Date publishedDate = resultSet.getDate("publishedDate");
        if (publishedDate != null) {
            builder.setPublishedDate(publishedDate.toLocalDate());
        }

        return builder.build();
    }
}

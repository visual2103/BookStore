package org.example.repository.sales;

import org.example.model.Sales;
import org.example.model.builder.SalesBuilder;

import java.sql.Connection;
import java.sql.Date; // Import corect pentru java.sql.Date
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // Adăugat importul pentru Statement
import java.util.ArrayList;
import java.util.List;

public class SalesRepositoryMySQL implements SalesRepository {
    private final Connection connection;

    public SalesRepositoryMySQL(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean save(Sales sale) {
        String sql = "INSERT INTO sales (book_title, book_author, quantity, price, sale_date,employee_id) VALUES (?, ?, ?, ?, ?,?);";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, sale.getBookTitle());
            preparedStatement.setString(2, sale.getBookAuthor());
            preparedStatement.setInt(3, sale.getQuantity());
            preparedStatement.setFloat(4, sale.getTotalPrice());
            preparedStatement.setDate(5, Date.valueOf(sale.getSaleDate()));
            preparedStatement.setLong(6, sale.getEmployeeid()); // Adăugăm user_id

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Obține ID-ul generat automat și setează-l în obiectul Sales
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        sale.setId(generatedKeys.getLong(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error saving sale: " + e.getMessage());
        }
        return false;
    }

    @Override
    public List<Sales> findAll() {
        String sql = "SELECT * FROM sales;";
        List<Sales> salesList = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                salesList.add(getSalesFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error fetching all sales: " + e.getMessage());
        }
        return salesList;
    }

    @Override
    public List<Sales> findSalesByUserID(Long userId) {
        List<Sales> sales = new ArrayList<>();
        try {
            String sql = "SELECT * FROM sales WHERE employee_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, userId);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Sales sale = new Sales();
                sale.setId(rs.getLong("id"));
                sale.setBookTitle(rs.getString("book_title"));
                sale.setBookAuthor(rs.getString("book_author"));
                sale.setQuantity(rs.getInt("quantity"));
                sale.setTotalPrice(rs.getFloat("price"));
                sale.setSaleDate(rs.getDate("sale_date").toLocalDate());
                sale.setId(rs.getLong("employee_id"));
                sales.add(sale);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sales;
    }


    private Sales getSalesFromResultSet(ResultSet resultSet) throws SQLException {
        SalesBuilder builder = new SalesBuilder()
                .setId(resultSet.getLong("id"))
                .setBookTitle(resultSet.getString("book_title"))
                .setBookAuthor(resultSet.getString("book_author"))
                .setQuantity(resultSet.getInt("quantity"))
                .setTotalPrice(resultSet.getFloat("price"));

        //  saleDate este null
        Date saleDate = resultSet.getDate("sale_date"); // java.sql.Date
        if (saleDate != null) {
            builder.setSaleDate(saleDate.toLocalDate());
        }

        return builder.build();
    }


}

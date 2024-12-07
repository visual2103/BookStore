package org.example.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBConnectionWrapper {
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306";
    private static final String USER = "root" ;
    private static final String PASSWORD = "Alina21072003*";
    private static final int TIMEOUT = 5;

    private Connection connection ;
    public JDBConnectionWrapper(String schema) {
        //schema = library /test_library -> bd de pe server se creeaza dinamic in construcotr
        try{
            Class.forName(JDBC_DRIVER) ;
            connection = DriverManager.getConnection(DB_URL +'/'+ schema + "?allowMultiQueries=true" , USER, PASSWORD);
            createTables();
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }catch (SQLException e){
            //ultima e generica , sa le prinda pe toate , mai sus se pun cele particulare
            e.printStackTrace();
        }

    }
    private void createTables() throws SQLException {
        Statement statement = connection.createStatement();

        // Creează tabelul 'book' dacă nu există
        String sql = "CREATE TABLE IF NOT EXISTS book (" +
                " id BIGINT NOT NULL AUTO_INCREMENT, " +
                " author VARCHAR(500) NOT NULL, " +
                " title VARCHAR(500) NOT NULL, " +
                " publishedDate DATETIME DEFAULT NULL, " +
                " quantity BIGINT NOT NULL DEFAULT 1 , "+
                " price DECIMAL(10,2) NOT NULL DEFAULT 0.0 ,"+
                " PRIMARY KEY (id), " +
                " UNIQUE KEY id_UNIQUE (id)" +
                ") ENGINE = InnoDB AUTO_INCREMENT = 0 DEFAULT CHARSET = utf8;";
        statement.execute(sql);

        System.out.println("Table 'book' has been created or already exists.");
    }


    public boolean testConnection() throws SQLException {
        return connection.isValid(TIMEOUT) ;
    }

    public Connection getConnection() {
        return connection ;
    }
}

package org.example.repository.user;

import org.example.model.Role;
import org.example.model.User;
import org.example.model.builder.UserBuilder;
import org.example.model.validator.Notification;
import org.example.repository.security.RightsRolesRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.example.database.Constants.Tables.USER;
import static java.util.Collections.singletonList;

public class UserRepositoryMySQL implements UserRepository {

    private final Connection connection;
    private final RightsRolesRepository rightsRolesRepository;

    public UserRepositoryMySQL(Connection connection, RightsRolesRepository rightsRolesRepository) {
        this.connection = connection;
        this.rightsRolesRepository = rightsRolesRepository;
    }

    @Override
    public List<User> findAll() {
        // SQL Injection Attacks should not work after fixing functions
        // Be careful that the last character in sql injection payload is an empty space
        // alexandru.ghiurutan95@gmail.com' and 1=1; --
        // ' or username LIKE '%admin%'; --
        List<User> users = new ArrayList<>();
        String fetchAllUsersSql = "SELECT * FROM `" + USER + "`";

        try (PreparedStatement preparedStatement = connection.prepareStatement(fetchAllUsersSql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                User user = new UserBuilder()
                        .setId(resultSet.getLong("id"))
                        .setUsername(resultSet.getString("username"))
                        .setPassword(resultSet.getString("password"))
                        .setRoles(rightsRolesRepository.findRolesForUser(resultSet.getLong("id")))
                        .build();
                users.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    @Override
    public Notification<User> findByUsernameAndPassword(String username, String password) {

        Notification<User> findByUsernameAndPasswordNotification = new Notification<>();
        String fetchUserSql = "SELECT * FROM `" + USER + "` WHERE `username` = ? AND `password` = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(fetchUserSql)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            try (ResultSet userResultSet = preparedStatement.executeQuery()) {
                if (userResultSet.next()) {
                    User user = new UserBuilder()
                            .setId(userResultSet.getLong("id"))
                            .setUsername(userResultSet.getString("username"))
                            .setPassword(userResultSet.getString("password"))
                            .setRoles(rightsRolesRepository.findRolesForUser(userResultSet.getLong("id")))
                            .build();

                    findByUsernameAndPasswordNotification.setResult(user);
                } else {
                    findByUsernameAndPasswordNotification.addError("Invalid username or password!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            findByUsernameAndPasswordNotification.addError("Something went wrong with the database!");
        }

        return findByUsernameAndPasswordNotification;
    }

    @Override
    public Notification<User> findByUsername(String username) {
        Notification<User> notification = new Notification<>();
        String fetchUserSql = "SELECT * FROM `" + USER + "` WHERE `username` = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(fetchUserSql)) {
            preparedStatement.setString(1, username);

            try (ResultSet userResultSet = preparedStatement.executeQuery()) {
                if (userResultSet.next()) {
                    User user = new UserBuilder()
                            .setId(userResultSet.getLong("id"))
                            .setUsername(userResultSet.getString("username"))
                            .setPassword(userResultSet.getString("password"))
                            .setSalt(userResultSet.getString("salt"))
                            .setRoles(rightsRolesRepository.findRolesForUser(userResultSet.getLong("id")))
                            .build();

                    notification.setResult(user);
                } else {
                    notification.addError("User not found!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            notification.addError("Database error: " + e.getMessage());
        }

        return notification;
    }


    @Override// de modificat , functia va trebui sa retunreze  notification de boolean , pe acelasi model ca la findByUsernameAndPassword ,
    public Notification<Boolean> save(User user) {
        Notification<Boolean> saveNotification = new Notification<>();
        String insertUserSql = "INSERT INTO `" + USER + "` (`username`, `password`, `salt`) VALUES (?, ?, ?)";

        try (PreparedStatement insertUserStatement = connection.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS)) {
            insertUserStatement.setString(1, user.getUsername());
            insertUserStatement.setString(2, user.getPassword());
            insertUserStatement.setString(3, user.getSalt());
            int rowsInserted = insertUserStatement.executeUpdate();

            if (rowsInserted > 0) {
                try (ResultSet rs = insertUserStatement.getGeneratedKeys()) {
                    if (rs.next()) {
                        long userId = rs.getLong(1);
                        user.setId(userId);
                        rightsRolesRepository.addRolesToUser(user, user.getRoles());
                        saveNotification.setResult(true);
                    } else {
                        saveNotification.addError("Failed to retrieve the generated ID.");
                    }
                }
            } else {
                saveNotification.addError("No rows were inserted.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            saveNotification.addError("Database error: " + e.getMessage());
        }

        return saveNotification;
    }



    @Override
    public void removeAll() {
        try {
            Statement statement = connection.createStatement();
            String sql = "DELETE from user where id >= 0";
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Notification<Boolean> deleteById(Long id) {
        Notification<Boolean> deleteNotification = new Notification<>();
        String deleteUserByIdSql = "DELETE FROM `" + USER + "` WHERE `id` = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteUserByIdSql)) {
            preparedStatement.setLong(1, id);
            int rowsDeleted = preparedStatement.executeUpdate();

            if (rowsDeleted > 0) {
                deleteNotification.setResult(true);
            } else {
                deleteNotification.addError("No user found with the given ID.");
                deleteNotification.setResult(false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            deleteNotification.addError("Database error: " + e.getMessage());
            deleteNotification.setResult(false);
        }

        return deleteNotification;
    }



    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT id, username, password FROM user WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    User user = new UserBuilder()
                            .setId(resultSet.getLong("id"))
                            .setUsername(resultSet.getString("username"))
                            .setPassword(resultSet.getString("password"))
                            .build();
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Notification<Boolean> update(User user) {
        Notification<Boolean> updateNotification = new Notification<>();

        try {
            connection.setAutoCommit(false);

            // Delete existing roles for the user
            String deleteRolesSql = "DELETE FROM user_role WHERE user_id = ?";
            try (PreparedStatement deleteStatement = connection.prepareStatement(deleteRolesSql)) {
                deleteStatement.setLong(1, user.getId());
                deleteStatement.executeUpdate();
            }

            // Insert new roles
            List<Role> roles = user.getRoles();
            for (Role role : roles) {
                String insertRoleSql = "INSERT INTO user_role (user_id, role_id) VALUES (?, ?)";
                try (PreparedStatement insertStatement = connection.prepareStatement(insertRoleSql)) {
                    insertStatement.setLong(1, user.getId());
                    insertStatement.setLong(2, role.getId());
                    insertStatement.executeUpdate();
                }
            }

            connection.commit();
            updateNotification.setResult(true);
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            updateNotification.addError("Database error: " + e.getMessage());
            updateNotification.setResult(false);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return updateNotification;
    }



    @Override
    public boolean existsByUsername(String email) { // de vericat : username sa fie unic , introduc apelul spre aceasta functie din authentificationservice, pt ca save sa ramana singleresponsability
        String fetchUserSql = "SELECT * FROM `" + USER + "` WHERE `username` = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(fetchUserSql)) {
            preparedStatement.setString(1, email);

            try (ResultSet userResultSet = preparedStatement.executeQuery()) {
                return userResultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


}
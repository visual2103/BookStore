package org.example.repository.security;

import org.example.model.Right;
import org.example.model.Role;
import org.example.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.example.database.Constants.Tables.RIGHT;
import static org.example.database.Constants.Tables.ROLE;
import static org.example.database.Constants.Tables.ROLE_RIGHT;
import static org.example.database.Constants.Tables.USER_ROLE;

public class RightsRolesRepositoryMySQL implements RightsRolesRepository {
    private final Connection connection;

    public RightsRolesRepositoryMySQL(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void addRole(String role) {
        String sql = "INSERT IGNORE INTO " + ROLE + " VALUES (NULL, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(sql)) {
            insertStatement.setString(1, role);
            insertStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding role: " + e.getMessage());
        }
    }

    @Override
    public void addRight(String right) {
        String sql = "INSERT IGNORE INTO " + RIGHT + " VALUES (NULL, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(sql)) {
            insertStatement.setString(1, right);
            insertStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding right: " + e.getMessage());
        }
    }

    @Override
    public Role findRoleByTitle(String role) {
        String sql = "SELECT * FROM " + ROLE + " WHERE role = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, role);
            try (ResultSet roleResultSet = statement.executeQuery()) {
                if (roleResultSet.next()) {
                    Long roleId = roleResultSet.getLong("id");
                    String roleTitle = roleResultSet.getString("role");
                    return new Role(roleId, roleTitle, null);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding role by title: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Role findRoleById(Long roleId) {
        String sql = "SELECT * FROM " + ROLE + " WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, roleId);
            try (ResultSet roleResultSet = statement.executeQuery()) {
                if (roleResultSet.next()) {
                    String roleTitle = roleResultSet.getString("role");
                    return new Role(roleId, roleTitle, null);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding role by ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Right findRightByTitle(String right) {
        String sql = "SELECT * FROM " + RIGHT + " WHERE `right` = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, right);
            try (ResultSet rightResultSet = statement.executeQuery()) {
                if (rightResultSet.next()) {
                    Long rightId = rightResultSet.getLong("id");
                    String rightTitle = rightResultSet.getString("right");
                    return new Right(rightId, rightTitle);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding right by title: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void addRolesToUser(User user, List<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            System.out.println("No roles to add for user: " + user.getUsername());
            return; // Oprește execuția dacă lista este goală sau null
        }

        String checkRoleExistsSql = "SELECT COUNT(*) FROM role WHERE id = ?";
        String sql = "INSERT INTO user_role (user_id, role_id) VALUES (?, ?)";
        try (PreparedStatement checkRoleStatement = connection.prepareStatement(checkRoleExistsSql);
             PreparedStatement insertStatement = connection.prepareStatement(sql)) {

            for (Role role : roles) {
                // Verifică dacă rolul există
                checkRoleStatement.setLong(1, role.getId());
                try (ResultSet rs = checkRoleStatement.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        System.out.println("Role with ID " + role.getId() + " does not exist.");
                        continue; // Sari peste rolurile inexistente
                    }
                }

                // Adaugă rolul utilizatorului
                insertStatement.setLong(1, user.getId());
                insertStatement.setLong(2, role.getId());
                insertStatement.addBatch();
            }
            insertStatement.executeBatch(); // Execută toate inserările în bloc
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public List<Role> findRolesForUser(Long userId) {
        String sql = "SELECT role_id FROM " + USER_ROLE + " WHERE user_id = ?";
        List<Role> roles = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            try (ResultSet userRoleResultSet = statement.executeQuery()) {
                while (userRoleResultSet.next()) {
                    long roleId = userRoleResultSet.getLong("role_id");
                    Role role = findRoleById(roleId);
                    if (role != null) {
                        roles.add(role);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding roles for user: " + e.getMessage());
        }
        return roles;
    }

    @Override
    public void addRoleRight(Long roleId, Long rightId) {
        String sql = "INSERT IGNORE INTO " + ROLE_RIGHT + " VALUES (NULL, ?, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(sql)) {
            insertStatement.setLong(1, roleId);
            insertStatement.setLong(2, rightId);
            insertStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding role-right relationship: " + e.getMessage());
        }
    }
}

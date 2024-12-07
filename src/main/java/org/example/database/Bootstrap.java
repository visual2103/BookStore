package org.example.database;

import org.example.repository.security.RightsRolesRepository;
import org.example.repository.security.RightsRolesRepositoryMySQL;

import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.example.database.Constants.Rights.RIGHTS;
import static org.example.database.Constants.Roles.ROLES;
import static org.example.database.Constants.Schemas.SCHEMAS;
import static org.example.database.Constants.Tables.*;
import static org.example.database.Constants.getRolesRights;
// Script - code that automates some steps or processes

public class Bootstrap {

    private static RightsRolesRepository rightsRolesRepository;

    public static void main(String[] args) throws SQLException {
        dropAll();

        bootstrapTables();

        bootstrapUserData();
    }

    private static void dropAll() throws SQLException {
        for (String schema : SCHEMAS) {
            System.out.println("Dropping all tables in schema: " + schema);

            Connection connection = new JDBConnectionWrapper(schema).getConnection();
            Statement statement = connection.createStatement();

            // Ordinea corectă de ștergere (tabele dependente primele)
            String[] dropStatements = {
                    "DROP TABLE IF EXISTS `role_right`;",
                    "DROP TABLE IF EXISTS `user_role`;",
                    "DROP TABLE IF EXISTS `right`;",
                    "DROP TABLE IF EXISTS `role`;",
                    "DROP TABLE IF EXISTS `user`;",
                    "DROP TABLE IF EXISTS `sales`;",
                    "DROP TABLE IF EXISTS `book`;"
            };

            Arrays.stream(dropStatements).forEach(dropStatement -> {
                try {
                    System.out.println("Executing: " + dropStatement);
                    statement.execute(dropStatement);
                    System.out.println("Dropped successfully: " + dropStatement);
                } catch (SQLException e) {
                    System.err.println("Error dropping table: " + dropStatement + " - " + e.getMessage());
                }
            });
        }

        System.out.println("Done table bootstrap");
    }


    private static void bootstrapTables() throws SQLException {
        SQLTableCreationFactory sqlTableCreationFactory = new SQLTableCreationFactory();

        for (String schema : SCHEMAS) {
            System.out.println("Bootstrapping " + schema + " schema");

            JDBConnectionWrapper connectionWrapper = new JDBConnectionWrapper(schema);
            Connection connection = connectionWrapper.getConnection();

            for (String table : Constants.Tables.ORDERED_TABLES_FOR_CREATION) {
                String createTableSQL = sqlTableCreationFactory.getCreateSQLForTable(table);


                try (PreparedStatement preparedStatement = connection.prepareStatement(createTableSQL)) {
                    preparedStatement.execute();
                    System.out.println("Table '" + table + "' created successfully (or already exists).");
                } catch (SQLException e) {
                    System.err.println("Error creating table '" + table + "': " + e.getMessage());
                }
            }
        }

        System.out.println("Done table bootstrap");
    }

    private static void bootstrapUserData() throws SQLException {
        for (String schema : SCHEMAS) {
            System.out.println("Bootstrapping user data for " + schema);

            JDBConnectionWrapper connectionWrapper = new JDBConnectionWrapper(schema);
            rightsRolesRepository = new RightsRolesRepositoryMySQL(connectionWrapper.getConnection());

            bootstrapRoles();
            bootstrapRights();
            bootstrapRoleRight();
            bootstrapUserRoles();
        }
    }

    private static void bootstrapRoles() throws SQLException {
        for (String role : ROLES) {
            rightsRolesRepository.addRole(role);
        }
    }

    private static void bootstrapRights() throws SQLException {
        for (String right : RIGHTS) {
            rightsRolesRepository.addRight(right);
        }
    }

    private static void bootstrapRoleRight() throws SQLException {
        Map<String, List<String>> rolesRights = getRolesRights();

        for (String role : rolesRights.keySet()) {
            Long roleId = rightsRolesRepository.findRoleByTitle(role).getId();

            for (String right : rolesRights.get(role)) {
                Long rightId = rightsRolesRepository.findRightByTitle(right).getId();

                rightsRolesRepository.addRoleRight(roleId, rightId);
            }
        }
    }

    private static void bootstrapUserRoles() throws SQLException {

    }
}
import org.example.model.Role;
import org.example.model.User;
import org.example.model.builder.UserBuilder;
import org.example.model.validator.Notification;
import org.example.repository.security.RightsRolesRepository;
import org.example.repository.security.RightsRolesRepositoryMySQL;
import org.example.repository.user.UserRepository;
import org.example.repository.user.UserRepositoryMySQL;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserRepositoryTest {

    private Connection connection;
    private UserRepository userRepository;
    private RightsRolesRepository rightsRolesRepository;

    @BeforeAll
    void setUpDatabase() throws SQLException {
        connection = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/library?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                "root", "Alina21072003*"
        );

        // Creare tabele dacă nu există deja
        connection.createStatement().execute("CREATE TABLE IF NOT EXISTS `user` (" +
                "  `id` INT NOT NULL AUTO_INCREMENT," +
                "  `username` VARCHAR(200) NOT NULL," +
                "  `password` VARCHAR(64) NOT NULL," +
                "  PRIMARY KEY (`id`)," +
                "  UNIQUE INDEX `username_UNIQUE` (`username` ASC));");

        connection.createStatement().execute("CREATE TABLE IF NOT EXISTS `role` (" +
                "  `id` INT NOT NULL AUTO_INCREMENT," +
                "  `role` VARCHAR(100) NOT NULL," +
                "  PRIMARY KEY (`id`)," +
                "  UNIQUE INDEX `role_UNIQUE` (`role` ASC));");

        connection.createStatement().execute("CREATE TABLE IF NOT EXISTS `user_role` (" +
                "  `id` INT NOT NULL AUTO_INCREMENT," +
                "  `user_id` INT NOT NULL," +
                "  `role_id` INT NOT NULL," +
                "  PRIMARY KEY (`id`)," +
                "  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE," +
                "  FOREIGN KEY (`role_id`) REFERENCES `role`(`id`) ON DELETE CASCADE);");
    }

    @BeforeEach
    void setUp() throws SQLException {
        connection.createStatement().execute("DELETE FROM user_role;");
        connection.createStatement().execute("DELETE FROM role;");
        connection.createStatement().execute("DELETE FROM user;");

        // Adaugă roluri de test
        connection.createStatement().execute("INSERT INTO role (id, role) VALUES (1, 'ADMIN');");
        connection.createStatement().execute("INSERT INTO role (id, role) VALUES (2, 'CUSTOMER');");

        // Inițializează repositories
        rightsRolesRepository = new RightsRolesRepositoryMySQL(connection);
        userRepository = new UserRepositoryMySQL(connection, rightsRolesRepository);
    }



    @AfterAll
    void closeDatabase() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    void testSave() {
        Role customerRole = new Role(1L, "CUSTOMER", null);
        List<Role> roles = Collections.singletonList(customerRole);

        User user = new UserBuilder()
                .setUsername("user1")
                .setPassword("password1")
                .setRoles(roles)
                .build();

        Notification<Boolean> saveNotification = userRepository.save(user);

        assertFalse(saveNotification.hasErrors(), "No errors should occur during save.");
        assertTrue(saveNotification.getResult(), "User should be saved successfully.");
        assertNotNull(user.getId(), "User ID should not be null after save.");
    }


    @Test
    void testFindByUsernameAndPassword() {
        Role customerRole = new Role(2L, "CUSTOMER", null);
        List<Role> roles = Collections.singletonList(customerRole);
        User user = new UserBuilder()
                .setUsername("user2")
                .setPassword("password2")
                .setRoles(roles)
                .build();
        userRepository.save(user);

        Notification<User> userNotification = userRepository.findByUsernameAndPassword("user2", "password2");

        assertFalse(userNotification.hasErrors(), "No errors should occur during find.");
        assertNotNull(userNotification.getResult(), "User should be found.");
        assertEquals("user2", userNotification.getResult().getUsername(), "Username should match.");
    }

    @Test
    void testFindById() {

        Role customerRole = new Role(2L, "CUSTOMER", null);
        List<Role> roles = Collections.singletonList(customerRole);
        User user = new UserBuilder()
                .setUsername("user3")
                .setPassword("password3")
                .setRoles(roles)
                .build();
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findById(user.getId());

        assertTrue(foundUser.isPresent(), "User should be found by ID.");
        assertEquals("user3", foundUser.get().getUsername(), "Username should match.");
    }

    @Test
    void testDeleteById() {
        User user = new UserBuilder()
                .setUsername("user4")
                .setPassword("password4")
                .build();
        userRepository.save(user);

        boolean existsBeforeDelete = userRepository.findById(user.getId()).isPresent();
        assertTrue(existsBeforeDelete, "User should exist before deletion.");

        Notification<Boolean> deleteNotification = userRepository.deleteById(user.getId());

        assertFalse(deleteNotification.hasErrors(), "No errors should occur during deletion.");
        assertTrue(deleteNotification.getResult(), "User should be deleted successfully.");

        boolean existsAfterDelete = userRepository.findById(user.getId()).isPresent();
        assertFalse(existsAfterDelete, "User should not exist after deletion.");
    }

    @Test
    void testFindAll() {
        userRepository.save(new UserBuilder()
                .setUsername("user5")
                .setPassword("password5")
                .build());
        userRepository.save(new UserBuilder()
                .setUsername("user6")
                .setPassword("password6")
                .build());

        List<User> users = userRepository.findAll();

        assertEquals(2, users.size(), "There should be exactly two users.");
        assertEquals("user5", users.get(0).getUsername(), "First username should match.");
        assertEquals("user6", users.get(1).getUsername(), "Second username should match.");
    }

    @Test
    void testRemoveAll() {
        userRepository.save(new UserBuilder()
                .setUsername("user7")
                .setPassword("password7")
                .build());
        userRepository.save(new UserBuilder()
                .setUsername("user8")
                .setPassword("password8")
                .build());

        userRepository.removeAll();

        List<User> users = userRepository.findAll();
        assertTrue(users.isEmpty(), "All users should be removed.");
    }
}
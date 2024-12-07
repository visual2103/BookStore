package org.example.service.user;

import org.example.laucher.AdminComponentFactory;
import org.example.laucher.CustomerComponentFactory;
import org.example.laucher.EmployeeComponentFactory;
import org.example.model.Role;
import org.example.model.User;
import org.example.model.builder.UserBuilder;
import org.example.model.validator.Notification;
import org.example.model.validator.UserValidator;
import org.example.repository.security.RightsRolesRepository;
import org.example.repository.user.UserRepository;
import org.example.util.PasswordUtil ;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Collections;

import static org.example.database.Constants.Roles.CUSTOMER;

public class AuthenticationServiceMySQL implements AuthenticationService {

    private final UserRepository userRepository;
    private final RightsRolesRepository rightsRolesRepository;
    private User currentLoggedInUser; // Variabilă de clasă


    public AuthenticationServiceMySQL(UserRepository userRepository, RightsRolesRepository rightsRolesRepository) {
        this.userRepository = userRepository;
        this.rightsRolesRepository = rightsRolesRepository;
    }

    @Override
    public Notification<Boolean> register(String username, String password) {

        Role customerRole = rightsRolesRepository.findRoleByTitle(CUSTOMER);

        User user = new UserBuilder()
                .setUsername(username)
                .setPassword(password)
                .setRoles(Collections.singletonList(customerRole)) // detalii
                .build();

        UserValidator userValidator = new UserValidator(user);

        boolean userValid = userValidator.validate();
        Notification<Boolean> userRegisterNotification = new Notification<>();

        if (!userValid){
            userValidator.getErrors().forEach(userRegisterNotification::addError);
            userRegisterNotification.setResult(Boolean.FALSE);
        } else {
            String salt = PasswordUtil.generateSalt();
            String hashedPassword = PasswordUtil.hashPassword(password, salt);
            user.setPassword(hashedPassword);
            user.setSalt(salt);
            // salvez user cu parola hash uita si salt
            Notification<Boolean> saveNotification = userRepository.save(user);


            if (saveNotification.hasErrors()) {
                //  erorile de la repository
                saveNotification.getErrors().forEach(userRegisterNotification::addError);
                userRegisterNotification.setResult(Boolean.FALSE);
            } else {
                userRegisterNotification.setResult(saveNotification.getResult());
            }
        }

        return userRegisterNotification;
    }

    @Override
    public Notification<User> login(String username, String password) {
        Notification<User> userNotification = userRepository.findByUsername(username);

        if (userNotification.hasErrors()) {
            return userNotification;
        }

        User user = userNotification.getResult();
        String salt = user.getSalt();
        String hashedPassword = PasswordUtil.hashPassword(password, salt);

        if (hashedPassword.equals(user.getPassword())) {
            // Setează utilizatorul curent după autentificare reușită
            currentLoggedInUser = user;
            return userNotification; // Autentificare reușită
        } else {
            Notification<User> failedNotification = new Notification<>();
            failedNotification.addError("Invalid username or password!");
            return failedNotification;
        }
    }


    @Override
    public boolean logout() {
        AdminComponentFactory.resetInstance();
        EmployeeComponentFactory.resetInstance();
        CustomerComponentFactory.resetInstance();
        return false ;
    }

    @Override
    public Notification<User> getLoggedInUser() {
        Notification<User> notification = new Notification<>();
        if (currentLoggedInUser != null) {
            System.out.println("Logged-in user found: " + currentLoggedInUser.getUsername());
            notification.setResult(currentLoggedInUser);
        } else {
            System.out.println("No user is logged in.");
            notification.addError("No user is logged in.");
        }
        return notification;
    }


    private String hashPassword(String password) {
        try {
            // Sercured Hash Algorithm - 256
            // 1 byte = 8 biți
            // 1 byte = 1 char
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
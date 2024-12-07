package org.example.service.user;

import org.example.model.Role;
import org.example.model.User;
import org.example.model.validator.Notification;
import org.example.repository.security.RightsRolesRepository;
import org.example.repository.user.UserRepository;
import org.example.util.PasswordUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class UserServiceImplementation implements UserService {
    private final UserRepository userRepository;
    private final RightsRolesRepository rightsRolesRepository;

    public UserServiceImplementation(UserRepository userRepository , RightsRolesRepository rightsRolesRepository) {
        this.userRepository = userRepository;
        this.rightsRolesRepository = rightsRolesRepository ;
    }

    @Override
    public Notification<Boolean> addUser(User user) {
        Notification<Boolean> notification = new Notification<>();

        String username = user.getUsername();
        String password = user.getPassword();
        List<Role> roles = user.getRoles();

        // Validare date
        if (username == null || username.isEmpty()) {
            notification.addError("Username cannot be empty.");
        }
        if (password == null || password.isEmpty()) {
            notification.addError("Password cannot be empty.");
        }
        if (roles == null || roles.isEmpty()) {
            notification.addError("User must have at least one role.");
        }
        if (notification.hasErrors()) {
            notification.setResult(false);
            return notification;
        }

        // Verificare existență utilizator
        if (userRepository.existsByUsername(username)) {
            notification.addError("User with username " + username + " already exists.");
            notification.setResult(false);
            return notification;
        }

        // Generăm salt-ul și hash-uim parola
        String salt = PasswordUtil.generateSalt();
        String hashedPassword = PasswordUtil.hashPassword(password, salt);
        user.setPassword(hashedPassword);
        user.setSalt(salt);

        // Salvăm utilizatorul
        Notification<Boolean> saveNotification = userRepository.save(user);
        if (saveNotification.hasErrors()) {
            notification.addError(saveNotification.getFormattedErrors());
            notification.setResult(false);
        } else {
            notification.setResult(saveNotification.getResult());
        }
        return notification;
    }


    @Override
    public Notification<Boolean> deleteUser(Long id) {
        Notification<Boolean> notification = userRepository.deleteById(id);
        if (notification.hasErrors()) {
            System.out.println("Delete failed: " + notification.getFormattedErrors());
        }
        return notification;
    }

    @Override
    public Notification<Boolean> updateUserRole(Long id, String newRole) {
        Notification<Boolean> notification = new Notification<>();
        //find role
        Role role = rightsRolesRepository.findRoleByTitle(newRole) ;
        if ( role == null){
            notification.addError("Role not found");
            return notification ;
        }
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            notification.addError("User not found with ID: " + id);
            return notification;
        }
        User user = optionalUser.get();
        //  new role
        user.setRoles(Collections.singletonList(role));
        // update user in bd
        Notification<Boolean> updateNotification = userRepository.update(user);
        if (updateNotification.hasErrors()) {
            notification.addError(updateNotification.getFormattedErrors());
            notification.setResult(false);
        } else {
            notification.setResult(updateNotification.getResult());
        }

        return notification;
    }

    @Override
    public Notification<List<User>> getAllUsers() {
        Notification<List<User>> notification = new Notification<>();
        try {
            List<User> users = userRepository.findAll();
            notification.setResult(users);
        } catch (Exception e) {
            notification.addError("Failed to fetch users: " + e.getMessage());
        }
        return notification;
    }

    @Override
    public Notification<User> findByUsername(String username) {
        Notification<User> notification = new Notification<>();
        try{
            notification = userRepository.findByUsername(username) ;
            if (notification.hasErrors()) {
                notification.addError("User not found for username :"+ username);
            }
        }catch (Exception e){
            notification.addError("Failed to find user by username : " + e.getMessage());
        }
        return notification;
    }

}

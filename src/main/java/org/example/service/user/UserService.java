package org.example.service.user;

import org.example.model.User;
import org.example.model.validator.Notification;

import java.util.List;

public interface UserService {
    Notification<Boolean> addUser(User user);
    Notification<Boolean> deleteUser(Long id);
    Notification<Boolean> updateUserRole(Long id, String newRole);
    Notification<List<User>> getAllUsers();
    Notification<User> findByUsername(String usename) ;
}

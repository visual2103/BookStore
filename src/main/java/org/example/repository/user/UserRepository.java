package org.example.repository.user;



import org.example.model.User;
import org.example.model.validator.Notification;

import java.util.*;

public interface UserRepository {

    List<User> findAll();

    Notification<User> findByUsernameAndPassword(String username, String password);
    Notification<User> findByUsername(String username);


    Notification<Boolean> save(User user);

    void removeAll();
    Notification<Boolean> deleteById(Long id) ;
    Optional<User> findById(Long id) ;
    Notification<Boolean> update(User user) ;

    boolean existsByUsername(String username);
}
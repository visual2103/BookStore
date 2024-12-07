package org.example.model.builder;


import java.util.ArrayList;
import java.util.List;
import org.example.model.Role;
import org.example.model.User;

public class UserBuilder {

    private User user;

    public UserBuilder(){
        user = new User();
    }

    public UserBuilder setId(Long id){
        user.setId(id);
        return this;
    }

    public UserBuilder setUsername(String username){
        user.setUsername(username);
        return this;
    }

    public UserBuilder setPassword(String password){
        user.setPassword(password);
        return this;
    }
    public UserBuilder setSalt(String salt) {
        user.setSalt(salt);
        return this;
    }

    public UserBuilder setRoles(List<Role> roles){
        user.setRoles(roles);
        return this;
    }

    public User build(){
        if (user.getRoles() == null) {
            user.setRoles(new ArrayList<>()); // lista goala
        }
        return user;
    }

}
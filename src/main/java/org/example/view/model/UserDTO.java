package org.example.view.model;

import javafx.beans.property.*;

public class UserDTO {
    private LongProperty id;
    private StringProperty username;
   // private StringProperty email;
    private StringProperty role;


    public void setId(Long id) {
        idProperty().set(id);
    }

    public Long getId() {
        return idProperty().get();
    }

    public LongProperty idProperty() {
        if (id == null) {
            id = new SimpleLongProperty(this, "id");
        }
        return id;
    }

    // Username
    public void setUsername(String username) {
        usernameProperty().set(username);
    }

    public String getUsername() {
        return usernameProperty().get();
    }

    public StringProperty usernameProperty() {
        if (username == null) {
            username = new SimpleStringProperty(this, "username");
        }
        return username;
    }

    // email -> discutabil
//    public void setEmail(String email) {
//        emailProperty().set(email);
//    }
//
//    public String getEmail() {
//        return emailProperty().get();
//    }
//
//    public StringProperty emailProperty() {
//        if (email == null) {
//            email = new SimpleStringProperty(this, "email");
//        }
//        return email;
//    }

    // Role
    public void setRole(String role) {
        roleProperty().set(role);
    }

    public String getRole() {
        return roleProperty().get();
    }

    public StringProperty roleProperty() {
        if (role == null) {
            role = new SimpleStringProperty(this, "role");
        }
        return role;
    }
}

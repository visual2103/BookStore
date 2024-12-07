package org.example.view.model.builder;

import org.example.view.model.UserDTO;

public class UserDTOBuilder {
    private UserDTO userDTO;

    public UserDTOBuilder() {
        userDTO = new UserDTO();
    }

    public UserDTOBuilder setId(Long id) {
        userDTO.setId(id);
        return this;
    }

    public UserDTOBuilder setUsername(String username) {
        userDTO.setUsername(username);
        return this;
    }

//    public UserDTOBuilder setEmail(String email) {
//        userDTO.setEmail(email);
//        return this;
//    }

    public UserDTOBuilder setRole(String role) {
        userDTO.setRole(role);
        return this;
    }

    public UserDTO build() {
        return userDTO;
    }
}

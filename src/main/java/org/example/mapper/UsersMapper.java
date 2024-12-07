package org.example.mapper;

import org.example.model.Role;
import org.example.model.User;
import org.example.model.builder.UserBuilder;
import org.example.view.model.UserDTO;
import org.example.view.model.builder.UserDTOBuilder;

import java.util.List;
import java.util.stream.Collectors;

public class UsersMapper {
    //mapare intre User -> UserDTO
    public static UserDTO convertUserToUserDTO (User user){
        String roles = user.getRoles().stream()
                .map(Role::getRole)
                .collect(Collectors.joining(" , ")) ;

        return new UserDTOBuilder()
                .setId(user.getId())
                .setUsername(user.getUsername())
                .setRole(roles)
                .build() ;
    }
    //userDTO -> user
    public static User convertUserDTOToUser(UserDTO userDto , List<Role> roles){
        return new UserBuilder()
                .setId(userDto.getId())
                .setUsername(userDto.getUsername())
                .setRoles(roles)
                .build();
    }
    public static List<UserDTO> convertUserListToUserDTOList(List<User> users) {
        return users.stream()
                .map(UsersMapper::convertUserToUserDTO)
                .collect(Collectors.toList());
    }

    public static List<User> convertUserDTOListToUserList(List<UserDTO> userDTOs, List<Role> allRoles) {
        return userDTOs.stream()
                .map(userDTO -> {
                    List<Role> roles = allRoles.stream()
                            .filter(role -> userDTO.getRole().contains(role.getRole()))
                            .collect(Collectors.toList());
                    return convertUserDTOToUser(userDTO, roles);
                })
                .collect(Collectors.toList());
    }

}

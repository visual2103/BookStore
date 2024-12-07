package org.example.service.security;

import org.example.model.Right;
import org.example.model.Role;
import org.example.model.User;
import org.example.repository.security.RightsRolesRepository;
import org.example.service.security.RightsRolesService ;
import java.util.List;

public class RightsRolesServiceImplementation implements RightsRolesService {

    private final RightsRolesRepository rightsRolesRepository;

    public RightsRolesServiceImplementation(RightsRolesRepository rightsRolesRepository) {
        this.rightsRolesRepository = rightsRolesRepository;
    }

    @Override
    public void addRole(String role) {
        rightsRolesRepository.addRole(role);
    }

    @Override
    public void addRight(String right) {
        rightsRolesRepository.addRight(right);
    }

    @Override
    public Role findRoleByTitle(String role) {
        return rightsRolesRepository.findRoleByTitle(role);
    }

    @Override
    public Role findRoleById(Long roleId) {
        return rightsRolesRepository.findRoleById(roleId);
    }

    @Override
    public Right findRightByTitle(String right) {
        return rightsRolesRepository.findRightByTitle(right);
    }

    @Override
    public void addRolesToUser(User user, List<Role> roles) {
        rightsRolesRepository.addRolesToUser(user, roles);
    }

    @Override
    public List<Role> findRolesForUser(Long userId) {
        return rightsRolesRepository.findRolesForUser(userId);
    }

    @Override
    public void addRoleRight(Long roleId, Long rightId) {
        rightsRolesRepository.addRoleRight(roleId, rightId);
    }
}

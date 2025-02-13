package com.example.library.services;

import com.example.library.models.User;
import org.springframework.stereotype.Service;

@Service
public class PermissionService {

    public boolean hasPermission(User user, String permission) {
        return user.getRoles().stream()
            .flatMap(role -> role.getPermissions().stream())
            .anyMatch(p -> p.getName().equals(permission));
    }

    public boolean hasRole(User user, String roleName) {
        return user.getRoles().stream()
            .anyMatch(role -> role.getName().equalsIgnoreCase(roleName));
    }
}

package com.example.library.controllers;

import com.example.library.models.Permission;
import com.example.library.repositories.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    @Autowired
    private PermissionRepository permissionRepository;

    @GetMapping
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    @PostMapping
    public String createPermission(@RequestBody Permission permission) {
        Optional<Permission> existingPermission = permissionRepository.findByName(permission.getName());
        if (existingPermission.isPresent()) {
            return "Permission already exists!";
        }
        permissionRepository.save(permission);
        return "Permission created successfully!";
    }

    @DeleteMapping("/{id}")
    public String deletePermission(@PathVariable Long id) {
        permissionRepository.deleteById(id);
        return "Permission deleted successfully!";
    }
}

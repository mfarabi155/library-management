package com.example.library.controllers;

import com.example.library.models.Role;
import com.example.library.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @PostMapping
    public String createRole(@RequestBody Role role) {
        Optional<Role> existingRole = roleRepository.findByName(role.getName());
        if (existingRole.isPresent()) {
            return "Role already exists!";
        }
        roleRepository.save(role);
        return "Role created successfully!";
    }

    @DeleteMapping("/{id}")
    public String deleteRole(@PathVariable Long id) {
        roleRepository.deleteById(id);
        return "Role deleted successfully!";
    }
}

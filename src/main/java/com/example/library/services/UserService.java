package com.example.library.services;

import com.example.library.models.Role;
import com.example.library.models.User;
import com.example.library.repositories.RoleRepository;
import com.example.library.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role viewerRole = roleRepository.findByName("Viewer")
                .orElseThrow(() -> new RuntimeException("Default role 'Viewer' not found"));

        user.setRoles(Collections.singleton(viewerRole));

        return userRepository.save(user);
    }

    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }

    public boolean authenticate(String usernameOrEmail, String password) {
        Optional<User> userOptional = findByUsernameOrEmail(usernameOrEmail);

        return userOptional.isPresent() && passwordEncoder.matches(password, userOptional.get().getPassword());
    }
}

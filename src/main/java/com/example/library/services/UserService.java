package com.example.library.services;

import com.example.library.models.User;
import com.example.library.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
    }

    public User getUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }

    public boolean authenticate(String usernameOrEmail, String password) {
        Optional<User> userOptional = findByUsernameOrEmail(usernameOrEmail);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return passwordEncoder.matches(password, user.getPassword());
        }
        return false;
    }
}
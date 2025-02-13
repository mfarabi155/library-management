package com.example.library.controllers;

import com.example.library.models.User;
import com.example.library.services.*;
import com.example.library.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final Map<String, Integer> failedLoginAttempts = new HashMap<>();
    private final Map<String, LocalDateTime> blockedUsers = new HashMap<>();
    private final Map<String, Boolean> awaitingOtpVerification = new HashMap<>();

    @Autowired
    private UserService userService;
    @Autowired
    private OtpService otpService;
    @Autowired
    private AuditLogService auditLogService;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private VariableService variableService;

    private boolean isValidApiKey(String apiKey) {
        String storedApiKey = variableService.getVariableValue("API-KEY", "");
        return apiKey != null && apiKey.equals(storedApiKey);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestHeader("API-KEY") String apiKey, @RequestBody User user) {
        if (!isValidApiKey(apiKey)) {
            return ResponseEntity.status(403).body(Map.of("status", "error", "message", "Invalid API Key"));
        }
        try {
            userService.registerUser(user);
            return ResponseEntity.ok(Map.of("status", "success", "message", "User registered successfully!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @PostMapping("/login-step1")
    public ResponseEntity<Map<String, Object>> loginStep1(@RequestHeader("API-KEY") String apiKey, @RequestBody Map<String, String> loginRequest, HttpServletRequest request) {
        if (!isValidApiKey(apiKey)) {
            return ResponseEntity.status(403).body(Map.of("status", "error", "message", "Invalid API Key"));
        }

        String usernameOrEmail = loginRequest.get("usernameOrEmail");
        String password = loginRequest.get("password");

        if (blockedUsers.containsKey(usernameOrEmail) && LocalDateTime.now().isBefore(blockedUsers.get(usernameOrEmail).plusMinutes(30))) {
            auditLogService.log("LOGIN_FAILED_BLOCKED", usernameOrEmail, request.getRemoteAddr(), request.getHeader("User-Agent"));
            return ResponseEntity.status(403).body(Map.of("status", "error", "message", "Account is locked. Try again later."));
        }

        if (!userService.authenticate(usernameOrEmail, password)) {
            failedLoginAttempts.put(usernameOrEmail, failedLoginAttempts.getOrDefault(usernameOrEmail, 0) + 1);
            auditLogService.log("LOGIN_FAILED", usernameOrEmail, request.getRemoteAddr(), request.getHeader("User-Agent"));

            if (failedLoginAttempts.get(usernameOrEmail) >= 5) {
                blockedUsers.put(usernameOrEmail, LocalDateTime.now());
                failedLoginAttempts.remove(usernameOrEmail);
                auditLogService.log("ACCOUNT_LOCKED", usernameOrEmail, request.getRemoteAddr(), request.getHeader("User-Agent"));
                return ResponseEntity.status(403).body(Map.of("status", "error", "message", "Too many failed attempts. Account locked for 30 minutes."));
            }
            return ResponseEntity.status(401).body(Map.of("status", "error", "message", "Invalid credentials!"));
        }

        failedLoginAttempts.remove(usernameOrEmail);
        otpService.generateOtp(usernameOrEmail);
        awaitingOtpVerification.put(usernameOrEmail, true);
        auditLogService.log("OTP_SENT", usernameOrEmail, request.getRemoteAddr(), request.getHeader("User-Agent"));

        return ResponseEntity.ok(Map.of("status", "success", "message", "Password verified! OTP sent to your email. Proceed to Step 2."));
    }

    @PostMapping("/login-step2")
    public ResponseEntity<Map<String, Object>> loginStep2(@RequestHeader("API-KEY") String apiKey, @RequestBody Map<String, String> otpRequest, HttpServletRequest request) {
        if (!isValidApiKey(apiKey)) {
            return ResponseEntity.status(403).body(Map.of("status", "error", "message", "Invalid API Key"));
        }

        String usernameOrEmail = otpRequest.get("usernameOrEmail");
        String otpCode = otpRequest.get("otpCode");

        if (!awaitingOtpVerification.getOrDefault(usernameOrEmail, false)) {
            return ResponseEntity.status(400).body(Map.of("status", "error", "message", "You must verify your password first!"));
        }

        if (otpService.validateOtp(usernameOrEmail, otpCode)) {
            awaitingOtpVerification.remove(usernameOrEmail);
            String token = jwtUtils.generateToken(usernameOrEmail);
            auditLogService.log("LOGIN_SUCCESS", usernameOrEmail, request.getRemoteAddr(), request.getHeader("User-Agent"));

            return ResponseEntity.ok(Map.of("status", "success", "message", "Login successful!", "data", Map.of("token", token)));
        }

        auditLogService.log("LOGIN_FAILED_OTP", usernameOrEmail, request.getRemoteAddr(), request.getHeader("User-Agent"));
        return ResponseEntity.status(401).body(Map.of("status", "error", "message", "Invalid OTP!"));
    }
}

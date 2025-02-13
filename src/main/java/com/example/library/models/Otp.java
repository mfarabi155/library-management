package com.example.library.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Otp {
    @Id
    private String email;
    private String otpCode;
    private LocalDateTime expiryTime;

    public Otp() {}

    public Otp(String email, String otpCode, LocalDateTime expiryTime) {
        this.email = email;
        this.otpCode = otpCode;
        this.expiryTime = expiryTime;
    }

    public String getEmail() {
        return email;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }

    public boolean isExpired() {
        return expiryTime.isBefore(LocalDateTime.now());
    }
}

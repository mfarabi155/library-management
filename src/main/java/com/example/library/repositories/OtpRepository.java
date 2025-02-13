package com.example.library.repositories;

import com.example.library.models.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, String> {
    Optional<Otp> findByEmail(String email);
}

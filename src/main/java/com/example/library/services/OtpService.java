package com.example.library.services;

import com.example.library.models.Otp;
import com.example.library.repositories.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private JavaMailSender mailSender;

    public String generateOtp(String email) {
        String otpCode = String.format("%06d", new Random().nextInt(999999));
        Otp otp = new Otp(email, otpCode, LocalDateTime.now().plusMinutes(5));

        otpRepository.save(otp);
        sendOtpEmail(email, otpCode);

        return otpCode;
    }

    public boolean validateOtp(String email, String otpCode) {
        Optional<Otp> otp = otpRepository.findByEmail(email);
        return otp.isPresent() && !otp.get().isExpired() && otp.get().getOtpCode().equals(otpCode);
    }

    private void sendOtpEmail(String email, String otpCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP code is: " + otpCode + ". It will expire in 5 minutes.");
        mailSender.send(message);
    }     
}

package com.example.library.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action;
    private String username;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime timestamp;

    public AuditLog() {}

    public AuditLog(String action, String username, String ipAddress, String userAgent) {
        this.action = action;
        this.username = username;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.timestamp = LocalDateTime.now();
    }
}

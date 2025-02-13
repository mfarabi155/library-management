package com.example.library.services;

import com.example.library.models.AuditLog;
import com.example.library.repositories.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    public void log(String action, String username, String ipAddress, String userAgent) {
        AuditLog log = new AuditLog(action, username, ipAddress, userAgent);
        auditLogRepository.save(log);
    }
}

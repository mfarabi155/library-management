package com.example.library.controllers;

import com.example.library.models.Library;
import com.example.library.models.User;
import com.example.library.repositories.LibraryRepository;
import com.example.library.services.PermissionService;
import com.example.library.services.UserService;
import com.example.library.services.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/libraries")
public class LibraryController {

    @Autowired
    private LibraryRepository libraryRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private AuditLogService auditLogService;

    @GetMapping
    public List<Library> getAllLibraries() {
        return libraryRepository.findAll();
    }

    @PostMapping
    public String createLibrary(@RequestBody Library library, 
                                @RequestParam Long userId, 
                                HttpServletRequest request) {
        User user = userService.getUserById(userId);

        if (!permissionService.hasPermission(user, "CREATE_ARTICLE")) {
            auditLogService.log("CREATE_ARTICLE_DENIED", user.getUsername(), 
                                request.getRemoteAddr(), request.getHeader("User-Agent"));
            return "Permission denied";
        }

        library.setAuthor(user);
        libraryRepository.save(library);
        auditLogService.log("CREATE_ARTICLE_SUCCESS", user.getUsername(), 
                            request.getRemoteAddr(), request.getHeader("User-Agent"));
        return "Article created successfully!";
    }

    @DeleteMapping("/{id}")
    public String deleteLibrary(@PathVariable Long id, 
                                @RequestParam Long userId, 
                                HttpServletRequest request) {
        User user = userService.getUserById(userId);

        if (!permissionService.hasPermission(user, "DELETE_ARTICLE")) {
            auditLogService.log("DELETE_ARTICLE_DENIED", user.getUsername(), 
                                request.getRemoteAddr(), request.getHeader("User-Agent"));
            return "Permission denied";
        }

        libraryRepository.deleteById(id);
        auditLogService.log("DELETE_ARTICLE_SUCCESS", user.getUsername(), 
                            request.getRemoteAddr(), request.getHeader("User-Agent"));
        return "Article deleted successfully!";
    }
}

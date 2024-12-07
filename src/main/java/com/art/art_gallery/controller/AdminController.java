package com.art.art_gallery.controller;

import com.art.art_gallery.model.Admin;
import com.art.art_gallery.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    @Autowired
    private AdminRepository adminRepository;

    // Register admin with optional image
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerAdmin(@RequestBody Admin admin) {
        // Check if the username already exists in the admin repository
        if (adminRepository.findByUsername(admin.getUsername()) != null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Username already exists"));
        }

        // Simple password encryption (not secure for production)
        admin.setPassword(simpleHash(admin.getPassword())); // Apply simple password hashing (for demo purposes)

        // Save the admin to the repository
        adminRepository.save(admin);

        // Return success message
        return ResponseEntity.ok(Map.of("message", "Admin registered successfully!"));
    }

    // Login admin
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginAdmin(@RequestBody Admin loginRequest) {
        Admin existingAdmin = adminRepository.findByUsername(loginRequest.getUsername());

        if (existingAdmin != null && simpleHash(loginRequest.getPassword()).equals(existingAdmin.getPassword())) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Login successful!");
            response.put("role", existingAdmin.getRole());
            response.put("adminId", existingAdmin.getId().toString());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid username or password"));
        }
    }

    // Get Admin profile by ID
    @GetMapping("/{id}")
    public ResponseEntity<Admin> getAdminProfile(@PathVariable Long id) {
        return adminRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Update Admin profile with optional image
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, String>> updateAdminProfile(
            @PathVariable Long id, 
            @RequestParam("name") String name, 
            @RequestParam("email") String email,
            @RequestParam("role") String role, 
            @RequestParam(value = "image", required = false) MultipartFile image) {

        return adminRepository.findById(id)
                .map(admin -> {
                    admin.setName(name);
                    admin.setEmail(email);
                    admin.setRole(role);

                    // Handle image upload if provided
                    if (image != null && !image.isEmpty()) {
                        try {
                            // Create uploads directory if it doesn't exist
                            Path uploadPath = Paths.get(UPLOAD_DIR);
                            if (!Files.exists(uploadPath)) {
                                Files.createDirectories(uploadPath);
                            }

                            // Save the image file to the uploads folder
                            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                            Path filePath = uploadPath.resolve(fileName);
                            Files.copy(image.getInputStream(), filePath);

                            admin.setImagePath("/uploads/" + fileName);
                        } catch (IOException e) {
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Failed to upload image."));
                        }
                    }

                    adminRepository.save(admin);
                    return ResponseEntity.ok(Map.of("message", "Admin profile updated successfully!"));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Admin not found")));
    }

    // Simple password hashing (not recommended for real applications)
    private String simpleHash(String input) {
        return String.valueOf(input.hashCode());
    }
}

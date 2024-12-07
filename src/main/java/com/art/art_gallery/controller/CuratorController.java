package com.art.art_gallery.controller;

import com.art.art_gallery.model.Curator;
import com.art.art_gallery.repository.CuratorRepository;
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/curators")
public class CuratorController {

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    @Autowired
    private CuratorRepository curatorRepository;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerCurator(@RequestBody Curator curator) {
        // Check if the username already exists in the curator repository
        if (curatorRepository.findByUsername(curator.getUsername()) != null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Username already exists"));
        }

        // Simple password encryption (not secure for production)
        curator.setPassword(simpleHash(curator.getPassword())); // Apply simple password hashing (for demo purposes)

        // Save the curator to the repository
        curatorRepository.save(curator);

        // Return success message
        return ResponseEntity.ok(Map.of("message", "Curator registered successfully!"));
    }


    // Curator login
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginCurator(@RequestBody Curator loginRequest) {
        Curator existingCurator = curatorRepository.findByUsername(loginRequest.getUsername());

        if (existingCurator != null && existingCurator.getPassword().equals(simpleHash(loginRequest.getPassword()))) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Login successful!");
            response.put("role", existingCurator.getRole());
            response.put("curatorId", existingCurator.getId().toString());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid username or password"));
        }
    }

    // Get curator profile
    @GetMapping("/{id}")
    public ResponseEntity<Curator> getCuratorProfile(@PathVariable Long id) {
        return curatorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Update curator profile
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, String>> updateCuratorProfile(
            @PathVariable Long id, @RequestParam("name") String name, @RequestParam("email") String email,
            @RequestParam("role") String role, @RequestParam(value = "image", required = false) MultipartFile image) {

        return curatorRepository.findById(id)
                .map(curator -> {
                    curator.setName(name);
                    curator.setEmail(email);
                    curator.setRole(role);

                    // Handle image upload if provided
                    if (image != null && !image.isEmpty()) {
                        try {
                            // Create the uploads directory if it doesn't exist
                            Path uploadPath = Paths.get(UPLOAD_DIR);
                            if (!Files.exists(uploadPath)) {
                                Files.createDirectories(uploadPath);
                            }

                            // Save the image file to the uploads folder
                            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                            Path filePath = uploadPath.resolve(fileName);
                            Files.copy(image.getInputStream(), filePath);

                            curator.setImagePath("/uploads/" + fileName);
                        } catch (IOException e) {
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Failed to upload image."));
                        }
                    }

                    curatorRepository.save(curator);
                    return ResponseEntity.ok(Map.of("message", "Curator profile updated successfully!"));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Curator not found")));
    }

    private String simpleHash(String password) {
        return String.valueOf(password.hashCode());  // Using hashCode() to hash the password
    }
}

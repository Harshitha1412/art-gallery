package com.art.art_gallery.controller;

import org.springframework.beans.factory.annotation.Autowired;
import java.nio.file.StandardCopyOption;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpStatus;

import org.springframework.web.multipart.MultipartFile;
import com.art.art_gallery.model.User;
import com.art.art_gallery.repository.UserRepository;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;



@RestController
@RequestMapping("/api")
public class UserController {
	private static final String UPLOAD_DIR = "uploads";

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("API is working!");
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Username already exists"));
        }

        // Simple password encryption (not secure for production)
        user.setPassword(simpleHash(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "User registered successfully!"));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody User loginRequest) {
        User existingUser = userRepository.findByUsername(loginRequest.getUsername());

        if (existingUser != null) {
            // Hash the login password to compare with the stored hash
            String hashedPassword = simpleHash(loginRequest.getPassword());

            if (hashedPassword.equals(existingUser.getPassword())) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Login successful!");
                response.put("role", existingUser.getRole());
                response.put("userId", existingUser.getId().toString());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(401).body(Map.of("message", "Invalid password"));
            }
        } else {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid username"));
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserProfile(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).build());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, String>> updateUserProfile(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam(value = "role", required = false) String role,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            user.setName(name);
            user.setEmail(email);
            if (role != null) {
                user.setRole(role);
            }

            if (image != null && !image.isEmpty()) {
                try {
                    Path uploadPath = Paths.get(UPLOAD_DIR);
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }

                    String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                    user.setImagePath("/uploads/" + fileName);
                } catch (IOException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                         .body(Map.of("message", "Failed to upload image."));
                }
            }

            userRepository.save(user);
            return ResponseEntity.ok(Map.of("message", "User profile updated successfully!"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
        }
    }

    

    
    private String simpleHash(String input) {
        // Simple hashing (not recommended for real applications)
        return String.valueOf(input.hashCode());
    }
    
}

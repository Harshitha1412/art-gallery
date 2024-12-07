package com.art.art_gallery.controller;

import com.art.art_gallery.model.Artist;
import com.art.art_gallery.repository.ArtistRepository;
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
@RequestMapping("/api/artists")
public class ArtistController {

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    @Autowired
    private ArtistRepository artistRepository;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerArtist(@RequestBody Artist artist) {
        // Check if username already exists
        if (artistRepository.findByUsername(artist.getUsername()) != null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Username already exists"));
        }

        // Simple password encryption (not secure for production)
        artist.setPassword(simpleHash(artist.getPassword()));

        // Set a default image path if no image is provided (optional)
        if (artist.getImagePath() == null || artist.getImagePath().isEmpty()) {
            artist.setImagePath("/uploads/default.jpg");  // Set a default image path
        }

        // Save the artist to the repository
        artistRepository.save(artist);

        // Return success message
        return ResponseEntity.ok(Map.of("message", "Artist registered successfully!"));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginArtist(@RequestBody Artist loginRequest) {
        Artist existingArtist = artistRepository.findByUsername(loginRequest.getUsername());

        if (existingArtist != null && simpleHash(loginRequest.getPassword()).equals(existingArtist.getPassword())) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Login successful!");
            response.put("role", existingArtist.getRole());
            response.put("artistId", existingArtist.getId().toString());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid username or password"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Artist> getArtistProfile(@PathVariable Long id) {
        return artistRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, String>> updateArtistProfile(
            @PathVariable Long id, @RequestParam("name") String name, @RequestParam("email") String email,
            @RequestParam("role") String role, @RequestParam(value = "image", required = false) MultipartFile image) {

        return artistRepository.findById(id)
                .map(artist -> {
                    artist.setName(name);
                    artist.setEmail(email);
                    artist.setRole(role);

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

                            artist.setImagePath("/uploads/" + fileName);
                        } catch (IOException e) {
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Failed to upload image."));
                        }
                    }

                    artistRepository.save(artist);
                    return ResponseEntity.ok(Map.of("message", "Artist profile updated successfully!"));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Artist not found")));
    }

    private String simpleHash(String input) {
        // Very simple hashing (not recommended for real applications)
        return String.valueOf(input.hashCode());
    }
}

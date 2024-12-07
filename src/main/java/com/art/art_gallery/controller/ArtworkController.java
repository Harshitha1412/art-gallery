package com.art.art_gallery.controller;

import com.art.art_gallery.model.Artwork;
import com.art.art_gallery.repository.ArtworkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/artworks")
public class ArtworkController {

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    @Autowired
    private ArtworkRepository artworkRepository;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> uploadArtwork(
            @RequestParam("title") String title,
            @RequestParam(value = "artist", required = false) String artist, // Artist is optional
            @RequestParam("cost") String cost,
            @RequestParam("description") String description,
            @RequestParam("category") String category, // New field for category
            @RequestParam("image") MultipartFile image) {

        // Validate file
        if (image.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Image file is required.");
        }

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

            // Save artwork data to the database including the new category
            Artwork artwork = new Artwork(
                    title,
                    artist != null ? artist : "Unknown Artist", // Default to "Unknown Artist" if artist is not provided
                    cost,
                    description,
                    "/uploads/" + fileName,
                    category
            );
            artworkRepository.save(artwork);

            return ResponseEntity.status(HttpStatus.CREATED).body(artwork);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image.");
        }
    }

    @GetMapping
    public ResponseEntity<List<Artwork>> getAllArtworks() {
        // Fetch all artworks from the database
        List<Artwork> artworks = artworkRepository.findAll();
        return ResponseEntity.ok(artworks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Artwork> getArtworkById(@PathVariable Long id) {
        return artworkRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}

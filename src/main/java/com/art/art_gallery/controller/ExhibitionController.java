package com.art.art_gallery.controller;

import com.art.art_gallery.model.Exhibition;
import com.art.art_gallery.service.ExhibitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exhibitions")
public class ExhibitionController {

    @Autowired
    private ExhibitionService exhibitionService;

    // Create a new exhibition
    @PostMapping
    public ResponseEntity<Exhibition> createExhibition(@RequestBody Exhibition exhibition) {
        if (exhibition.getTitle() == null || exhibition.getDescription() == null ||
            exhibition.getDates() == null || exhibition.getPlace() == null || exhibition.getOrganizerName() == null) {
            return ResponseEntity.badRequest().body(null); // Ensure all fields are present
        }
        Exhibition createdExhibition = exhibitionService.createExhibition(exhibition);
        return ResponseEntity.ok(createdExhibition);
    }

    // Get all exhibitions
    @GetMapping
    public ResponseEntity<List<Exhibition>> getAllExhibitions() {
        List<Exhibition> exhibitions = exhibitionService.getAllExhibitions();
        return ResponseEntity.ok(exhibitions);
    }

    // Get a specific exhibition by ID
    @GetMapping("/{id}")
    public ResponseEntity<Exhibition> getExhibitionById(@PathVariable Long id) {
        Exhibition exhibition = exhibitionService.getExhibitionById(id);
        if (exhibition == null) {
            return ResponseEntity.notFound().build(); // Handle case where ID is not found
        }
        return ResponseEntity.ok(exhibition);
    }

    // Update an existing exhibition
    @PutMapping("/{id}")
    public ResponseEntity<Exhibition> updateExhibition(@PathVariable Long id, @RequestBody Exhibition updatedExhibition) {
        Exhibition exhibition = exhibitionService.updateExhibition(id, updatedExhibition);
        if (exhibition == null) {
            return ResponseEntity.notFound().build(); // Handle case where ID is not found
        }
        return ResponseEntity.ok(exhibition);
    }

   


    // Delete an exhibition
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExhibition(@PathVariable Long id) {
        exhibitionService.deleteExhibition(id);
        return ResponseEntity.noContent().build();
    }
}

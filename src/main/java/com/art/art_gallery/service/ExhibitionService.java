package com.art.art_gallery.service;

import com.art.art_gallery.model.Exhibition;
import com.art.art_gallery.repository.ExhibitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExhibitionService {

    @Autowired
    private ExhibitionRepository exhibitionRepository;

    // Create a new exhibition
    public Exhibition createExhibition(Exhibition exhibition) {
        return exhibitionRepository.save(exhibition);
    }

    // Get all exhibitions
    public List<Exhibition> getAllExhibitions() {
        return exhibitionRepository.findAll();
    }

    // Get an exhibition by ID
    public Exhibition getExhibitionById(Long id) {
        return exhibitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exhibition not found with ID: " + id));
    }

    // Update an existing exhibition
    public Exhibition updateExhibition(Long id, Exhibition updatedExhibition) {
        Exhibition existingExhibition = getExhibitionById(id);

        existingExhibition.setTitle(updatedExhibition.getTitle());
        existingExhibition.setDescription(updatedExhibition.getDescription());
        existingExhibition.setDates(updatedExhibition.getDates());
        existingExhibition.setPlace(updatedExhibition.getPlace());
        existingExhibition.setOrganizerName(updatedExhibition.getOrganizerName()); // Update organizer name

        return exhibitionRepository.save(existingExhibition);
    }

    // Delete an exhibition by ID
    public void deleteExhibition(Long id) {
        if (!exhibitionRepository.existsById(id)) {
            throw new RuntimeException("Exhibition not found with ID: " + id);
        }
        exhibitionRepository.deleteById(id);
    }
}

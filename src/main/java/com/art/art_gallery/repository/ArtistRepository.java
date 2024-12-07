package com.art.art_gallery.repository;

import com.art.art_gallery.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistRepository extends JpaRepository<Artist, Long> {
    Artist findByUsername(String username);  // Custom query method to find artist by username
}

package com.art.art_gallery.repository;

import com.art.art_gallery.model.Artwork;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtworkRepository extends JpaRepository<Artwork, Long> {

	
}

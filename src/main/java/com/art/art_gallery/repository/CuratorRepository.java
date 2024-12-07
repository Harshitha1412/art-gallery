package com.art.art_gallery.repository;


import com.art.art_gallery.model.Curator;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CuratorRepository extends JpaRepository<Curator, Long> {
    Curator findByUsername(String username);  // Custom query method to find artist by username

	void save(Curator curator);
}

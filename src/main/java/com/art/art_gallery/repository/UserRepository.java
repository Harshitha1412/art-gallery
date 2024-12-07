package com.art.art_gallery.repository;

import org.springframework.data.jpa.repository.JpaRepository;


import com.art.art_gallery.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	User findByUsername(String username);
	

	boolean existsByUsername(String username);
}

package com.art.art_gallery.repository;

import com.art.art_gallery.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Admin findByUsername(String username);

}

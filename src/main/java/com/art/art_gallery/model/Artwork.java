package com.art.art_gallery.model;

import jakarta.persistence.*;

@Entity
@Table(name = "artworks")
public class Artwork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // Title of the artwork

    @Column(nullable = false)
    private String artist; // Artist of the artwork

    @Column(nullable = false)
    private String cost; // Cost of the artwork

    @Column(nullable = false, length = 1000)
    private String description; // Description of the artwork

    @Column(nullable = false)
    private String imagePath; // Path to the image file
    
    @Column(nullable = false)
    private String category; // Category of the artwork (e.g., Painting, Sculpture, etc.)

    public Artwork() {
    }

    public Artwork(String title, String artist, String cost, String description, String imagePath, String category) {
        this.title = title;
        this.artist = artist;
        this.cost = cost;
        this.description = description;
        this.imagePath = imagePath;
        this.category = category;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}

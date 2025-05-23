package com.example.paksahara.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Product {
    private int id;
    private String title;
    private String description;
    private String imageUrl;
    private double price;
    private int stock;
    private int categoryId;
    private String categoryName;
    private String status;

    public Product(int id, String title, String description, String imageUrl, double price, int stock,
                   int categoryId, String categoryName, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.stock = stock;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.status = status;
    }

    // Getters...
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    //public LocalDateTime getDateAdded() { return dateAdded; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
    public int getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName; }
    public String getStatus() { return status; }

    // Setters for update functionality
    public void setTitle(String title) {
        this.title = title;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

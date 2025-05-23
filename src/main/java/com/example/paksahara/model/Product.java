package com.example.paksahara.model;

import java.sql.ResultSet;
import java.sql.SQLException;
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
    public String getImageUrl() {
        return (this.imageUrl != null && !this.imageUrl.isEmpty())
                ? this.imageUrl
                : "icon.png"; // Default if null/empty
    }
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

    public Product(ResultSet rs) throws SQLException {
        this.id = rs.getInt("product_id");
        this.title = rs.getString("title");
        this.description = rs.getString("description");
        this.imageUrl = rs.getString("image_url");
        this.price = rs.getDouble("price");
        this.stock = rs.getInt("stock");
        this.categoryId = rs.getInt("category_id");
        this.categoryName = rs.getString("category"); // alias in your SQL
        this.status = rs.getString("status");
    }

}

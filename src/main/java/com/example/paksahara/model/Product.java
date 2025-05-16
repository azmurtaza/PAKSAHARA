package com.example.paksahara.model;

public class Product {
    private int productID;
    private String name;
    private String description;
    private String imagePath;
    private double price;
    private int stockQuantity;

    public Product(int productID, String name, String description, double price, int stockQuantity,  String imagePath) {
        this.productID = productID;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.imagePath = imagePath;
    }

    /**
     * Create a product without specifying an ID;
     * the DAO will auto-generate it for you.
     */
    public Product(String name,
                   String description,
                   double price,
                   int stockQuantity,
                   String imagePath) {
        // give it a dummy 0; the DAO will replace it with the real ID
        this(0, name, description, price, stockQuantity, imagePath);
    }


    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public double getPrice() {
        return price;
    }

    public int getProductID() {
        return productID;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }


    public void updateStock(int quantity) {
        this.stockQuantity = quantity;
        System.out.println("Stock updated. New stock: " + stockQuantity);
    }

    public void reduceStock(int quantity) {
        if (quantity > 0 && stockQuantity >= quantity) {
            stockQuantity -= quantity;
            System.out.println(quantity + " units sold. Remaining stock: " + stockQuantity);
        } else {
            System.out.println("Insufficient stock to reduce!");
        }
    }
}

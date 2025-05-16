package com.example.paksahara.model;

import java.util.ArrayList;
import java.util.List;

public class Inventory implements Manageable<Product> {
    private List<Product> products;

    public Inventory() {
        this.products = new ArrayList<>();
    }

    @Override
    public void add(Product product) {
        products.add(product);
        System.out.println("Product added: " + product.getName());
    }

    @Override
    public void update(Product updatedProduct) {
        for (int i = 0; i < products.size(); i++) {
            Product currentProduct = products.get(i);
            if (currentProduct.getProductID() == updatedProduct.getProductID()) {
                products.set(i, updatedProduct);
                System.out.println("Product updated: " + updatedProduct.getName());
                return;
            }
        }
        System.out.println("Product not found with ID: " + updatedProduct.getProductID());
    }

    @Override
    public void delete(Product productToDelete) {
        if (products.removeIf(p -> p.getProductID() == productToDelete.getProductID())) {
            System.out.println("Product deleted: " + productToDelete.getName());
        } else {
            System.out.println("Product not found with ID: " + productToDelete.getProductID());
        }
    }

    // Helper method to get a product by ID
    public Product getProduct(int productID) {
        for (Product product : products) {
            if (product.getProductID() == productID) {
                return product;
            }
        }
        System.out.println("Product not found with ID: " + productID);
        return null;
    }

    // Helper method to view all products
    public void viewAllProducts() {
        if (products.isEmpty()) {
            System.out.println("No products available in inventory.");
        } else {
            System.out.println("Available Products:");
            for (Product product : products) {
                System.out.println("- " + product.getName() + " | Stock: " + product.getStockQuantity());
            }
        }
    }
}

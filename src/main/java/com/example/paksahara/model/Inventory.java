package com.example.paksahara.model;
import java.util.ArrayList;
import java.util.List;
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
        System.out.println("Product added: " + product.getTitle());
    }


    @Override
    public void update(Product updatedProduct) {
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            if (p.getId() == updatedProduct.getId()) {
                products.set(i, updatedProduct);
                System.out.println("Product updated: " + updatedProduct.getTitle());
                return;
            }
        }
        System.out.println("Product not found with ID: " + updatedProduct.getId());
    }


    @Override
    public void delete(Product productToDelete) {
        if (products.removeIf(p -> p.getId() == productToDelete.getId())) {
            System.out.println("Product deleted: " + productToDelete.getTitle());
        } else {
            System.out.println("Product not found with ID: " + productToDelete.getId());
        }
    }

    // Helper method to get a product by ID
    public Product getProduct(int productID) {
        return products.stream()
                .filter(p -> p.getId() == productID)
                .findFirst().orElse(null);
    }

    // Helper method to view all products
    public void viewAllProducts() {
        if (products.isEmpty()) System.out.println("No products available.");
        else products.forEach(p ->
                System.out.println("- " + p.getTitle() + " | Stock: " + p.getStock()));
    }
}


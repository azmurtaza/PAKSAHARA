package com.example.paksahara.model;

import java.util.HashMap;
import java.util.Map;

public class ShoppingCart {
    public Map<Product, Integer> getProducts() {
        return products;
    }

    private int cartID;
    private Map<Product, Integer> products;

    public ShoppingCart(){
        products = new HashMap<>();
    }

    public void setCartID(int cartID) {
        this.cartID = cartID;
    }

    public void addProduct(Product product, int quantity){
        if(products.containsKey(product)){
            products.put(product, products.get(product) + quantity);
        }
        else{
            products.put(product, quantity);
        }
    }

    public void removeProduct(Product product){
        products.remove(product);
    }

    public int getTotalItems() {
        int total = 0;
        for (int quantity : products.values()) {
            total += quantity;
        }
        return total;
    }

    public double getTotalPrice() {
        double total = 0.0;
        for (Map.Entry<Product, Integer> entry : products.entrySet()) {
            total += entry.getKey().getPrice() * entry.getValue();
        }
        return total;
    }

    public void clearCart() {
        products.clear();
    }
}

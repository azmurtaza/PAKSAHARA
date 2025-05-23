package com.example.paksahara.model;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
public class Customer extends User{
    private String shippingAddress;
    private String phoneNumber;
    private Map<Product, Integer> shoppingCart;

    public Customer(int userID, String name, String email, String password,
                    String shippingAddress, String phoneNumber) {
        super(userID, name, email, password, "END_USER");
        this.shippingAddress = shippingAddress;
        this.phoneNumber = phoneNumber;
        this.shoppingCart = new HashMap<>();
    }



    public void addToCart(Product product) {
        shoppingCart.put(product, shoppingCart.getOrDefault(product, 0) + 1);
        System.out.println(product.getTitle() + " added to cart."); // Corrected to getTitle()
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void register(){
        System.out.println("Customer registered: " + getName());
        // Additional registration logic can be added here
    }

    public ArrayList<Product> viewProducts = new ArrayList<Product>();

    public Order checkout(){
        if (shoppingCart.isEmpty()) {
            System.out.println("Shopping cart is empty. Cannot proceed to checkout.");
            return null;
        }

        int generatedOrderId = (int)(Math.random() * 10000); // Random Order ID for now
        Date orderDate = new Date(); // Current date
        double totalAmount = 0.0;

        for (Map.Entry<Product, Integer> entry : shoppingCart.entrySet()) {
            totalAmount += entry.getKey().getPrice() * entry.getValue();
        }

        Order order = new Order(
                generatedOrderId,
                this,
                orderDate,
                totalAmount,
                "Pending",
                new HashMap<>(shoppingCart)
        );

        shoppingCart.clear(); // Clear the cart after checkout
        System.out.println("Order placed successfully. Order ID: " + order.getOrderID());
        return order;
    }
}

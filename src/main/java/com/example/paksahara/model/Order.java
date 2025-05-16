package com.example.paksahara.model;

import java.lang.Object;
import java.util.Date;
import java.util.Map;
public class Order {
    private static int idCounter = 1; // For generating unique order IDs
    private int orderID;
    private Customer customer;
    private Date orderDate;
    private double totalAmount;

    public static void setIdCounter(int idCounter) {
        Order.idCounter = idCounter;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setProducts(Map<Product, Integer> products) {
        this.products = products;
    }

    private String status;
    private Map<Product, Integer> products;

    public Order(int orderID,
                 Customer customer,
                 Date orderDate,
                 double totalAmount,
                 String status,
                 Map<Product, Integer> products) {
        this.orderID     = orderID;                   // honor passed ID
        Order.idCounter = Math.max(orderID + 1, idCounter);
        this.customer    = customer;
        this.orderDate   = orderDate;
        this.totalAmount = totalAmount;
        this.status      = status;                    // use passed status
        this.products    = products;
    }

    private double calculateTotalAmount() {
        double total = 0.0;
        for (Map.Entry<Product, Integer> entry : products.entrySet()) {
            total += entry.getKey().getPrice() * entry.getValue();
        }
        return total;
    }


    public int getOrderID() {
        return orderID;
    }

    public Customer getCustomer() {
        return customer;
    }

    public static int getIdCounter() {
        return idCounter;
    }

    public Map<Product, Integer> getProducts() {
        return products;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }

    //override toString() for better readability
    @Override
    public String toString() {
        return "Order ID: " + orderID +
                ", Customer: " + customer +
                "Order Date: " + orderDate +
                "Total Amount: " + totalAmount +
                "Satus: " + status;
    }
    public void updateStatus(String status) {
        this.status = status;
    }

}

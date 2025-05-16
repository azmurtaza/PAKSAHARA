package com.example.paksahara.model;

import java.util.ArrayList;
import java.util.List;

public class Admin extends User implements Manageable<Order> {
    private List<Order> orders;

    public Admin(int userID, String name, String email, String password) {
        super(userID, name, email, password);
        this.orders = new ArrayList<>();
    }

    @Override
    public void add(Order order) {
        orders.add(order);
        System.out.println("Order added: " + order);
    }

    @Override
    public void update(Order updatedOrder) {
        for (int i = 0; i < orders.size(); i++) {
            Order currentOrder = orders.get(i);
            if (currentOrder.getOrderID() == updatedOrder.getOrderID()) {
                orders.set(i, updatedOrder);
                System.out.println("Order updated: " + updatedOrder);
                return;
            }
        }
        System.out.println("Order not found with ID: " + updatedOrder.getOrderID());
    }

    @Override
    public void delete(Order orderToDelete) {
        if (orders.removeIf(order -> order.getOrderID() == orderToDelete.getOrderID())) {
            System.out.println("Order deleted: " + orderToDelete);
        } else {
            System.out.println("Order not found with ID: " + orderToDelete.getOrderID());
        }
    }

    public void viewOrders() {
        if (orders.isEmpty()) {
            System.out.println("No orders to display.");
        } else {
            System.out.println("List of Orders:");
            for (Order order : orders) {
                System.out.println(order);
            }
        }
    }
}

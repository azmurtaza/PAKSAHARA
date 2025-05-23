package com.example.paksahara.model;

import java.util.ArrayList;
import java.util.List;

public class Admin extends User implements Manageable<Order> {
    private final List<Order> orders = new ArrayList<>();

    public Admin(int userID, String name, String email, String role) {
        super(userID, name, email, role);
    }

    @Override
    public void add(Order order) {
        orders.add(order);
    }

    @Override
    public void update(Order updatedOrder) {
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getOrderID() == updatedOrder.getOrderID()) {
                orders.set(i, updatedOrder);
                return;
            }
        }
    }

    @Override
    public void delete(Order order) {
        orders.removeIf(o -> o.getOrderID() == order.getOrderID());
    }

    public List<Order> viewOrders() {
        return List.copyOf(orders);
    }
}

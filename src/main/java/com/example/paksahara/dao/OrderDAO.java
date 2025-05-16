package com.example.paksahara.dao;

import com.example.paksahara.model.Order;
import java.util.List;

public interface OrderDAO {
    void addOrder(Order order);
    Order findOrderById(int orderID);
    List<Order> findAllOrders();
    void updateOrder(Order order);
    void deleteOrder(int orderID);
}

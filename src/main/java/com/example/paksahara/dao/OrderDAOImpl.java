package com.example.paksahara.dao;

import com.example.paksahara.model.Order;
import com.example.paksahara.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAOImpl implements OrderDAO {
    private Connection connection;

    public OrderDAOImpl() {
        try {
            this.connection = DatabaseUtil.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addOrder(Order order) {
        String sql = "INSERT INTO orders (orderID, customerID, orderDate, totalAmount, status) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, order.getOrderID());
            stmt.setInt(2, order.getCustomer().getUserID());
            stmt.setDate(3, new java.sql.Date(order.getOrderDate().getTime()));
            stmt.setDouble(4, order.getTotalAmount());
            stmt.setString(5, order.getStatus());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Order findOrderById(int orderID) {
        String sql = "SELECT * FROM orders WHERE orderID=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, orderID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // you’ll need getters on Order for all fields
                return new Order(
                        rs.getInt("orderID"),
                        /* customer */ null,
                        rs.getDate("orderDate"),
                        rs.getDouble("totalAmount"),
                        rs.getString("status"),
                        /* product map */ null
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Order> findAllOrders() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Order(
                        rs.getInt("orderID"),
                        /* customer */ null,
                        rs.getDate("orderDate"),
                        rs.getDouble("totalAmount"),
                        rs.getString("status"),
                        /* product map */ null
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void updateOrder(Order order) {
        String sql = "UPDATE orders SET status=? WHERE orderID=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, order.getStatus());
            stmt.setInt(2, order.getOrderID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteOrder(int orderID) {
        String sql = "DELETE FROM orders WHERE orderID=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, orderID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

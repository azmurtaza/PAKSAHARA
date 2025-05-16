package com.example.paksahara.dao;

import com.example.paksahara.model.Customer;
import com.example.paksahara.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAOImpl implements CustomerDAO {
    private Connection connection;

    // Constructor wraps getConnection in try/catch
    public CustomerDAOImpl() {
        try {
            this.connection = DatabaseUtil.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to connect to DB", e);
        }
    }

    @Override
    public void addCustomer(Customer customer) {
        String sql = "INSERT INTO users (userID, name, email, password, isLoggedIn, role) "
                + "VALUES (?, ?, ?, ?, ?, 'customer')";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, customer.getUserID());
            stmt.setString(2, customer.getName());
            stmt.setString(3, customer.getEmail());
            stmt.setString(4, customer.getPassword());
            stmt.setBoolean(5, false);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Customer findCustomerById(int userID) {
        String sql = "SELECT * FROM users WHERE userID = ? AND role = 'customer'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // You’ll need a constructor that accepts the fields you stored
                return new Customer(
                        rs.getInt("userID"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                    /* shippingAddress and phoneNumber
                       would need to be stored in separate columns/tables */
                        "", ""  // <-- adjust once you persist those
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Customer findCustomerByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ? AND role = 'customer'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Customer(
                        rs.getInt("userID"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("shipping_address"), // Ensure column exists
                        rs.getString("phone_number")      // Ensure column exists
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Customer> findAllCustomers() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'customer'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Customer(
                        rs.getInt("userID"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        "", ""  // shippingAddress, phoneNumber
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void updateCustomer(Customer customer) {
        String sql = "UPDATE users SET name=?, email=?, password=? WHERE userID=? AND role='customer'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getEmail());
            stmt.setString(3, customer.getPassword());
            stmt.setInt(4, customer.getUserID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteCustomer(int userID) {
        String sql = "DELETE FROM users WHERE userID=? AND role='customer'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

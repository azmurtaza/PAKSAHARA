package com.example.paksahara.dao;

import com.example.paksahara.model.Payment;
import com.example.paksahara.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAOImpl implements PaymentDAO {
    private Connection connection;

    public PaymentDAOImpl() {
        try {
            this.connection = DatabaseUtil.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addPayment(Payment payment) {
        String sql = "INSERT INTO payments (paymentID, orderID, paymentDate, amount, paymentMethod) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, payment.getPaymentID());
            stmt.setInt(2, payment.getOrder().getOrderID());
            stmt.setDate(3, new java.sql.Date(payment.getPaymentDate().getTime()));
            stmt.setDouble(4, payment.getAmount());
            stmt.setString(5, payment.getPaymentMethod());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Payment findPaymentById(int paymentID) {
        String sql = "SELECT * FROM payments WHERE paymentID=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, paymentID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Payment(
                        rs.getInt("paymentID"),
                        /* order */ null,
                        rs.getDate("paymentDate"),
                        rs.getDouble("amount"),
                        rs.getString("paymentMethod")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Payment> findAllPayments() {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM payments";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Payment(
                        rs.getInt("paymentID"),
                        /* order */ null,
                        rs.getDate("paymentDate"),
                        rs.getDouble("amount"),
                        rs.getString("paymentMethod")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void updatePayment(Payment payment) {
        String sql = "UPDATE payments SET amount=?, paymentMethod=? WHERE paymentID=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, payment.getAmount());
            stmt.setString(2, payment.getPaymentMethod());
            stmt.setInt(3, payment.getPaymentID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deletePayment(int paymentID) {
        String sql = "DELETE FROM payments WHERE paymentID=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, paymentID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

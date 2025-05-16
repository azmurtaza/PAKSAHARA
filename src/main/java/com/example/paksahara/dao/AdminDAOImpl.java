package com.example.paksahara.dao;

import com.example.paksahara.model.Admin;
import com.example.paksahara.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDAOImpl implements AdminDAO {
    private Connection connection;

    public AdminDAOImpl() {
        try {
            this.connection = DatabaseUtil.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addAdmin(Admin admin) {
        String sql = "INSERT INTO users (userID, name, email, password, isLoggedIn, role) "
                + "VALUES (?, ?, ?, ?, ?, 'admin')";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, admin.getUserID());
            stmt.setString(2, admin.getName());
            stmt.setString(3, admin.getEmail());
            stmt.setString(4, admin.getPassword());
            stmt.setBoolean(5, false);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Admin findAdminById(int userID) {
        String sql = "SELECT * FROM users WHERE userID=? AND role='admin'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Admin(
                        rs.getInt("userID"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Admin findAdminByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ? AND role = 'admin'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Admin(
                        rs.getInt("userID"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Admin> findAllAdmins() {
        List<Admin> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role='admin'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Admin(
                        rs.getInt("userID"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void updateAdmin(Admin admin) {
        String sql = "UPDATE users SET name=?, email=?, password=? WHERE userID=? AND role='admin'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, admin.getName());
            stmt.setString(2, admin.getEmail());
            stmt.setString(3, admin.getPassword());
            stmt.setInt(4, admin.getUserID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAdmin(int userID) {
        String sql = "DELETE FROM users WHERE userID=? AND role='admin'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

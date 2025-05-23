package com.example.paksahara.db;
import com.example.paksahara.model.CartItem;
import com.example.paksahara.model.User;
import com.example.paksahara.model.Product;
import com.example.paksahara.model.Order;
import com.example.paksahara.model.LoginResult;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class DBUtils {


    private static final String URL = "jdbc:mysql://localhost:3306/paksahara_db";
    private static final String USER = "root";
    private static final String PASSWORD = "Azaan2004.";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // --- USER CRUD & AUTH ---

    public static boolean insertUser(String firstName, String lastName, String email, String password, String contact) {
        String sql = "INSERT INTO users (first_name, last_name, email, password, contact, role) VALUES (?, ?, ?, ?, ?, 'END_USER')";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, email);
            ps.setString(4, password);
            ps.setString(5, contact);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static List<CartItem> fetchCartItems(int userId) {
        String sql = """
        SELECT c.user_id,
               c.product_id,
               p.title        AS product_name,
               c.quantity,
               p.price        AS unit_price
          FROM cart c
          JOIN product p ON c.product_id = p.product_id
         WHERE c.user_id = ?
    """;

        List<CartItem> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new CartItem(
                            rs.getInt("user_id"),
                            rs.getInt("product_id"),
                            rs.getString("product_name"),
                            rs.getInt("quantity"),
                            rs.getDouble("unit_price")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void addProduct(String title, double price) throws SQLException {
        String sql = "INSERT INTO product (title, price, date_added, stock, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setDouble(2, price);
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(4, 0); // default stock
            ps.setString(5, "IN_STOCK");
            ps.executeUpdate();
        }
    }



    // Overload for single-quantity adds
    public static void addToCart(int userId, int productId) {
        addToCart(userId, productId, 1);
    }

    public static LoginResult checkLoginCredentials(String email, String password) {
        String sql = "SELECT user_id, role FROM users WHERE email=? AND password=?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new LoginResult(rs.getInt("user_id"), rs.getString("role"), null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void addToCart(int userId, int productId, int quantity) {
        String checkSql = "SELECT quantity FROM cart WHERE user_id = ? AND product_id = ?";
        String insertSql = "INSERT INTO cart (user_id, product_id, quantity) VALUES (?, ?, ?)";
        String updateSql = "UPDATE cart SET quantity = ? WHERE user_id = ? AND product_id = ?";
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setInt(1, userId);
                checkPs.setInt(2, productId);
                ResultSet rs = checkPs.executeQuery();
                if (rs.next()) {
                    int existing = rs.getInt("quantity");
                    try (PreparedStatement updPs = conn.prepareStatement(updateSql)) {
                        updPs.setInt(1, existing + quantity);
                        updPs.setInt(2, userId);
                        updPs.setInt(3, productId);
                        updPs.executeUpdate();
                    }
                } else {
                    try (PreparedStatement insPs = conn.prepareStatement(insertSql)) {
                        insPs.setInt(1, userId);
                        insPs.setInt(2, productId);
                        insPs.setInt(3, quantity);
                        insPs.executeUpdate();
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // In DBUtils class
    public static User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("user_id"),
                        rs.getString("first_name") + " " + rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("role")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static boolean updateUserName(int userId, String newName) {
        String[] parts = newName.split(" ", 2);
        String sql = "UPDATE users SET first_name=?, last_name=? WHERE user_id=?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, parts[0]);
            ps.setString(2, parts.length>1 ? parts[1] : "");
            ps.setInt(3, userId);
            return ps.executeUpdate()>0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<User> fetchAllUsers() {
        String sql = "SELECT user_id, first_name, last_name, email, role, image_url FROM users";
        List<User> users = new ArrayList<>();
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(new User(
                        rs.getInt("user_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("role"),
                        rs.getString("image_url")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }


    public static void deleteUserById(int userId) {
        String sql = "DELETE FROM users WHERE user_id=?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --- PRODUCT CRUD & REPORTS ---

    public static boolean insertProduct(Product p) {
        String sql = """
            INSERT INTO product
              (title, description, image_url, price, stock, date_added, category_id, status)
            VALUES (?,?,?,?,?,NOW(),?,?)
            """;
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getTitle());
            ps.setString(2, p.getDescription());
            ps.setString(3, p.getImageUrl());
            ps.setDouble(4, p.getPrice());
            ps.setInt(5, p.getStock());
            ps.setInt(6, p.getCategoryId());
            ps.setString(7, p.getStatus());
            return ps.executeUpdate()>0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void updateProduct(Product product) throws SQLException {
        String sql = "UPDATE product SET title = ?, price = ?, stock = ?, status = ? WHERE product_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getTitle());
            ps.setDouble(2, product.getPrice());
            ps.setInt(3, product.getStock());
            ps.setString(4, product.getStatus());
            ps.setInt(5, product.getId());
            ps.executeUpdate();
        }
    }

    public static void deleteProduct(int productId) throws SQLException {
        String sql = "DELETE FROM product WHERE product_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.executeUpdate();
        }
    }

    public static List<Product> fetchAllProducts() {
        String sql = """
            SELECT p.*, c.name AS category_name
            FROM product p
            LEFT JOIN category c ON p.category_id=c.category_id
            ORDER BY p.date_added DESC
            """;
        List<Product> list = new ArrayList<>();
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Product(
                        rs.getInt("product_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("image_url"),
                        rs.getTimestamp("date_added").toLocalDateTime(),
                        rs.getDouble("price"),
                        rs.getInt("stock"),
                        rs.getInt("category_id"),
                        rs.getString("category_name"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Product fetchProductById(int id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Product(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("image_url"),
                        rs.getTimestamp("date_added").toLocalDateTime(),
                        rs.getDouble("price"),
                        rs.getInt("stock"),
                        rs.getInt("category_id"),
                        rs.getString("category_name"),
                        rs.getString("status")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Product> fetchPosts(String category, String type, String department, String search) {
        List<Product> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT i.item_id AS product_id, i.title, i.description, i.image_url, i.date_posted AS date_added, " +
                        "0 AS price, 0 AS stock, i.category_id, c.name AS category_name, i.type AS status " +
                        "FROM item i JOIN category c ON i.category_id = c.category_id WHERE 1=1");

        if (category != null && !"ALL".equals(category)) sql.append(" AND c.name = ?");
        if (type != null && !"ALL".equals(type)) sql.append(" AND i.type = ?");
        if (department != null && !"ALL".equals(department)) sql.append(" AND i.department = ?");
        if (search != null && !search.isEmpty()) sql.append(" AND LOWER(i.title) LIKE ?");

        sql.append(" ORDER BY i.date_posted DESC");

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (category != null && !"ALL".equals(category)) ps.setString(idx++, category);
            if (type != null && !"ALL".equals(type)) ps.setString(idx++, type);
            if (department != null && !"ALL".equals(department)) ps.setString(idx++, department);
            if (search != null && !search.isEmpty()) ps.setString(idx++, "%" + search.toLowerCase() + "%");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Product(
                        rs.getInt("product_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("image_url"),
                        rs.getTimestamp("date_added").toLocalDateTime(),
                        rs.getDouble("price"),
                        rs.getInt("stock"),
                        rs.getInt("category_id"),
                        rs.getString("category_name"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Map<String,Integer> fetchCategoryCounts() {
        String sql = """
            SELECT c.name, COUNT(p.product_id) AS cnt
            FROM category c
            LEFT JOIN product p ON c.category_id=p.category_id
            GROUP BY c.name
            """;
        Map<String,Integer> m = new HashMap<>();
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                m.put(rs.getString("name"), rs.getInt("cnt"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return m;
    }

    public static Map<String,Integer> fetchStockStatusCounts() {
        String sql = """
            SELECT CASE WHEN stock>0 THEN 'In Stock' ELSE 'Out of Stock' END AS st, COUNT(*) AS cnt
            FROM product
            GROUP BY st
            """;
        Map<String,Integer> m = new HashMap<>();
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                m.put(rs.getString("st"), rs.getInt("cnt"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return m;
    }

    public static List<Order> fetchOrdersForUser(int userId) {
        String sql = """
        SELECT order_id, user_id, order_date, total_amount, status
          FROM orders
         WHERE user_id = ?
         ORDER BY order_date DESC
        """;
        List<Order> list = new ArrayList<>();
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Order(
                            rs.getInt("order_id"),
                            null, // Customer not loaded here
                            rs.getTimestamp("order_date"),
                            rs.getDouble("total_amount"),
                            rs.getString("status"),
                            fetchOrderItems(rs.getInt("order_id"))
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Fetch line-items for a specific order
    public static Map<Product,Integer> fetchOrderItems(int orderId) {
        String sql = """
        SELECT oi.product_id, oi.quantity, p.title, p.price, p.image_url
          FROM order_items oi
          JOIN product p ON oi.product_id = p.product_id
         WHERE oi.order_id = ?
        """;
        Map<Product,Integer> map = new LinkedHashMap<>();
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product p = new Product(
                            rs.getInt("product_id"),
                            rs.getString("title"),
                            /* description */ "",
                            rs.getString("image_url"),
                            /* dateAdded */ null,
                            rs.getDouble("price"),
                            /* stock */ 0,
                            /* categoryId */ 0,
                            /* categoryName */ "",
                            /* status */ ""
                    );
                    map.put(p, rs.getInt("quantity"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static User fetchUserById(int userId) {
        String sql = "SELECT first_name, last_name, email, role, image_url FROM users WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            userId,
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("email"),
                            rs.getString("role"),
                            rs.getString("image_url")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void updateUserImage(int userId, String imageUrl) throws SQLException {
        String sql = "UPDATE users SET image_url = ? WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, imageUrl);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    public static void updateUserName(int userId, String first, String last) throws SQLException {
        String sql = "UPDATE users SET first_name = ?, last_name = ? WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, first);
            ps.setString(2, last);
            ps.setInt(3, userId);
            ps.executeUpdate();
        }
    }


    // --- ORDER CREATION (in-memory for now) ---

    // You might later persist orders, but for now orders are created in Customer.checkout().
}

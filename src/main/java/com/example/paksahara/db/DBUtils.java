package com.example.paksahara.db;
import com.example.paksahara.model.CartItem;
import com.example.paksahara.model.User;
import com.example.paksahara.model.Product;
import com.example.paksahara.model.Order;
import com.example.paksahara.model.LoginResult;
import java.util.Date;
import java.util.Map;
import com.example.paksahara.model.Customer;
import com.example.paksahara.model.Order;
import com.example.paksahara.model.Product;
import java.util.List;
import java.awt.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import com.example.paksahara.session.SessionManager;
import com.example.paksahara.controller.CartContent;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

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

    public static LoginResult checkLoginCredentials(String email, String password) throws SQLException{
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
    public static void addToCart(int userId, int productId, int qty) {
        Connection conn = null;
        try {
            conn = getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            conn.setAutoCommit(false);

            // 1) lock & fetch current stock
            int stock;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT stock FROM product WHERE product_id = ? FOR UPDATE")) {
                ps.setInt(1, productId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new SQLException("Product not found");
                    stock = rs.getInt("stock");
                }
            }

            if (qty > stock) {
                throw new SQLException("Cannot add more than available stock (" + stock + ")");
            }

            // 2) insert/update cart
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO cart (user_id, product_id, quantity) VALUES (?, ?, ?) " +
                            "ON DUPLICATE KEY UPDATE quantity = quantity + VALUES(quantity)")) {
                ps.setInt(1, userId);
                ps.setInt(2, productId);
                ps.setInt(3, qty);
                ps.executeUpdate();
            }

            // 3) decrement product stock
            int newStock = stock - qty;
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE product SET stock = ? WHERE product_id = ?")) {
                ps.setInt(1, newStock);
                ps.setInt(2, productId);
                ps.executeUpdate();
            }

            // 4) if stock hits zero, first clear cart rows, then delete product
            if (newStock == 0) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM cart WHERE product_id = ?")) {
                    ps.setInt(1, productId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM product WHERE product_id = ?")) {
                    ps.setInt(1, productId);
                    ps.executeUpdate();
                }
            }

            conn.commit();
        } catch (Exception ex) {
            try { conn.rollback(); } catch (SQLException ignore) {}
            throw new RuntimeException(ex);
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException ignore) {}
        }
    }

    public static void removeFromCart(int userId, int productId) throws SQLException {
        String sql = "DELETE FROM cart WHERE user_id = ? AND product_id = ?";
        try (var conn = getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            ps.executeUpdate();
        }
    }
    /** Fetches the unit price of a product for order‐total calculations */
    public static double fetchProductPrice(int productId) throws SQLException {
        String sql = "SELECT price FROM product WHERE product_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("price");
                } else {
                    throw new SQLException("Product not found with ID " + productId);
                }
            }
        }
    }

    public static void createOrder(int userId,
                                   int productId,
                                   int quantity,
                                   String method,
                                   String cardNum,
                                   String cvc) throws SQLException {
        String insertOrderSql =
                "INSERT INTO orders (user_id, order_date, total_amount, status) " +
                        "VALUES (?, NOW(), ?, 'Pending')";
        String insertPaymentSql =
                "INSERT INTO payments (order_id, method, card_number, cvc) VALUES (?, ?, ?, ?)";
        // Now include unitPrice in the line‐items insert:
        String insertItemSql =
                "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            // 1) Insert into orders and grab the generated order_id
            int orderId;
            double unitPrice = fetchProductPrice(productId); // get the product’s unit price
            double totalAmt  = unitPrice * quantity;

            try (PreparedStatement ps = conn.prepareStatement(
                    insertOrderSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, userId);
                ps.setDouble(2, totalAmt);
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        orderId = keys.getInt(1);
                    } else {
                        throw new SQLException("Creating order failed, no ID obtained.");
                    }
                }
            }

            // 2) Insert into payments
            try (PreparedStatement ps = conn.prepareStatement(insertPaymentSql)) {
                ps.setInt(1, orderId);
                ps.setString(2, method);
                ps.setString(3, cardNum);
                ps.setString(4, cvc);
                ps.executeUpdate();
            }

            // 3) Insert into order_items, now including unitPrice
            try (PreparedStatement ps = conn.prepareStatement(insertItemSql)) {
                ps.setInt(1, orderId);
                ps.setInt(2, productId);
                ps.setInt(3, quantity);
                ps.setDouble(4, unitPrice);
                ps.executeUpdate();
            }

            conn.commit();
        } catch (SQLException ex) {
            // rollback if you want, then rethrow
            throw ex;
        }
    }

    public static void updateUserAddress(int userId, String address) throws SQLException {
        String sql = "UPDATE users SET address = ? WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, address);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    public static List<Order> fetchAllOrders() throws SQLException {
        String sql = """
        SELECT o.order_id,
               o.user_id,
               o.order_date,
               o.total_amount,
               o.status,
               p.method
          FROM orders o
          JOIN payments p ON o.order_id = p.order_id
         ORDER BY o.order_date DESC
    """;
        List<Order> orders = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int orderId   = rs.getInt("order_id");
                // Option A: no customer object yet
                Customer cust = null;
                // Option B: if you want the actual Customer:
                // Customer cust = (Customer) DBUtils.fetchUserById(rs.getInt("user_id"));

                // Convert SQL Timestamp to java.util.Date
                Date orderDate = new Date(rs.getTimestamp("order_date").getTime());

                double total   = rs.getDouble("total_amount");
                String status  = rs.getString("status");

                // Reuse your existing helper to load the map of products → quantities
                Map<Product,Integer> items = DBUtils.fetchOrderItems(orderId);

                Order ord = new Order(
                        orderId,    // orderID
                        cust,       // Customer object (or null)
                        orderDate,  // java.util.Date
                        total,      // double
                        status,     // String
                        items       // Map<Product,Integer>
                );

                orders.add(ord);
            }

        }
        return orders;
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
    public static List<String> fetchAllCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT name FROM category ORDER BY name";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                categories.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public static List<User> fetchAllUsers() {
        String sql = "SELECT user_id, first_name, last_name, email, role FROM users";
        List<User> users = new ArrayList<>();
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(new User(
                        rs.getInt("user_id"),
                        rs.getString("first_name") + " " + rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("role")
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

    public static int getCategoryId(String categoryName) throws SQLException {
        String sql = "SELECT category_id FROM category WHERE name = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, categoryName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("category_id");
                }
            }
        }
        throw new SQLException("Category not found: " + categoryName);
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


    public static void addProduct(Product p) throws SQLException {
        String sql = "INSERT INTO product (title, description, image_url, date_added, price, stock, category_id, status)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getTitle());
            ps.setString(2, p.getDescription());
            ps.setString(3, p.getImageUrl());
            //ps.setTimestamp(4, Timestamp.valueOf(p.getDateAdded()));
            ps.setDouble(5, p.getPrice());
            ps.setInt(6, p.getStock());
            ps.setInt(7, p.getCategoryId());
            ps.setString(8, p.getStatus());
            ps.executeUpdate();
        }
    }

    public static void deleteProduct(int productId) throws SQLException {
        String sql = "DELETE FROM product WHERE product_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
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
                        //rs.getTimestamp("date_added").toLocalDateTime(),
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
        String sql = "SELECT * FROM product WHERE product_id = ?";  // ✅ Correct table & column
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
                        //rs.getTimestamp("date_added").toLocalDateTime(),
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
                        //rs.getTimestamp("date_added").toLocalDateTime(),
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
                            "",                        // description (empty string for now)
                            rs.getString("image_url"),
                            rs.getDouble("price"),
                            0,                         // stock (default 0)
                            0,                         // categoryId (default 0)
                            "",                        // categoryName (empty)
                            ""                         // status (empty)
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
        String sql = "SELECT user_id, first_name, last_name, email, role, image_url, address FROM users WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User(
                            rs.getInt("user_id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("email"),
                            rs.getString("role"),
                            rs.getString("image_url")
                    );
                    user.setAddress(rs.getString("address")); // NEW
                    return user;
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

    @FXML
    private Pane contentArea;   // or StackPane, AnchorPane—whatever your FXML root is
    @FXML private Button cartBtn;

    private void loadCart() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/paksahara/fxml/cartContent.fxml")
            );
            Parent pane = loader.load();
            CartContent ctrl = loader.getController();
            ctrl.setCurrentUserId(SessionManager.getCurrentUserId());
            contentArea.getChildren().setAll(pane);
        } catch (IOException ex) {
            ex.printStackTrace();
            showError("Could not load cart: " + ex.getMessage());
        }
    }

    private void showError(String s) {
    }


    // --- ORDER CREATION (in-memory for now) ---

    // You might later persist orders, but for now orders are created in Customer.checkout().
}

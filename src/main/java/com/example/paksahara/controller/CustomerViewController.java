package com.example.paksahara.controller;

import com.example.paksahara.dao.OrderDAO;
import com.example.paksahara.dao.OrderDAOImpl;
import com.example.paksahara.dao.PaymentDAO;
import com.example.paksahara.dao.PaymentDAOImpl;
import com.example.paksahara.dao.ProductDAO;
import com.example.paksahara.dao.ProductDAOImpl;
import com.example.paksahara.model.Customer;
import com.example.paksahara.model.Order;
import com.example.paksahara.model.Payment;
import com.example.paksahara.model.Product;
import com.example.paksahara.model.ShoppingCart;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class CustomerViewController {

    @FXML private Button checkoutButton;
    @FXML private Label cartTotalLabel;
    @FXML private VBox productsContainer;
    @FXML private Button logoutButton;

    private ProductDAO productDAO;
    private OrderDAO orderDAO;
    private PaymentDAO paymentDAO;
    private ShoppingCart shoppingCart;
    private Customer currentCustomer;
    private double cartTotal = 0.0;
    private final NumberFormat pkrFormat = NumberFormat.getCurrencyInstance(new Locale("en", "PK"));

    @FXML
    public void initialize() {
        try {
            productDAO = new ProductDAOImpl();
            orderDAO = new OrderDAOImpl();
            paymentDAO = new PaymentDAOImpl();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize DAOs", e);
        }
        shoppingCart = new ShoppingCart();
        cartTotalLabel.setText("Cart Total: " + pkrFormat.format(0));
        loadProducts();
    }

    private void loadProducts() {
        productsContainer.getChildren().clear();
        for (Product product : productDAO.getAllProducts()) {
            VBox card = createProductCard(product);
            productsContainer.getChildren().add(card);
        }
    }

    private VBox createProductCard(Product product) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10;");

        Label nameLabel = new Label(product.getName());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label priceLabel = new Label(pkrFormat.format(product.getPrice()));
        priceLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 14px;");

        Label descLabel = new Label(product.getDescription());
        descLabel.setWrapText(true);

        Button addToCartBtn = new Button("Add to Cart");
        addToCartBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        addToCartBtn.setOnAction(e -> handleAddToCart(product));

        card.getChildren().addAll(nameLabel, priceLabel, descLabel, addToCartBtn);
        return card;
    }

    private void handleAddToCart(Product product) {
        shoppingCart.addProduct(product, 1);
        cartTotal += product.getPrice();
        cartTotalLabel.setText("Cart Total: " + pkrFormat.format(cartTotal));
    }

    @FXML
    private void handleCheckout() {
        if (currentCustomer == null) {
            new Alert(Alert.AlertType.ERROR, "No customer logged in.").showAndWait();
            return;
        }
        if (shoppingCart.getTotalItems() == 0) {
            new Alert(Alert.AlertType.WARNING, "Your cart is empty.").showAndWait();
            return;
        }

        int orderId = generateOrderID();
        Order newOrder = new Order(
                orderId,
                currentCustomer,
                new Date(),
                cartTotal,
                "Pending",
                shoppingCart.getProducts()
        );
        orderDAO.addOrder(newOrder);

        Payment payment = new Payment(
                generatePaymentID(),
                newOrder,
                new Date(),
                cartTotal,
                "Card"
        );
        paymentDAO.addPayment(payment);

        for (Map.Entry<Product, Integer> entry : shoppingCart.getProducts().entrySet()) {
            Product p = entry.getKey();
            int qty = entry.getValue();
            p.reduceStock(qty);
            productDAO.updateProduct(p);
        }

        shoppingCart.clearCart();
        cartTotal = 0;
        cartTotalLabel.setText("Cart Total: " + pkrFormat.format(cartTotal));

        new Alert(Alert.AlertType.INFORMATION, "Order placed and payment processed!").showAndWait();
    }

    private int generateOrderID() {
        return (int)(Math.random() * 100000);
    }

    private int generatePaymentID() {
        return (int)(Math.random() * 100000);
    }

    public void setCurrentCustomer(Customer customer) {
        this.currentCustomer = customer;
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to logout?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Logout");
        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isEmpty() || res.get() != ButtonType.YES) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/Login.fxml")
            );
            Parent loginRoot = loader.load();
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(loginRoot));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

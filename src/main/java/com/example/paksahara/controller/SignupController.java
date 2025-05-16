package com.example.paksahara.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;

import com.example.paksahara.dao.CustomerDAO;
import com.example.paksahara.dao.CustomerDAOImpl;

import com.example.paksahara.model.Customer;
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
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ButtonType;
import java.util.Optional;
import java.io.IOException;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.event.ActionEvent;
import java.io.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;

import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

public class SignupController {
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField contactField;
    @FXML private Label errorLabel;

    @FXML
    private void handleSignup(ActionEvent event) {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String contact = contactField.getText().trim();

        // Basic validation
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("All fields are required.");
            return;
        }

        try {
            // Create customer (adjust constructor as needed)
            Customer newCustomer = new Customer(
                    0, // ID will be auto-generated
                    firstName + " " + lastName,
                    email,
                    password,
                    "", // Shipping address (optional)
                    contact
            );

            // Save to database
            CustomerDAO customerDAO = new CustomerDAOImpl();
            customerDAO.addCustomer(newCustomer);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Signup Successful");
            alert.setHeaderText(null);
            alert.setContentText("Account created successfully! You can now login.");
            alert.showAndWait();

            // Redirect to login
            handleLogin(event);
        } catch (Exception e) {
            errorLabel.setText("Signup failed. Please try again.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            errorLabel.setText("Failed to load login screen");
            e.printStackTrace();
        }
    }
}
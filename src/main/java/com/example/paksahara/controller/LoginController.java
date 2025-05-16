package com.example.paksahara.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import com.example.paksahara.dao.AdminDAO;
import com.example.paksahara.dao.AdminDAOImpl;
import com.example.paksahara.dao.CustomerDAO;
import com.example.paksahara.dao.CustomerDAOImpl;
import com.example.paksahara.model.Admin;
import com.example.paksahara.model.Customer;
import javafx.scene.control.*;
import java.io.*;
// Add this import at the top of the file
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;


public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;

    private CustomerDAO customerDAO = new CustomerDAOImpl();
    private AdminDAO     adminDAO    = new AdminDAOImpl();

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String pass  = passwordField.getText().trim();

        // Try Customer
        Customer c = customerDAO.findCustomerByEmail(email);
        if (c != null && c.login(email, pass)) {
            loadCustomerView(c, event);
            return;
        }

        // Try Admin
        Admin a = adminDAO.findAdminByEmail(email);
        if (a != null && a.login(email, pass)) {
            loadSimpleView("AdminView.fxml", event);
            return;
        }

        errorLabel.setText("Invalid credentials.");
    }

    /* loads AdminView without any injection */
    private void loadSimpleView(String fxml, ActionEvent event) {
        try {
            Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(
                    FXMLLoader.load(getClass().getResource("/fxml/" + fxml))
            );
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** loads CustomerView and injects the Customer into its controller */
    private void loadCustomerView(Customer customer, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/CustomerView.fxml")
            );
            Parent root = loader.load();

            // inject
            CustomerViewController custController = loader.getController();
            custController.setCurrentCustomer(customer);

            Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Welcome.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleSignup(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Signup.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Sign Up - PAK-SAHARA");
        } catch (IOException e) {
            errorLabel.setText("Failed to load signup form");
            e.printStackTrace();
        }
    }

}

package com.example.paksahara.model;

import com.example.paksahara.db.DBUtils;
import com.example.paksahara.session.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Parent;
import java.sql.*;
import java.io.IOException;

public class Login {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;


    @FXML
    public void handleLogin(ActionEvent event) {
        String email    = emailField.getText();
        String password = passwordField.getText();

        System.out.println("Login Attempt: " + email + " | " + password);

        LoginResult result;
        try {
            result = DBUtils.checkLoginCredentials(email, password);
        } catch (SQLException ex) {
            ex.printStackTrace();
            errorLabel.setText("Database error—please try again.");
            return;
        }

        if (result == null) {
            errorLabel.setText("Invalid email or password.");
            System.out.println("Login failed: credentials invalid or DB error");
            return;
        }

        System.out.println("Login successful: " + result.getRole());
        SessionManager.setCurrentUser(result.getUserId(), result.getRole());

        try {
            FXMLLoader loader;
            switch (result.getRole().toUpperCase()) {
                case "ADMIN"     -> loader = new FXMLLoader(
                        getClass().getResource("/com/example/paksahara/admin_dashboard.fxml"));
                case "END_USER"  -> loader = new FXMLLoader(
                        getClass().getResource("/com/example/paksahara/endUser_dashboard.fxml"));
                default -> {
                    errorLabel.setText("Unauthorized role.");
                    return;
                }
            }

            Parent dashboard = loader.load();
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(dashboard));
            stage.setWidth(1200);
            stage.setHeight(800);
            stage.centerOnScreen();
            stage.setTitle("Dashboard");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Error loading dashboard.");
        }
    }




    @FXML
    private void handleSignup(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/paksahara/signup.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Sign Up");
            stage.show();
        } catch (Exception e) {
            errorLabel.setText("Failed to open Sign Up.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/paksahara/frontPage.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Welcome");
            stage.show();
        } catch (Exception e) {
            errorLabel.setText("Failed to return to the front page.");
            e.printStackTrace();
        }
    }


}

package com.example.paksahara.model;

import com.example.paksahara.db.DBUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class Signup {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField contactField;
    @FXML private Label errorLabel;

    @FXML
    private void handleSignup(ActionEvent event) {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String contact = contactField.getText();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || contact.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
            return;
        }

        boolean success = DBUtils.insertUser(firstName, lastName, email, password, contact);

        if (success) {
            errorLabel.setText("Signup successful! You can now log in.");
        } else {
            errorLabel.setText("Error occurred during signup.");
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/paksahara/login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Login - PAKSAHARA");
            stage.show();
        } catch (IOException e) {
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



package com.example.paksahara.model;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.io.IOException;

public class FrontPage {
    @FXML private VBox mainContent;
    @FXML private VBox loginSignupPanel;
    @FXML private StackPane backgroundContainer;

    @FXML
    private void handleGetStarted() {
        if (mainContent == null || loginSignupPanel == null || backgroundContainer == null) {
            System.err.println("FXML elements not properly injected!");
            return;
        }

        // Create slide animation
        Timeline slideAnimation = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(backgroundContainer.translateXProperty(), 0),
                        new KeyValue(mainContent.translateXProperty(), 0),
                        new KeyValue(loginSignupPanel.translateXProperty(), 1100)
                ),
                new KeyFrame(Duration.millis(500),
                        new KeyValue(backgroundContainer.translateXProperty(), -550),
                        new KeyValue(mainContent.translateXProperty(), -550),
                        new KeyValue(loginSignupPanel.translateXProperty(), 0)
                )
        );

        slideAnimation.play();
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/paksahara/login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Login - PAKSAHARA STORE");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSignup(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/paksahara/signup.fxml"));
            Parent root = loader.load();

            // Get current stage from the event's source node
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Sign Up");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
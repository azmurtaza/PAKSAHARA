package com.example.paksahara.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.application.Platform;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import com.example.paksahara.session.SessionManager;

public class EndUserDashboard implements Initializable {
    // FXML may call openCart/openProfile directly
    @FXML private void openCart() { handleCart(); }
    @FXML private void openProfile() { handleProfile(); }
    @FXML private void openOrders() { handleOrders(); }
    @FXML private StackPane contentArea;
    @FXML private Button logoutButton;
    @FXML private Button homeButton;
    @FXML private Button cartButton;
    @FXML private Button ordersButton;
    @FXML private Button profileButton;
    private int currentUserId;// or StackPane, AnchorPane—whatever your FXML root is
    @FXML private Button  cartBtn;

    public void setCurrentUserId(int id) {
        this.currentUserId = id;
        loadHome();
        setActiveButton(homeButton); // already correctly implemented
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            loadHome(); // Ensure it loads after the scene is ready
            setActiveButton(homeButton);
        });
    }
    private void loadProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/paksahara/fxml/profile.fxml")
            );
            Parent pane = loader.load();
            ProfileController ctrl = loader.getController();
            ctrl.setCurrentUserId(SessionManager.getCurrentUserId());
            contentArea.getChildren().setAll(pane);
        } catch (IOException ex) {
            showError("Could not load profile: " + ex.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        try {
            Parent login = FXMLLoader.load(getClass().getResource("/com/example/paksahara/login.fxml"));
            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.getScene().setRoot(login);
        } catch (IOException e) {
            showError("Logout failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleHome() {
        loadView("homeContent.fxml");
        setActiveButton(homeButton);
    }

    @FXML
    private void handleCart() {
        try {
            // 1. Load from /fxml/cartContent.fxml (not the root folder)
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/paksahara/fxml/cartContent.fxml")
            );
            Parent cartView = loader.load();

            // 2. Tell the controller which user to load
            CartContent cartCtrl = loader.getController();
            int userId = SessionManager.getCurrentUserId();
            cartCtrl.setCurrentUserId(userId);

            // 3. Show it
            contentArea.getChildren().setAll(cartView);
            setActiveButton(cartButton);

        } catch (IOException ex) {
            ex.printStackTrace();
            showError("Could not load cart: " + ex.getMessage());
        }
    }


    @FXML
    private void handleOrders() {
        // load your orders/transactions view
        loadView("transaction_report.fxml");
        setActiveButton(ordersButton);
    }

    @FXML
    private void handleProfile() {
        // ensure profile.fxm is lowercase if your file is named so
        loadView("profile.fxml");
        setActiveButton(profileButton);
    }

    private void loadHome() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/paksahara/homeContent.fxml")
            );
            Parent pane = loader.load();
            HomeContent ctrl = loader.getController();
            ctrl.setCurrentUserId(SessionManager.getCurrentUserId());
            contentArea.getChildren().setAll(pane);
        } catch (IOException ex) {
            ex.printStackTrace();
            showError("Could not load home: " + ex.getMessage());
        }
    }



    private void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/paksahara/" + fxmlFile
            ));
            Parent view = loader.load();
            Object controller = loader.getController();

            // propagate userId to known controllers:
            if (controller instanceof HomeContent) {
                ((HomeContent) controller).setCurrentUserId(currentUserId);
            } else if (controller instanceof CartContent) {
                ((CartContent) controller).setCurrentUserId(currentUserId);
            }
            // …add other instanceof checks here as needed (e.g. ProfileController)

            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            showError("Could not load view " + fxmlFile + ": " + e.getMessage());
        }
    }


    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }

    private void setActiveButton(Button active) {
        homeButton.getStyleClass().remove("active");
        cartButton.getStyleClass().remove("active");
        ordersButton.getStyleClass().remove("active");
        profileButton.getStyleClass().remove("active");
        active.getStyleClass().add("active");
    }
}

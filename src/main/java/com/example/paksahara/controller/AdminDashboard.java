package com.example.paksahara.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.Method;
import javafx.fxml.FXMLLoader;
public class AdminDashboard {
    @FXML private StackPane contentArea;
    @FXML private Button homeButton;
    @FXML private Button usersButton;
    @FXML private Button productsButton;
    @FXML private Button addProductButton;
    @FXML private Button transactionsButton;
    @FXML private Button logoutButton;

    private int currentAdminId;

    public void setCurrentAdminId(int id) {
        this.currentAdminId = id;
        handleHome();
    }

    @FXML public void initialize() {
        // nothing else
    }

    @FXML private void handleHome()         { loadContent("/com/example/paksahara/admin_home.fxml");        setActive(homeButton);        }
    @FXML private void handleUsers()        { loadContent("/com/example/paksahara/admin_users.fxml");       setActive(usersButton);       }
    @FXML private void handleProducts()     { loadContent("/com/example/paksahara/admin_products.fxml");    setActive(productsButton);    }
    @FXML private void handleAddProduct()   { loadContent("/com/example/paksahara/add_product.fxml");       setActive(addProductButton);  }
    @FXML private void handleTransactions() { loadContent("/com/example/paksahara/admin_transactions.fxml");setActive(transactionsButton);}
    @FXML private void handleLogout() {
        try {
            Parent login = FXMLLoader.load(getClass().getResource("/com/example/paksahara/login.fxml"));
            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.getScene().setRoot(login);
        } catch (IOException e) {
            showError("Logout failed: " + e.getMessage());
        }
    }

    @FXML private void handleUpdateProduct() {
        showError("Update Product not handled in AdminDashboard.\nPlease implement in your product editor controller.");
    }

    @FXML private void handleDeleteProduct() {
        showError("Delete Product not handled in AdminDashboard.\nPlease implement in your product editor controller.");
    }


    private void loadContent(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/paksahara/" + fxml));
            Parent pane = loader.load();
            Object ctrl = loader.getController();
            if (ctrl != null) {
                try {
                    Method m = ctrl.getClass().getMethod("setCurrentAdminId", int.class);
                    m.invoke(ctrl, currentAdminId);
                } catch (Exception ignored) { }
            }
            contentArea.getChildren().setAll(pane);
        } catch (IOException e) {
            showError("Could not load " + fxml + ": " + e.getMessage());
        }
    }


    private void setActive(Button b) {
        for (Button btn : new Button[]{homeButton,usersButton,productsButton,addProductButton,transactionsButton,logoutButton})
            btn.getStyleClass().remove("active");
        b.getStyleClass().add("active");
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }
}

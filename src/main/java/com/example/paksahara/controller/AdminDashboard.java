package com.example.paksahara.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import com.example.paksahara.db.DBUtils;
import com.example.paksahara.model.Product;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminDashboard implements Initializable {
    @FXML private StackPane contentArea;
    @FXML private Button homeButton;
    @FXML private Button usersButton;
    @FXML private Button productsButton;
    @FXML private Button addProductButton;
    @FXML private Button transactionsButton;
    @FXML private Button logoutButton;

    private int currentAdminId;
    @FXML private ImageView imagePreview;
    @FXML private Button handleChooseImage;
    @FXML private Button handleCancel;
    @FXML private Button handleSave;
    @FXML private TextField titleField;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox categoryComboBox;
    @FXML private TextField priceField;
    @FXML private TextField stockField;
    public void setCurrentAdminId(int id) {
        this.currentAdminId = id;
        // Load default home view
        loadHome(null);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // ensure home loads after init
        Platform.runLater(() -> {
            loadHome(null);
        });
    }
    private String selectedImageUri;
    @FXML private void handleLogout(ActionEvent e) {
        try {
            Parent login = FXMLLoader.load(getClass().getResource("/com/example/paksahara/frontPage.fxml"));
            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.getScene().setRoot(login);
        } catch (IOException ex) {
            showError("Logout failed: " + ex.getMessage());
        }
    }

    @FXML
    private void handleChooseImage() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Product Image");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        Window win = imagePreview.getScene().getWindow();
        File file = chooser.showOpenDialog(win);
        if (file != null) {
            selectedImageUri = file.toURI().toString();
            imagePreview.setImage(new Image(selectedImageUri, 200, 150, true, true));
        }
    }



    @FXML private void loadHome(ActionEvent e) {
        loadView("admin_home.fxml", homeButton);
    }

    @FXML private void loadUsers(ActionEvent e) {
        loadView("admin_users.fxml", usersButton);
    }

    @FXML private void loadProducts(ActionEvent e) {
        loadView("admin_products.fxml", productsButton);
    }

    @FXML private void loadAddProduct(ActionEvent e) {
        loadView("add_product.fxml", addProductButton);
    }

    @FXML private void loadTransactions(ActionEvent e) {
        loadView("transaction_report.fxml", transactionsButton);
    }

    private void loadView(String fxmlFile, Button active) {
        try {
            String path = "/com/example/paksahara/" + fxmlFile;
            URL fxmlURL = getClass().getResource(path);
            if (fxmlURL == null) throw new IOException("FXML not found: " + path);

            FXMLLoader loader = new FXMLLoader(fxmlURL);
            Parent view = loader.load();

            // propagate admin ID if controller needs it
            Object ctrl = loader.getController();
            try {
                ctrl.getClass().getMethod("setCurrentAdminId", int.class)
                        .invoke(ctrl, currentAdminId);
            } catch (NoSuchMethodException ignored) {
            }

            contentArea.getChildren().setAll(view);
            setActiveButton(active);
        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Could not load: " + fxmlFile + "\n" + ex.getMessage()).showAndWait();
        }
    }

    private void setActiveButton(Button active) {
        for (Button b : new Button[]{homeButton, usersButton, productsButton, addProductButton, transactionsButton, logoutButton}) {
            b.getStyleClass().remove("active");
        }
        if (active != null) active.getStyleClass().add("active");
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }
}

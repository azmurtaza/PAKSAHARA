package com.example.paksahara.controller;

import com.example.paksahara.db.DBUtils;
import com.example.paksahara.model.Product;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AdminProductDetailController implements Initializable {
    @FXML private DialogPane dialogPane;
    @FXML private ImageView productImage;
    @FXML private Label titleLabel;
    @FXML private Label categoryLabel;
    @FXML private Label priceLabel;
    @FXML private TextArea descArea;
    @FXML private Button deleteButton;

    private int productId;
    private Product product;
    private Runnable onDeleteSuccess;

    /**
     * Called by the loader to pass in the product ID and a callback to refresh the list.
     */
    public void setProduct(int productId, Runnable onDeleteSuccess) {
        this.productId = productId;
        this.onDeleteSuccess = onDeleteSuccess;
        loadProductDetails();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // nothing to do here
    }

    private void loadProductDetails() {
        product = DBUtils.fetchProductById(productId);
        if (product == null) {
            new Alert(Alert.AlertType.ERROR, "Product not found.").showAndWait();
            closeDialog();
            return;
        }

        titleLabel.setText(product.getTitle());
        categoryLabel.setText("Category: " + product.getCategoryName());
        priceLabel.setText(String.format("₨ %.2f", product.getPrice()));
        descArea.setText(product.getDescription());

        try {
            String path = product.getImageUrl();
            if (!path.startsWith("file:")) path = new File(path).toURI().toString();
            productImage.setImage(new Image(path, 360, 0, true, true));
        } catch (Exception e) {
            // fallback to default
            productImage.setImage(new Image(getClass().getResource("/icon.png").toExternalForm(), 360,0,true,true));
        }

        deleteButton.setOnAction(e -> handleDelete());
    }

    private void handleDelete() {
        try {
            DBUtils.deleteProduct(productId);
            new Alert(Alert.AlertType.INFORMATION, "Product deleted.").showAndWait();
            if (onDeleteSuccess != null) onDeleteSuccess.run();
            closeDialog();
        } catch (SQLException ex) {
            new Alert(Alert.AlertType.ERROR, "Failed to delete: " + ex.getMessage()).showAndWait();
        }
    }

    @FXML
    private void handleClose() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) dialogPane.getScene().getWindow();
        stage.close();
    }
}

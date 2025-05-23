package com.example.paksahara.controller;

import com.example.paksahara.db.DBUtils;
import com.example.paksahara.model.Product;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ProductCardController {
    @FXML private ImageView productImage;
    @FXML private Label titleLabel;
    @FXML private Label priceLabel;
    @FXML private Label stockLabel;

    private Product product;

    public void setData(Product product) {
        this.product = product;
        titleLabel.setText(product.getTitle());
        priceLabel.setText(String.format("$%.2f", product.getPrice()));
        stockLabel.setText("Stock: " + product.getStock());

        try {
            productImage.setImage(new Image(product.getImageUrl()));
        } catch (Exception e) {
            productImage.setImage(new Image("/com/example/paksahara/images/default-product.png"));
        }
    }

    @FXML
    private void handleEdit() {
        // Implement edit logic
    }

    @FXML
    private void handleDelete() {
        try {
            DBUtils.deleteProduct(product.getId());
            ProductViewController.getInstance().loadProducts();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
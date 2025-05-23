package com.example.paksahara.controller;

import com.example.paksahara.model.Product;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ItemCardController {
    @FXML private ImageView cardImage;
    @FXML private Label titleLabel;
    @FXML private Label categoryLabel;
    @FXML private Label dateLabel;
    @FXML private Label statusLabel;

    /**
     * Populates the card UI with product/post data.
     */
    public void setData(Product p) {
        titleLabel.setText(p.getTitle());
        categoryLabel.setText(p.getCategoryName());
        dateLabel.setText(p.getDateAdded().toLocalDate().toString());
        statusLabel.setText(p.getStatus());
        if (p.getImageUrl() != null && !p.getImageUrl().isEmpty()) {
            cardImage.setImage(new Image(p.getImageUrl(), true));
        }
    }
}

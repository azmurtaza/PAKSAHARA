package com.example.paksahara.controller;

import com.example.paksahara.model.Product;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Pair;

import java.io.File;
import java.util.function.Consumer;

public class ProductCardController {
    @FXML private ImageView cardImage;
    @FXML private Label cardTitle;
    @FXML private Label cardCategory;
    @FXML private Label cardPrice;
    @FXML private Label cardStatus;
    @FXML private Spinner<Integer> quantitySpinner;
    @FXML private Button actionBtn;

    private void bindCommon(Product p) {
        cardTitle.setText(p.getTitle());
        cardCategory.setText(p.getCategoryName());
        cardPrice.setText(String.format("₨ %.2f", p.getPrice()));
        cardStatus.setText(p.getStatus());
        try {
            String path = p.getImageUrl();
            if (!path.startsWith("file:") && !path.startsWith("http")) {
                path = new File(path).toURI().toString();
            }
            cardImage.setImage(new Image(path, 180,120,true,true));
        } catch (Exception e) {
            cardImage.setImage(new Image(
                    getClass().getResource("/com/example/paksahara/images/default-product.png")
                            .toExternalForm(),
                    180,120,true,true));
        }
    }

    /** Admin mode: Delete button + confirmation */
    public void setDataForAdmin(Product p, Consumer<Product> onDelete) {
        bindCommon(p);
        actionBtn.setText("Delete");
        actionBtn.setOnAction(e -> {
            var confirm = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.CONFIRMATION,
                    "Delete “" + p.getTitle() + "”?",
                    javafx.scene.control.ButtonType.OK,
                    javafx.scene.control.ButtonType.CANCEL);
            confirm.setHeaderText("Confirm Delete");
            confirm.showAndWait().ifPresent(btn -> {
                if (btn == javafx.scene.control.ButtonType.OK) {
                    onDelete.accept(p);
                }
            });
        });
        quantitySpinner.setVisible(false);
    }

    /** User mode: Spinner + Add to Cart (no confirmation) */
    public void setDataForUser(Product p, Consumer<Pair<Product,Integer>> onAdd) {
        bindCommon(p);
        // spinner between 1 and p.getStock()
        quantitySpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, p.getStock(), 1)
        );
        quantitySpinner.setDisable(p.getStock()==0);

        actionBtn.setText("Add to Cart");
        actionBtn.setDisable(p.getStock()==0);
        actionBtn.setOnAction(e -> {
            int qty = quantitySpinner.getValue();
            onAdd.accept(new Pair<>(p, qty));
        });
    }
}

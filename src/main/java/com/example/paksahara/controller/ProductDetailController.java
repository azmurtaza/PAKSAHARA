package com.example.paksahara.controller;

import com.example.paksahara.db.DBUtils;
import com.example.paksahara.model.Product;
import com.example.paksahara.model.CartItem;
import com.example.paksahara.session.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import com.example.paksahara.controller.CartContent;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Controller for the product detail page: shows full info and allows adding to cart.
 */
public class ProductDetailController implements Initializable {
    @FXML private Label titleLabel;
    @FXML private Label descLabel;
    @FXML private Label priceLabel;
    @FXML private Label dateLabel;
    @FXML private ImageView productImage;
    @FXML private Spinner<Integer> quantitySpinner;
    @FXML private Button addToCartButton;

    private int productId;
    private Product product;

    /**
     * Receives the product ID from the previous view.
     */
    public void setProductId(int productId) {
        this.productId = productId;
        loadProductDetails();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configure spinner default values
        SpinnerValueFactory.IntegerSpinnerValueFactory factory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        quantitySpinner.setValueFactory(factory);

        // Add to Cart action
        addToCartButton.setOnAction(e -> handleAddToCart());
    }

    /**
     * Loads product data from the database and populates UI fields.
     */
    private void loadProductDetails() {
        product = DBUtils.fetchProductById(productId);
        if (product == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Product not found.");
            alert.showAndWait();
            return;
        }

        titleLabel.setText(product.getTitle());
        descLabel.setText(product.getDescription());
        priceLabel.setText(String.format("$ %.2f", product.getPrice()));
        dateLabel.setText(product.getDateAdded()
                .format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));

        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            productImage.setImage(new Image(product.getImageUrl()));
        }
    }

    /**
     * Handles the Add to Cart button: calls CartContent.addToCart and shows feedback.
     */
    private void handleAddToCart() {
        int userId = SessionManager.getCurrentUserId();
        if (userId == 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please log in to add items to cart.");
            alert.showAndWait();
            return;
        }

        int qty = quantitySpinner.getValue();
        CartContent.addToCart(userId, productId, qty);

        Alert confirmation = new Alert(Alert.AlertType.INFORMATION,
                qty + " x " + product.getTitle() + " added to cart.");
        confirmation.showAndWait();
    }
}

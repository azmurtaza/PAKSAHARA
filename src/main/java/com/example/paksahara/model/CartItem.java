package com.example.paksahara.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Represents an item in a user's cart, with JavaFX properties for UI binding.
 */
public class CartItem {
    private final SimpleIntegerProperty userId;
    private final SimpleIntegerProperty productId;
    private final SimpleStringProperty productName;
    private final SimpleIntegerProperty quantity;
    private final SimpleDoubleProperty unitPrice;
    private final SimpleDoubleProperty totalPrice;

    public CartItem(int userId, int productId, String productName, int quantity, double unitPrice) {
        this.userId = new SimpleIntegerProperty(userId);
        this.productId = new SimpleIntegerProperty(productId);
        this.productName = new SimpleStringProperty(productName);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.unitPrice = new SimpleDoubleProperty(unitPrice);
        this.totalPrice = new SimpleDoubleProperty(quantity * unitPrice);
    }

    // Property getters for JavaFX bindings
    public SimpleIntegerProperty userIdProperty() { return userId; }
    public SimpleIntegerProperty productIdProperty() { return productId; }
    public SimpleStringProperty productNameProperty() { return productName; }
    public SimpleIntegerProperty quantityProperty() { return quantity; }
    public SimpleDoubleProperty unitPriceProperty() { return unitPrice; }
    public SimpleDoubleProperty totalPriceProperty() { return totalPrice; }

    // Standard getters
    public int getUserId() { return userId.get(); }
    public int getProductId() { return productId.get(); }
    public String getProductName() { return productName.get(); }
    public int getQuantity() { return quantity.get(); }
    public double getUnitPrice() { return unitPrice.get(); }
    public double getTotalPrice() { return totalPrice.get(); }

    // Setter for quantity (updates total price accordingly)
    public void setQuantity(int qty) {
        this.quantity.set(qty);
        this.totalPrice.set(qty * getUnitPrice());
    }
}

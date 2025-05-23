package com.example.paksahara.controller;

import com.example.paksahara.db.DBUtils;
import com.example.paksahara.model.CartItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import com.example.paksahara.model.Product;
import com.example.paksahara.session.SessionManager;
import javafx.scene.control.Alert;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the cart view: displays items in the user's cart and
 * allows quantity updates and removals.
 */
public class CartContent implements Initializable {
    @FXML private TableView<CartItem> cartTable;
    @FXML private TableColumn<CartItem, String> colProductName;
    @FXML private TableColumn<CartItem, Integer> colQuantity;
    @FXML private TableColumn<CartItem, Double> colUnitPrice;
    @FXML private TableColumn<CartItem, Double> colTotalPrice;
    @FXML private Button btnRefresh;

    private ObservableList<CartItem> cartItems = FXCollections.observableArrayList();

    private int currentUserId;

    /**
     * Called by the main dashboard to set which user this cart belongs to.
     */
    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
        loadCartItems();
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up table columns
        colProductName.setCellValueFactory(data -> data.getValue().productNameProperty());
        colQuantity.setCellValueFactory(data -> data.getValue().quantityProperty().asObject());
        colUnitPrice.setCellValueFactory(data -> data.getValue().unitPriceProperty().asObject());
        colTotalPrice.setCellValueFactory(data -> data.getValue().totalPriceProperty().asObject());

        cartTable.setItems(cartItems);

        // Refresh button action
        btnRefresh.setOnAction(e -> loadCartItems());
    }

    /**
     * Fetches the latest cart items from the database and updates the table.
     */
    @FXML
    private void loadCartItems() {
        if (currentUserId == 0) return;
        List<CartItem> items = DBUtils.fetchCartItems(currentUserId);
        cartItems.setAll(items);
    }

    /**
     * Adds an item to the cart and reloads the table.
     * @param userId the ID of the user
     * @param productId the ID of the product
     * @param quantity how many to add
     */
    public static void addToCart(int userId, int productId, int quantity) throws SQLException {
        DBUtils.addToCart(userId, productId, quantity);
    }
    public static void addToCart(int userId, int productId) throws SQLException {
        DBUtils.addToCart(userId, productId, 1);
    }


}

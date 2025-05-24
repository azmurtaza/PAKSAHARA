package com.example.paksahara.controller;

import com.example.paksahara.db.DBUtils;
import com.example.paksahara.model.CartItem;
import com.example.paksahara.session.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class CartContent implements Initializable {
    @FXML private TableView<CartItem> cartTable;
    @FXML private TableColumn<CartItem, String> colProductName;
    @FXML private TableColumn<CartItem, Integer> colQuantity;
    @FXML private TableColumn<CartItem, Double> colUnitPrice;
    @FXML private TableColumn<CartItem, Double> colTotalPrice;

    // NEW columns
    @FXML private TableColumn<CartItem, Void> colCheckout;
    @FXML private TableColumn<CartItem, Void> colRemove;

    @FXML private Button btnRefresh;

    private final ObservableList<CartItem> cartItems = FXCollections.observableArrayList();
    private int currentUserId;

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
        loadCartItems();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // existing columns
        colProductName.setCellValueFactory(data -> data.getValue().productNameProperty());
        colQuantity.setCellValueFactory(data -> data.getValue().quantityProperty().asObject());
        colUnitPrice.setCellValueFactory(data -> data.getValue().unitPriceProperty().asObject());
        colTotalPrice.setCellValueFactory(data -> data.getValue().totalPriceProperty().asObject());
        cartTable.setItems(cartItems);

        // wire up the new button-columns
        addRemoveButton();
        addCheckoutButton();

        // refresh action
        btnRefresh.setOnAction(e -> loadCartItems());
    }

    @FXML
    private void loadCartItems() {
        if (currentUserId == 0) return;
        List<CartItem> items = DBUtils.fetchCartItems(currentUserId);
        cartItems.setAll(items);
    }

    private void addRemoveButton() {
        colRemove.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Remove");
            {
                btn.setOnAction(e -> {
                    CartItem item = getTableView().getItems().get(getIndex());
                    try {
                        DBUtils.removeFromCart(item.getUserId(), item.getProductId());
                        getTableView().getItems().remove(item);
                    } catch (SQLException ex) {
                        new Alert(Alert.AlertType.ERROR,
                                "Could not remove item:\n" + ex.getMessage()
                        ).showAndWait();
                    }
                });
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : btn);
                setAlignment(Pos.CENTER);
            }
        });
    }

    private void addCheckoutButton() {
        colCheckout.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Checkout");
            {
                btn.setOnAction(e -> processCheckout(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : btn);
                setAlignment(Pos.CENTER);
            }
        });
    }

    private void processCheckout(CartItem item) {
        // 1) Payment method choice
        ChoiceDialog<String> methodDlg =
                new ChoiceDialog<>("Cash on Delivery", "Cash on Delivery", "Card Payment");
        methodDlg.setTitle("Payment Method");
        methodDlg.setHeaderText("Select payment method:");
        Optional<String> methodOpt = methodDlg.showAndWait();
        if (methodOpt.isEmpty()) return;

        String method = methodOpt.get().equals("Card Payment") ? "CARD" : "COD";
        String cardNum = null, cvc = null;

        // 2) If CARD, prompt for details
        if ("CARD".equals(method)) {
            Dialog<Pair<String,String>> cardDlg = new Dialog<>();
            cardDlg.setTitle("Card Details");

            // build the form
            GridPane grid = new GridPane();
            TextField numF = new TextField();
            TextField cvcF = new TextField();
            grid.addRow(0, new Label("Card Number:"), numF);
            grid.addRow(1, new Label("CVC:"),         cvcF);
            cardDlg.getDialogPane().setContent(grid);

            // add buttons
            cardDlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            // THIS is the key: convert the clicked ButtonType into your Pair<>
            cardDlg.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    return new Pair<>(numF.getText(), cvcF.getText());
                }
                return null;
            });

            Optional<Pair<String,String>> res = cardDlg.showAndWait();
            if (res.isEmpty()) return;        // user cancelled
            cardNum = res.get().getKey();
            cvc     = res.get().getValue();
        }


        // 3) Always succeed
        new Alert(Alert.AlertType.INFORMATION, "✔ Payment processed").showAndWait();

        // 4) Move to Orders
        try {
            DBUtils.createOrder(
                    currentUserId,
                    item.getProductId(),
                    item.getQuantity(),
                    method,       // "COD" or "CARD"
                    cardNum,      // may be null
                    cvc           // may be null
            );
            DBUtils.removeFromCart(item.getUserId(), item.getProductId());
            cartItems.remove(item);
        } catch (SQLException ex) {
            new Alert(Alert.AlertType.ERROR,
                    "Failed to checkout:\n" + ex.getMessage()
            ).showAndWait();
        }
    }
}

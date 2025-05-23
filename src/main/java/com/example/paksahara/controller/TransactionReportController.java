package com.example.paksahara.controller;

import com.example.paksahara.db.DBUtils;
import com.example.paksahara.model.Order;
import com.example.paksahara.model.Product;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.fxml.FXMLLoader;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import com.example.paksahara.session.SessionManager;
import com.example.paksahara.db.DBUtils;
import com.example.paksahara.model.User;
public class TransactionReportController implements Initializable {
    @FXML private TableView<Order> ordersTable;
    @FXML private TableColumn<Order, Integer> colOrderId;
    @FXML private TableColumn<Order, String>  colDate;
    @FXML private TableColumn<Order, Double>  colTotal;
    @FXML private TableColumn<Order, String>  colStatus;
    @FXML private TableColumn<Order, Void>    colAction;

    private ObservableList<Order> orders = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colOrderId.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getOrderID()));
        colDate.setCellValueFactory(data -> {
            String d = data.getValue().getOrderDate()
                    .toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime()
                    .format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"));
            return new SimpleObjectProperty<>(d);
        });
        colTotal.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getTotalAmount()));
        colStatus.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getStatus()));

        // Add “View Details” button
        colAction.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Details");
            {
                btn.setOnAction(e -> {
                    Order order = getTableView().getItems().get(getIndex());
                    showOrderDetails(order);
                });
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : btn);
            }
        });

        ordersTable.setItems(orders);

        loadUserOrders();
    }

    private void loadUserOrders() {
        int userId = SessionManager.getCurrentUserId();
        orders.clear();
        orders.addAll(DBUtils.fetchOrdersForUser(userId));
    }

    private void showOrderDetails(Order order) {
        Dialog<Void> dlg = new Dialog<>();
        dlg.initModality(Modality.APPLICATION_MODAL);
        dlg.setTitle("Order " + order.getOrderID());
        dlg.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        VBox box = new VBox(10);
        box.setPadding(new javafx.geometry.Insets(20));

        for (Map.Entry<Product,Integer> entry : order.getProducts().entrySet()) {
            Product p = entry.getKey();
            int qty = entry.getValue();
            Label line = new Label(
                    p.getTitle() + " × " + qty + " @ ₨ " + p.getPrice()
                            + " = ₨ " + (p.getPrice()*qty)
            );
            box.getChildren().add(line);
        }

        Label total = new Label("Total: ₨ " + order.getTotalAmount());
        total.setStyle("-fx-font-weight:bold; -fx-padding-top:10;");
        box.getChildren().add(total);

        dlg.getDialogPane().setContent(box);
        dlg.showAndWait();
    }
}

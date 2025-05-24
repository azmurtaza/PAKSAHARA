package com.example.paksahara.controller;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.example.paksahara.db.DBUtils;
import com.example.paksahara.model.Product;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import com.example.paksahara.db.DBUtils;
import com.example.paksahara.model.Product;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Alert;
import javafx.scene.Node;          // For general node handling
import javafx.scene.layout.GridPane; // For creating a grid layout
import javafx.scene.layout.Pane;      // For a generic pane layout
import javafx.scene.layout.RowConstraints;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.ResourceBundle;
import java.util.function.Consumer;
public class AdminProductsController implements Initializable {
    @FXML private FlowPane productsContainer;
    @FXML private TextField searchField;

    private List<Product> allProducts = new ArrayList<>();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadProducts();
    }


    private void loadProducts() {
        productsContainer.getChildren().clear();
        for (Product p : DBUtils.fetchAllProducts()) {
            addCardFor(p);
        }
    }


    private void addCardFor(Product p) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/paksahara/fxml/product_card.fxml")
            );
            AnchorPane card = loader.load();
            ProductCardController ctrl = loader.getController();
            ctrl.setDataForAdmin(p, prod -> {
                try {
                    DBUtils.deleteProduct(prod.getId());
                    loadProducts();
                } catch (SQLException ex) {
                    new Alert(Alert.AlertType.ERROR, "Delete failed:\n" + ex.getMessage()).showAndWait();
                }
            });

            card.setOnMouseClicked(evt -> showAdminDetail(p.getId(), card));
            FlowPane.setMargin(card, new Insets(10));
            productsContainer.getChildren().add(card);

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Failed to load product card:\n" + e.getMessage()
            ).showAndWait();
        }
    }

    private void showAdminDetail(int productId, AnchorPane card) {
        try {
            String fxmlPath = "/com/example/paksahara/fxml/admin_product_detail.fxml";
            URL detailUrl = getClass().getResource(fxmlPath);
            if (detailUrl == null) {
                throw new IOException("FXML file not found at path: " + fxmlPath);
            }

            FXMLLoader detailLoader = new FXMLLoader(detailUrl);
            DialogPane pane = detailLoader.load();
            AdminProductDetailController detailCtrl = detailLoader.getController();
            detailCtrl.setProduct(productId, this::loadProducts);

            Dialog<Void> dlg = new Dialog<>();
            Window owner = card.getScene().getWindow();
            dlg.initOwner(owner);
            dlg.getDialogPane().setContent(pane);
            dlg.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            dlg.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Could not load product details:\n" + e.getMessage())
                    .showAndWait();
        }
    }



    private void setupSearch() {
        searchField.textProperty().addListener((obs, oldV, newV) -> {
            String q = newV == null ? "" : newV.trim().toLowerCase();
            productsContainer.getChildren().clear();
            allProducts.stream()
                    .filter(p ->
                            p.getTitle().toLowerCase().contains(q) ||
                                    p.getCategoryName().toLowerCase().contains(q) ||
                                    p.getStatus().toLowerCase().contains(q)
                    )
                    .forEach(this::addCardFor);
        });
    }
}

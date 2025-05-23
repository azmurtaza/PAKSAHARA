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
        setupSearch();
    }

    private void loadProducts() {
        allProducts.clear();
        productsContainer.getChildren().clear();

        String sql = """
            SELECT p.product_id,
                   p.title,
                   p.description,
                   p.image_url,
                   p.price,
                   p.stock,
                   p.category_id,
                   c.name AS category_name,
                   p.status
              FROM product p
         LEFT JOIN category c ON p.category_id = c.category_id
          ORDER BY p.date_added DESC
        """;

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                allProducts.add(new Product(
                        rs.getInt("product_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("image_url"),
                        rs.getDouble("price"),
                        rs.getInt("stock"),
                        rs.getInt("category_id"),
                        rs.getString("category_name"),
                        rs.getString("status")
                ));
            }

            allProducts.forEach(this::addCardFor);

        } catch (SQLException ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Failed to load products:\n" + ex.getMessage()
            ).showAndWait();
        }
    }


    private void addCardFor(Product p) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/paksahara/fxml/product_card.fxml")
            );
            AnchorPane card = loader.load();
            ProductCardController ctrl = loader.getController();

            Consumer<Product> deleteCb = prod -> {
                try {
                    DBUtils.deleteProduct(prod.getId());
                    loadProducts();
                } catch (SQLException ex) {
                    new Alert(Alert.AlertType.ERROR,
                            "Delete failed:\n"+ex.getMessage())
                            .showAndWait();
                }
            };
            ctrl.setDataForAdmin(p, deleteCb);

            FlowPane.setMargin(card, new Insets(10));
            productsContainer.getChildren().add(card);
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR,
                    "Failed to load card:\n"+e.getMessage())
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

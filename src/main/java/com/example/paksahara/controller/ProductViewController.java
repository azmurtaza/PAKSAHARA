package com.example.paksahara.controller;

import com.example.paksahara.db.DBUtils;
import com.example.paksahara.model.Product;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ProductViewController implements Initializable {
    @FXML private GridPane grid;

    private static ProductViewController instance;

    public ProductViewController() {
        instance = this;
    }

    public static ProductViewController getInstance() {
        return instance;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadProducts();
    }

    public void loadProducts() {
        grid.getChildren().clear();
        List<Product> products = DBUtils.fetchAllProducts();
        int col = 0, row = 0;
        for (Product p : products) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/paksahara/fxml/product_card.fxml"));
                Node card = loader.load();
                ProductCardController ctrl = loader.getController();
                ctrl.setData(p);
                GridPane.setMargin(card, new Insets(10));
                grid.add(card, col, row);
                if (++col == 3) {
                    col = 0;
                    row++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

package com.example.paksahara.controller;

import com.example.paksahara.db.DBUtils;
import com.example.paksahara.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import com.example.paksahara.db.DBUtils;
import com.example.paksahara.model.Product;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AdminHomeController implements Initializable {
    @FXML private Label totalProductsLabel;
    @FXML private PieChart categoryChart;
    @FXML private BarChart<String, Number> stockChart;
    @FXML private TableView<Product> productsTable;
    @FXML private TableColumn<Product, Integer> idColumn;
    @FXML private TableColumn<Product, String> titleColumn;
    @FXML private TableColumn<Product, String> categoryColumn;
    @FXML private TableColumn<Product, Double> priceColumn;
    @FXML private TableColumn<Product, Integer> stockColumn;
    @FXML private TableColumn<Product, String> statusColumn;
    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private TextField searchField;
    private Product selectedProduct;
    @FXML private TableColumn<Product, Void> actionColumn;
    @FXML
    private void handleAddProduct() throws SQLException{
        String name = nameField.getText();
        double price = Double.parseDouble(priceField.getText());
        DBUtils.addProduct(name, price);
        loadProducts();
    }


    @FXML
    private void handleUpdateProduct() throws SQLException{
        if (selectedProduct != null) {
            selectedProduct.setTitle(nameField.getText());
            selectedProduct.setPrice(Double.parseDouble(priceField.getText()));
            DBUtils.updateProduct(selectedProduct);
            loadProducts();
        }
    }


    @FXML
    private void handleDeleteProduct() throws SQLException{
        if (selectedProduct != null) {
            DBUtils.deleteProduct(selectedProduct.getId());
            selectedProduct = null;
            loadProducts();
        }
    }


    private ObservableList<Product> productsList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        loadStatistics();
        loadCharts();
        loadProducts();
        setupSearch();
    }

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");
            {
                deleteButton.getStyleClass().add("delete-button");
                deleteButton.setOnAction(e -> {
                    Product p = getTableView().getItems().get(getIndex());
                    handleDeleteProduct(p);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteButton);
            }
        });
    }

    private void loadStatistics() {
        try (Connection conn = DBUtils.getConnection()) {
            String totalQuery = "SELECT COUNT(*) FROM product";
            try (PreparedStatement stmt = conn.prepareStatement(totalQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    totalProductsLabel.setText(String.valueOf(rs.getInt(1)));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Failed to load statistics: " + e.getMessage());
        }
    }

    private void loadCharts() {
        try (Connection conn = DBUtils.getConnection()) {
            // Category distribution
            String catQ = "SELECT c.name, COUNT(*) AS cnt "
                    + "FROM product p JOIN category c ON p.category_id = c.category_id "
                    + "GROUP BY c.name";
            try (PreparedStatement stmt = conn.prepareStatement(catQ);
                 ResultSet rs = stmt.executeQuery()) {
                ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
                while (rs.next()) {
                    data.add(new PieChart.Data(rs.getString("name"), rs.getInt("cnt")));
                }
                categoryChart.getData().setAll(data);
                categoryChart.setLegendVisible(true);
                categoryChart.setLabelsVisible(true);

            }

            // Stock status chart
            String stockQ = "SELECT CASE WHEN stock>0 THEN 'In Stock' ELSE 'Out of Stock' END AS st, "
                    + "COUNT(*) AS cnt FROM product GROUP BY st";
            try (PreparedStatement stmt = conn.prepareStatement(stockQ);
                 ResultSet rs = stmt.executeQuery()) {
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Stock Status");
                while (rs.next()) {
                    series.getData().add(new XYChart.Data<>(rs.getString("st"), rs.getInt("cnt")));
                }
                stockChart.getData().setAll(series);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Failed to load charts: " + e.getMessage());
        }
    }

    private void loadProducts() {
        try (Connection conn = DBUtils.getConnection()) {
            String sql = "SELECT p.*, c.name AS category_name "
                    + "FROM product p LEFT JOIN category c ON p.category_id = c.category_id "
                    + "ORDER BY p.date_added DESC";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                productsList.clear();
                while (rs.next()) {
                    productsList.add(new Product(
                            rs.getInt("product_id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getString("image_url"),
                            //rs.getTimestamp("date_added").toLocalDateTime(),
                            rs.getDouble("price"),
                            rs.getInt("stock"),
                            rs.getInt("category_id"),
                            rs.getString("category_name"),
                            rs.getString("status")
                    ));
                }
                productsTable.setItems(productsList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Failed to load products: " + e.getMessage());
        }
    }

    private void setupSearch() {
        FilteredList<Product> filtered = new FilteredList<>(productsList, p -> true);
        searchField.textProperty().addListener((obs, oldV, newV) -> {
            String q = newV == null ? "" : newV.toLowerCase();
            filtered.setPredicate(prod -> prod.getTitle().toLowerCase().contains(q)
                    || prod.getCategoryName().toLowerCase().contains(q)
                    || prod.getStatus().toLowerCase().contains(q));
        });
        SortedList<Product> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(productsTable.comparatorProperty());
        productsTable.setItems(sorted);
    }

    private void handleDeleteProduct(Product product) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete this product? This action cannot be undone.",
                ButtonType.OK, ButtonType.CANCEL);
        confirm.setHeaderText("Confirm Delete");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                try (Connection conn = DBUtils.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(
                             "DELETE FROM product WHERE product_id = ?")) {
                    stmt.setInt(1, product.getId());
                    stmt.executeUpdate();
                    productsList.remove(product);
                    loadStatistics();
                    loadCharts();
                    showSuccess("Product deleted");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    showError("Failed to delete: " + ex.getMessage());
                }
            }
        });
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }
    private void showSuccess(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }
}

package com.example.paksahara.controller;

import com.example.paksahara.dao.ProductDAO;
import com.example.paksahara.dao.ProductDAOImpl;
import com.example.paksahara.model.Product;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import java.text.NumberFormat;
import java.util.Locale;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;
import java.sql.SQLException;
import javafx.event.ActionEvent;

public class AdminViewController {
    // ---- TABLE & COLUMNS ----
    @FXML private TableView<Product> productTableAdmin;
    @FXML private TableColumn<Product, Integer> colIdAdmin;
    @FXML private TableColumn<Product, String>  colNameAdmin;
    @FXML private TableColumn<Product, Double>  colPriceAdmin;
    @FXML private VBox productsContainer;

    // ---- FORM FIELDS ----
    @FXML private TextField nameField;
    @FXML private TextField priceField;

    // ---- ACTION BUTTONS ----
    @FXML private Button addProductButton;
    @FXML private Button updateProductButton;
    @FXML private Button deleteProductButton;
    @FXML private Button logoutButton;


    // DAO
    private ProductDAO productDAO;

    // PKR formatter
    private final NumberFormat pkrFormat = NumberFormat.getCurrencyInstance(new Locale("en","PK"));


    @FXML
    public void initialize() {
        try {
            productDAO = new ProductDAOImpl();
            loadProducts();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize ProductDAO", e);
        }



        colIdAdmin.setCellValueFactory(c ->
                new SimpleIntegerProperty(c.getValue().getProductID()).asObject()
        );
        colNameAdmin.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getName())
        );
        colPriceAdmin.setCellValueFactory(c ->
                new SimpleDoubleProperty(c.getValue().getPrice()).asObject()
        );

        // Format price as PKR
        colPriceAdmin.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                setText(empty ? null : pkrFormat.format(price));
            }
        });

        refreshProducts();
    }

    private void refreshProducts() {
        Product selected = productTableAdmin.getSelectionModel().getSelectedItem();
        productTableAdmin.setItems(
                FXCollections.observableArrayList(productDAO.getAllProducts())
        );
        if (selected != null) {
            productTableAdmin.getSelectionModel().select(selected);
        }
    }



    private void loadProducts() {
        productsContainer.getChildren().clear();

        for (Product product : productDAO.getAllProducts()) {
            VBox card = createProductCard(product);
            productsContainer.getChildren().add(card);
        }
    }

    private VBox createProductCard(Product product) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10;");

        HBox header = new HBox(10);
        Label nameLabel = new Label(product.getName());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label priceLabel = new Label(pkrFormat.format(product.getPrice())); // Changed from "$" + product.getPrice()
        priceLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 16px;");

        header.getChildren().addAll(nameLabel, priceLabel);

        Label descLabel = new Label(product.getDescription());
        descLabel.setWrapText(true);

        HBox actions = new HBox(10);
        Button editBtn = new Button("Edit");
        Button deleteBtn = new Button("Delete");

        editBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

        editBtn.setOnAction(e -> handleEditProduct(product));
        deleteBtn.setOnAction(e -> handleDeleteProduct(product));

        actions.getChildren().addAll(editBtn, deleteBtn);

        card.getChildren().addAll(header, descLabel, actions);

        editBtn.setOnAction(e -> {
            // Select the product in TableView
            productTableAdmin.getSelectionModel().select(product);
            handleEditProduct(product);
        });

        deleteBtn.setOnAction(e -> {
            // Select the product in TableView
            productTableAdmin.getSelectionModel().select(product);
            handleDeleteProduct(product);
        });

        return card;
    }

    @FXML
    private void handleAddProduct() {
        String name  = nameField.getText().trim();
        String price = priceField.getText().trim();

        if (name.isEmpty()) {
            warn("Product name is required.");
            return;
        }

        double p;
        try {
            p = Double.parseDouble(price);
            if (p < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            warn("Enter a valid non-negative price.");
            return;
        }

        // Create the product
        Product prod = new Product(name, "", p, 0, "");

        try {
            // Save to database
            productDAO.addProduct(prod); // <-- THIS LINE WAS MISSING
            info("Product added with ID " + prod.getProductID());
        } catch (Exception e) {
            warn("Failed to add product. Check database connection.");
            e.printStackTrace();
        }

        nameField.clear();
        priceField.clear();

        refreshProducts(); // Updates TableView
        loadProducts();    // Reloads the VBox cards
    }

    @FXML
    private void handleUpdateProduct() {
        Product sel = productTableAdmin.getSelectionModel().getSelectedItem();
        if (sel == null) {
            warn("Select a product first.");
            return;
        }
        try {
            double p = Double.parseDouble(priceField.getText().trim());
            if (p < 0) throw new NumberFormatException();
            sel.setName(nameField.getText());
            sel.setPrice(p);
            productDAO.updateProduct(sel);
            refreshProducts();
            info("Updated product ID " + sel.getProductID());
        } catch (Exception ex) {
            warn("Select a product and enter a valid price to update.");
        }
    }

    private void handleDeleteProduct(Product product) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete product: " + product.getName() + "?",
                ButtonType.YES, ButtonType.NO
        );
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            productDAO.deleteProduct(product.getProductID());
            loadProducts(); // Refresh both views
            refreshProducts(); // Refresh table
            info("Deleted product: " + product.getName());
        }
    }

    @FXML
    private void handleLogout(ActionEvent ev) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Logout and return to login?", ButtonType.YES, ButtonType.NO
        );
        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isEmpty() || res.get() != ButtonType.YES) return;

        try {
            Parent login = FXMLLoader.load(
                    getClass().getResource("/fxml/Login.fxml")
            );
            Stage st = (Stage) logoutButton.getScene().getWindow();
            st.setScene(new Scene(login));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddDialog(ActionEvent event) {
        // For now, just pop up a simple dialog or show your add‐product form.
        // Later you can replace this with your real “Add Product” dialog logic.
        Alert info = new Alert(Alert.AlertType.INFORMATION,
                "Here you’d open your Add Product dialog."
        );
        info.setHeaderText("Add Product");
        info.showAndWait();
    }

    private void warn(String msg) {
        new Alert(Alert.AlertType.WARNING, msg).showAndWait();
    }
    private void info(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }

    @FXML
    private void handleDeleteFromTable() {
        Product sel = productTableAdmin.getSelectionModel().getSelectedItem();
        if (sel != null) {
            handleDeleteProduct(sel);
        }
    }

    private void handleEditProduct(Product product) {
        // Create a dialog or form for editing
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Edit Product");

        // Setup buttons
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create form fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField nameField = new TextField(product.getName());
        TextField priceField = new TextField(String.valueOf(product.getPrice()));

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Price (PKR):"), 0, 1); // Added (PKR) to clarify input
        grid.add(priceField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Convert result to Product
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    product.setName(nameField.getText());
                    product.setPrice(Double.parseDouble(priceField.getText()));
                    return product;
                } catch (NumberFormatException e) {
                    warn("Invalid price format");
                    return null;
                }
            }
            return null;
        });

        Optional<Product> result = dialog.showAndWait();
        result.ifPresent(updatedProduct -> {
            productDAO.updateProduct(updatedProduct);
            loadProducts(); // Refresh the view
            info("Product updated successfully");
        });
    }
}

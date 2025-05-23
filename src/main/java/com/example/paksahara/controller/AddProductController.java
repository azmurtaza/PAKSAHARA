package com.example.paksahara.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import com.example.paksahara.db.DBUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
public class AddProductController {

    @FXML
    private TextField titleField;

    @FXML
    private TextArea descArea;

    @FXML
    private ComboBox<String> categoryCombo;

    @FXML
    private TextField priceField;

    @FXML
    private TextField stockField;

    @FXML
    private ImageView imagePreview;

    private File selectedImage;

    @FXML
    private void handleCancel() {
        // You can clear the form or close the window
        clearFields(); // if you already have this method
        showAlert(Alert.AlertType.INFORMATION, "Cancelled", "Product addition cancelled.");
    }

    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Product Image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg"));
        selectedImage = fileChooser.showOpenDialog(null);
        if (selectedImage != null) {
            imagePreview.setImage(new Image(selectedImage.toURI().toString()));
        }
    }

    @FXML
    private void handleSave() {
        if (validateInput()) {
            try {
                String imagePath = saveImage();

                String query = "INSERT INTO product (title, description, category, price, stock, image_path) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";

                PreparedStatement stmt = DBUtils.getConnection().prepareStatement(query);
                stmt.setString(1, titleField.getText());
                stmt.setString(2, descArea.getText());
                stmt.setString(3, categoryCombo.getValue());
                stmt.setDouble(4, Double.parseDouble(priceField.getText()));
                stmt.setInt(5, Integer.parseInt(stockField.getText()));
                stmt.setString(6, imagePath);
                //stmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));

                stmt.executeUpdate();

                showAlert(Alert.AlertType.INFORMATION, "Success", "Product saved successfully!");
                clearFields();

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to save product.");
            }
        }
    }

    private boolean validateInput() {
        if (titleField.getText().isEmpty() || descArea.getText().isEmpty() ||
                categoryCombo.getValue() == null || priceField.getText().isEmpty() || stockField.getText().isEmpty()) {

            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all fields.");
            return false;
        }

        try {
            Double.parseDouble(priceField.getText());
            Integer.parseInt(stockField.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Price must be a number. Stock must be an integer.");
            return false;
        }

        return true;
    }

    private String saveImage() throws SQLException {
        if (selectedImage != null) {
            File uploadsDir = new File("uploads");
            if (!uploadsDir.exists()) uploadsDir.mkdirs();

            String fileName = System.currentTimeMillis() + "_" + selectedImage.getName();
            File destFile = new File(uploadsDir, fileName);

            try {
                Files.copy(selectedImage.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                return destFile.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
                throw new SQLException("Failed to save image");
            }
        }

        return "default.jpg";
    }

    private void clearFields() {
        titleField.clear();
        descArea.clear();
        categoryCombo.setValue(null);
        priceField.clear();
        stockField.clear();
        imagePreview.setImage(null);
        selectedImage = null;
    }


    public void initialize() {
        loadCategories();
    }

    private void loadCategories() {
        try {
            Connection conn = DBUtils.getConnection();
            String query = "SELECT name FROM category";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            List<String> categories = new ArrayList<>();
            while (rs.next()) {
                categories.add(rs.getString("name"));
            }

            categoryCombo.getItems().clear();
            categoryCombo.getItems().addAll(categories);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}

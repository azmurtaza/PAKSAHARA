package com.example.paksahara.model;

import com.example.paksahara.db.DBUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import com.example.paksahara.db.DBUtils;
import com.example.paksahara.controller.HomeContent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class ProductPost implements Initializable {
    @FXML
    private TextField titleField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextField locationField;
    @FXML private TextArea descriptionArea;
    @FXML private Label imageLabel;
    @FXML private ImageView imagePreview;
    @FXML
    private ComboBox<String> departmentComboBox;

    private File selectedImage;
    private int currentUserId;
    private DBUtils db;

    @Override
    public void initialize(URL loc, ResourceBundle res) {
        loadCategories();
        typeComboBox.setItems(FXCollections.observableArrayList("LOST", "FOUND")); //need to change this
    }

    private void loadCategories() {
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT name FROM category ORDER BY name");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) categoryComboBox.getItems().add(rs.getString("name"));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load categories.");
        }
    }

    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        selectedImage = fileChooser.showOpenDialog(imageLabel.getScene().getWindow());
        if (selectedImage != null) {
            imageLabel.setText(selectedImage.getName());
            // Display the selected image
            Image image = new Image(selectedImage.toURI().toString());
            imagePreview.setImage(image);
        }
    }

    @FXML
    private void handlePost() {
        if (validateInput()) {
            try {
                // Save image to resources folder
                String imagePath = saveImage();

                // Insert item into database
                String query = "INSERT INTO item (title, category_id, type, storage_location, department, description, " +
                        "image_url, user_id, date_posted) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

                PreparedStatement stmt = db.getConnection().prepareStatement(query);
                stmt.setString(1, titleField.getText());
                stmt.setInt(2, getCategoryId(categoryComboBox.getValue()));
                stmt.setString(3, typeComboBox.getValue());
                stmt.setString(4, locationField.getText());
                stmt.setString(5, departmentComboBox.getValue());
                stmt.setString(6, descriptionArea.getText());
                stmt.setString(7, imagePath);
                stmt.setInt(8, currentUserId);
                stmt.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));

                stmt.executeUpdate();

                showAlert(Alert.AlertType.INFORMATION, "Success", "Item posted successfully!");
                notifyParentToReload();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to post item. Please try again.");
            }
        }
    }

    private boolean validateInput() {
        if (titleField.getText().isEmpty() || categoryComboBox.getValue() == null ||
                typeComboBox.getValue() == null || locationField.getText().isEmpty() ||
                descriptionArea.getText().isEmpty()) {

            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all required fields.");
            return false;
        }
        return true;
    }

   /* private String saveImage() throws SQLException {
        if (selectedImage != null) {
            // Create images directory if it doesn't exist
            File imagesDir = new File("src/main/resources/images/items");
            if (!imagesDir.exists()) {
                imagesDir.mkdirs();
            }

            // Generate unique filename
            String fileName = System.currentTimeMillis() + "_" + selectedImage.getName();
            File destFile = new File(imagesDir, fileName);

            try {
                Files.copy(selectedImage.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                return "images/items/" + fileName;
            } catch (IOException e) {
                e.printStackTrace();
                throw new SQLException("Failed to save image");
            }
        }
        return "images/default-item.png"; // Return default image path if no image selected
    }
    */
   private String saveImage() throws SQLException {
       if (selectedImage != null) {
           String uploadsDir = "src/main/resources/uploads/"; // Relative to project
           File destDir = new File(uploadsDir);
           if (!destDir.exists()) destDir.mkdirs();

           String fileName = System.currentTimeMillis() + "_" + selectedImage.getName();
           File destFile = new File(destDir, fileName);
           try {
               Files.copy(selectedImage.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
               return "/uploads/" + fileName; // Use resource path
           } catch (IOException e) {
               throw new SQLException("Failed to save image");
           }
       }
       return getClass().getResource("/com/example/paksahara/icon.png").toString();
   }

    private int getCategoryId(String categoryName) throws SQLException {
        String query = "SELECT category_id FROM category WHERE name = ?";
        PreparedStatement stmt = db.getConnection().prepareStatement(query);
        stmt.setString(1, categoryName);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return rs.getInt("category_id");
        }
        throw new SQLException("Category not found");
    }

    @FXML
    private void handleCancel() {
        notifyParentToReload();
    }

    private void notifyParentToReload() {
        // Get the parent StackPane (contentArea)
        StackPane contentArea = (StackPane) titleField.getScene().lookup("#contentArea");

        if (contentArea == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not find content area.");
            return;
        }

        try {
            URL fxmlLocation = getClass().getResource("/com/example/paksahara/homeContent.fxml");
            if (fxmlLocation == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "homeContent.fxml not found.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent homeContent = loader.load();

            HomeContent controller = loader.getController();
            controller.setCurrentUserId(currentUserId);
            //controller.refreshPosts(); // if such method exists

            contentArea.getChildren().clear();
            contentArea.getChildren().add(homeContent);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to return to home view.");
        }
    }



    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
    }
}

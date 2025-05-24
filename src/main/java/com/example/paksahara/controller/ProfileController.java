package com.example.paksahara.controller;

import com.example.paksahara.db.DBUtils;
import com.example.paksahara.model.User;
import com.example.paksahara.session.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ResourceBundle;
public class ProfileController implements Initializable {
    @FXML private ImageView profileImageView;
    @FXML private Label lblName, lblEmail, lblRole;
    @FXML private TextField tfName;
    @FXML private Button btnEdit, btnSave, btnCancel, changePicButton;
    @FXML private Label lblAddress;
    @FXML private TextField tfAddress;

    private String imagePath;

    private int currentUserId;
    private User currentUser;
    private boolean addressField;

    public void initialize(URL location, ResourceBundle resources) {
        // hide edit controls
        tfName.setVisible(false);
        btnSave.setVisible(false);
        btnCancel.setVisible(false);

        currentUserId = SessionManager.getCurrentUserId();
        loadUserData();
    }

    private void loadUserData() {
        currentUser = DBUtils.fetchUserById(currentUserId);
        if (currentUser == null) {
            showError("User not found!");
            return;
        }

        // --- Text fields ---
        lblName.setText(currentUser.getFirstName() + " " + currentUser.getLastName());
        lblEmail.setText(currentUser.getEmail());
        lblRole.setText(currentUser.getRole());
        lblAddress.setText(currentUser.getAddress() != null ? currentUser.getAddress() : "");

        // --- Profile image ---
        String imgPath = currentUser.getImageUrl();
        if (imgPath != null && !imgPath.isBlank()) {
            File imgFile = new File(imgPath);
            if (imgFile.exists()) {
                profileImageView.setImage(
                        new Image(imgFile.toURI().toString(), 150, 150, true, true)
                );
            } else {
                // In case someone manually messed with the DB
                profileImageView.setImage(new Image("/icon.png"));
            }
        } else {
            profileImageView.setImage(new Image("/icon.png"));
        }
    }

    private String saveProfileImage(File selectedFile) throws IOException {
        File uploadsDir = new File("uploads");
        if (!uploadsDir.exists()) uploadsDir.mkdirs();

        String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
        File dest = new File(uploadsDir, fileName);
        Files.copy(selectedFile.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);

        // Store relative path, e.g. "uploads/16824234234_photo.png"
        return "uploads/" + fileName;
    }

    @FXML
    private void handleChangePicture(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose Profile Picture");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selected = chooser.showOpenDialog(profileImageView.getScene().getWindow());
        if (selected == null) return;

        try {
            // 1) Copy into our uploads folder
            String relativePath = saveProfileImage(selected);

            // 2) Persist that relative path
            DBUtils.updateUserImage(currentUserId, relativePath);
            currentUser.setImageUrl(relativePath);

            // 3) Display it
            String uri = new File(relativePath).toURI().toString();
            profileImageView.setImage(new Image(uri, 150, 150, true, true));
        } catch (Exception e) {
            showError("Could not update image: " + e.getMessage());
        }
    }


    @FXML
    private void onEdit() {
//        tfName.setText(lblName.getText());
//        lblName.setVisible(false);
//        tfName.setVisible(true);
//        btnEdit.setVisible(false);
//        btnSave.setVisible(true);
//        btnCancel.setVisible(true);
//        tfName.setText(currentUser.getFirstName());
//        tfAddress.setText(currentUser.getAddress());

        // Show text fields
        tfName.setVisible(true);
        tfAddress.setVisible(true);

        // Hide labels
        lblName.setVisible(false);
        lblAddress.setVisible(false);

        // Show save/cancel buttons
        btnSave.setVisible(true);
        btnCancel.setVisible(true);
        tfAddress.setText(currentUser.getAddress());
    }

    @FXML
    private void onSave() {
        // Name update (unchanged)
        String fullName = tfName.getText().trim();
        if (!fullName.isEmpty()) {
            String[] parts = fullName.split(" ", 2);
            try {
                DBUtils.updateUserName(currentUserId, parts[0], parts.length>1 ? parts[1] : "");
                currentUser.setFirstName(parts[0]);
                currentUser.setLastName(parts.length>1 ? parts[1] : "");
                lblName.setText(fullName);
            } catch (Exception e) {
                showError("Could not save name: " + e.getMessage());
            }
        }

        // *** Address update ***
        String newAddr = tfAddress.getText().trim();
        try {
            DBUtils.updateUserAddress(currentUserId, newAddr);
            currentUser.setAddress(newAddr);
            lblAddress.setText(newAddr);
        } catch (Exception e) {
            showError("Could not save address: " + e.getMessage());
        }

        resetEditMode();
    }




    @FXML
    private void onCancel() {
        resetEditMode();
    }

    private void resetEditMode() {
        tfName.setVisible(false);
        lblName.setVisible(true);
        btnSave.setVisible(false);
        btnCancel.setVisible(false);
        btnEdit.setVisible(true);
        tfAddress.setVisible(false);
        lblAddress.setVisible(true);

    }

    private void showError(String message) {
        new Alert(Alert.AlertType.ERROR, message).showAndWait();
    }

    public void setCurrentUserId(int id) {
        this.currentUserId = id;
        loadUserData();
    }

}
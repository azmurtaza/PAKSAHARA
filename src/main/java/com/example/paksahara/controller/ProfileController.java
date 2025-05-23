package com.example.paksahara.controller;

import com.example.paksahara.db.DBUtils;
import com.example.paksahara.model.User;
import com.example.paksahara.session.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {
    @FXML private ImageView profileImageView;
    @FXML private Label lblName, lblEmail, lblRole;
    @FXML private TextField tfName;
    @FXML private Button btnEdit, btnSave, btnCancel, changePicButton;

    private int currentUserId;
    private User currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // hide edit controls initially
        tfName.setVisible(false);
        btnSave.setVisible(false);
        btnCancel.setVisible(false);

        // load via SessionManager
        currentUserId = SessionManager.getCurrentUserId();
        loadUserData();
    }

    private void loadUserData() {
        currentUser = DBUtils.fetchUserById(currentUserId);
        if (currentUser == null) {
            showError("User not found!");
            return;
        }
        // display user info
        lblName.setText(currentUser.getFirstName() + " " + currentUser.getLastName());
        lblEmail.setText(currentUser.getEmail());
        lblRole.setText(currentUser.getRole());

        // load profile image
        String imagePath = currentUser.getImageUrl();
        InputStream is = null;
        if (imagePath != null && imagePath.startsWith("/")) {
            // resource path
            is = getClass().getResourceAsStream(imagePath);
        }
        if (is == null) {
            // fallback default
            is = getClass().getResourceAsStream("/com/example/paksahara/images/default-avatar.png");
        }
        if (is != null) {
            profileImageView.setImage(new Image(is,150,150,true,true));
        }
    }

    @FXML
    private void handleChangePicture() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Profile Picture");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png","*.jpg","*.jpeg")
        );
        Window win = profileImageView.getScene().getWindow();
        var file = chooser.showOpenDialog(win);
        if (file != null) {
            String uri = file.toURI().toString();
            profileImageView.setImage(new Image(uri,150,150,true,true));
            try {
                DBUtils.updateUserImage(currentUserId, uri);
                currentUser.setImageUrl(uri);
            } catch (Exception ex) {
                showError("Could not save image: " + ex.getMessage());
            }
        }
    }

    @FXML
    private void onEdit() {
        tfName.setText(lblName.getText());
        lblName.setVisible(false);
        tfName.setVisible(true);
        btnEdit.setVisible(false);
        btnSave.setVisible(true);
        btnCancel.setVisible(true);
    }

    @FXML
    private void onSave() {
        String fullName = tfName.getText().trim();
        if (!fullName.isEmpty()) {
            String[] parts = fullName.split(" ", 2);
            String first = parts[0];
            String last = parts.length>1 ? parts[1] : "";
            try {
                DBUtils.updateUserName(currentUserId, first, last);
                currentUser.setFirstName(first);
                currentUser.setLastName(last);
                lblName.setText(fullName);
            } catch (Exception e) {
                showError("Could not save name: " + e.getMessage());
            }
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
    }

    private void showError(String message) {
        new Alert(Alert.AlertType.ERROR, message).showAndWait();
    }
}
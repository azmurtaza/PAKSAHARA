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

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {
    @FXML private ImageView profileImageView;
    @FXML private Label lblName, lblEmail, lblRole;
    @FXML private TextField tfName;
    @FXML private Button btnEdit, btnSave, btnCancel, changePicButton;
    @FXML private Label lblAddress;
    @FXML private TextField tfAddress;


    private int currentUserId;
    private User currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // hide edit controls
        tfName.setVisible(false);
        btnSave.setVisible(false);
        btnCancel.setVisible(false);

        // load user
        currentUserId = SessionManager.getCurrentUserId();
        loadUserData();

        // NOW it's safe to ask for the URL:
        String imagePath = currentUser.getImageUrl();
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                profileImageView.setImage(
                        new Image(new File(imagePath).toURI().toString())
                );
            } catch (Exception e) {
                profileImageView.setImage(new Image("/icon.png"));
            }
        } else {
            profileImageView.setImage(new Image("/icon.png"));
        }
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
        lblAddress.setText(
                currentUser.getAddress() != null
                        ? currentUser.getAddress()
                        : "(no address set)"
        );


        // load profile image
        String imagePath = currentUser.getImageUrl();
        InputStream is = null;
        if (imagePath != null && imagePath.startsWith("/")) {
            // resource path
            is = getClass().getResourceAsStream(imagePath);
        }
        if (is == null) {
            // fallback default
            is = getClass().getResourceAsStream("/icon.png");
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
        try {
            String name = tfName.getText().trim();
            String address = tfAddress.getText() == null ? "" : tfAddress.getText().trim();

            DBUtils.updateUserName(currentUserId, name);  // your original logic
            DBUtils.updateUserAddress(currentUserId, address);  // ensure this is implemented

            currentUser.setFirstName(name);
            currentUser.setAddress(address);

            lblName.setText(name);
            lblAddress.setText(address);
            resetEditMode();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not save profile: " + e.getMessage());
        }
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
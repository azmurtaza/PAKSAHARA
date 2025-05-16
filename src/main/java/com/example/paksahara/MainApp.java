package com.example.paksahara;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;



public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // load your login FXML as before...
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));

        // Set custom icon
        primaryStage.getIcons().add(
                new Image(getClass().getResourceAsStream("/images/icon.png"))
        );

        primaryStage.setTitle("PAK-SAHARA Store");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
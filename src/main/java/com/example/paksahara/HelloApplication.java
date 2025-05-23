package com.example.paksahara;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HelloApplication extends Application {

    private ScheduledExecutorService scheduler;

    @Override
    public void start(Stage stage) {
        try {
            System.out.println(getClass().getResource("/com/example/paksahara/frontPage.fxml"));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/paksahara/frontPage.fxml"));
            Scene scene = new Scene(loader.load());

            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

            // Set logo - ensure logo.png is inside /resources/ or /com/example/dbs_project/
            Image logo = new Image(getClass().getResourceAsStream("/icon.png"));
            stage.getIcons().add(logo);

            stage.setTitle("PAKSAHARA Store");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

package com.example.shutdown_timer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ShutdownApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ShutdownApp.class.getResource("shutdownUI.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/shutdown_timer/icons/timer_icon.png"))));

        stage.setTitle("ShutDownAPP!");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}
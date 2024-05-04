package org.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        loadAES();
    }

    public static void loadAES() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        primaryStage.setTitle("AES Encryption");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void loadRSA() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("RSA.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        primaryStage.setTitle("RSA Encryption");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch();
    }
}

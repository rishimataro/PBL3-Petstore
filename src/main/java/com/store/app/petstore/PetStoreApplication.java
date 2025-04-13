package com.store.app.petstore;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class PetStoreApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Staff/Order.fxml"));
        AnchorPane root = loader.load();

        Scene scene = new Scene(root, 990, 512);

        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/Styles/Order.css")).toExternalForm());

        primaryStage.setTitle("Order");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Login.fxml"));
        AnchorPane root = loader.load();  // Tải FXML vào AnchorPane

        // Thiết lập Scene với AnchorPane đã tải từ FXML
        Scene scene = new Scene(root, 720, 512);

        // Thêm CSS nếu có
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/Styles/Login.css")).toExternalForm());

        // Thiết lập Stage (cửa sổ)
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();  // Hiển thị cửa sổ
    }

    public static void main(String[] args) {
        launch(args);
    }
}
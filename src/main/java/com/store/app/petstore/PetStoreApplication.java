package com.store.app.petstore;

import com.store.app.petstore.Models.Model;
import com.store.app.petstore.Views.ViewFactory;
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
        Model.getInstance().getViewFactory().showOrderWindow();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
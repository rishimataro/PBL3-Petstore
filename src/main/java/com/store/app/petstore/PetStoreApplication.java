package com.store.app.petstore;

import com.store.app.petstore.Models.DatabaseManager;
import com.store.app.petstore.Views.ModelView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class PetStoreApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
//        DatabaseManager.connect();
        ModelView.getInstance().getViewFactory().showWindow("bestseller");

    }

    public static void main(String[] args) {
        launch(args);
    }
}
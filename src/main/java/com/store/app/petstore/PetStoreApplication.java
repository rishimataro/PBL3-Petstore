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
        // Initialize database connection
        DatabaseManager.connect();
        
        // Show login window
//        ModelView.getInstance().getViewFactory().showWindow("login");

//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Staff/BillHistory.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Staff/PersonalInfo.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return; // Exits the method if the FXML file cannot be loaded
        }
        primaryStage.setTitle("Hoá đơn");
        primaryStage.setScene(new Scene(root));
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
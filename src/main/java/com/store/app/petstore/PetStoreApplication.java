package com.store.app.petstore;

import com.store.app.petstore.Models.DatabaseManager;
import com.store.app.petstore.Views.ModelView;
import javafx.application.Application;
import javafx.stage.Stage;

public class PetStoreApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Initialize database connection
        DatabaseManager.connect();
        
        // Show login window
//        ModelView.getInstance().getViewFactory().showWindow("login");
        ModelView.getInstance().getViewFactory().showWindow("order");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
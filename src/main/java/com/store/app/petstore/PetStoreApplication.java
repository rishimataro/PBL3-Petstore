package com.store.app.petstore;

import com.store.app.petstore.Views.ModelView;
import javafx.application.Application;
import javafx.stage.Stage;

public class PetStoreApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        ModelView.getInstance().getViewFactory().showWindow("login");
    }

    public static void main(String[] args) {
        launch(args);
    }
}

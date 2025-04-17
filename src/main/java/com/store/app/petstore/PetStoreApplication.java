package com.store.app.petstore;

import com.store.app.petstore.Views.ModelView;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class PetStoreApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        ModelView.getInstance().getViewFactory().showLoginWindow();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
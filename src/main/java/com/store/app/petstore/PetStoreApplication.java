package com.store.app.petstore;

import com.store.app.petstore.Models.ConnectJDBC;
import com.store.app.petstore.Views.LoginForm;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PetStoreApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Login");

        LoginForm loginFormView = new LoginForm();

        Scene scene = new Scene(loginFormView.getView(), 450, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        ConnectJDBC connectJDBC = new ConnectJDBC();
        connectJDBC.connect();
//        launch(args);
    }
}
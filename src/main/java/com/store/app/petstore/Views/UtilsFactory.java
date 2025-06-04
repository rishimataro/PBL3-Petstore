package com.store.app.petstore.Views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UtilsFactory {
    private static UtilsFactory instance;
    private final String LOGIN_FXML = "/FXML/Login.fxml";
    private final String FORGOT_PASSWORD_FXML = "/FXML/ForgotPassword.fxml";

    private final Map<String, AnchorPane> views;
    private final Map<String, Stage> stages;

    UtilsFactory() {
        this.views = new HashMap<>();
        this.stages = new HashMap<>();
    }

    public static synchronized UtilsFactory getInstance() {
        if (instance == null) {
            instance = new UtilsFactory();
        }
        return instance;
    }

    public void showWindow(String fxmlName) {
        Stage stage = new Stage();
        Parent root = null;
        try {
            switch (fxmlName.toLowerCase()) {
                case "login":
                    root = loadFXML(LOGIN_FXML);
                    stage.setTitle("Login");
                    break;
                case "forgotpassword":
                    root = loadFXML(FORGOT_PASSWORD_FXML);
                    stage.setTitle("Forgot Password");
                    break;
                default:
                    System.err.println("Unknown FXML file: " + fxmlName);
                    return;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private Parent loadFXML(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        return loader.load();
    }

}

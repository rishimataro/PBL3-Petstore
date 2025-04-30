package com.store.app.petstore.Controllers;

//import com.store.app.petstore.Services.AuthService;
import com.store.app.petstore.Views.ViewFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ForgotPasswordController implements Initializable {
    @FXML
    private TextField emailField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button resetButton;

    @FXML
    private Button backButton;

    @FXML
    private Label messageLabel;

    @FXML
    private VBox passwordFields;

    @FXML
    private VBox confirmPasswordFields;

    private boolean isEmailVerified = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}
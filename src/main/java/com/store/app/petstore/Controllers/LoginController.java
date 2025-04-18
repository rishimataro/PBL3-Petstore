package com.store.app.petstore.Controllers;

import com.store.app.petstore.Services.AuthService;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private TextField usernameField;

    @FXML
    private FontAwesomeIconView eyeIcon;

    @FXML
    private TextField showPassword;

    @FXML
    private PasswordField passwordField;
    @FXML
    private FontAwesomeIconView loginButton;
    @FXML
    private FontAwesomeIconView registerButton;

    private double x, y;

    private final AuthService authService;

    public LoginController() {
        authService = new AuthService();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showPassword();
    }

    @FXML
    private void dragged(MouseEvent event) {
        Stage stage = (Stage)passwordField.getScene().getWindow();
        stage.setX(event.getXOnScreen() - x);
        stage.setY(event.getYOnScreen() - y);
    }

    @FXML
    private void pressed(MouseEvent event) {
        x = event.getXOnScreen();
        y = event.getYOnScreen();
    }

    @FXML
    private void showPassword() {
        showPassword.managedProperty().bind(eyeIcon.pressedProperty());
        showPassword.visibleProperty().bind(eyeIcon.pressedProperty());
        showPassword.textProperty().bindBidirectional(passwordField.textProperty());

        passwordField.managedProperty().bind(eyeIcon.pressedProperty().not());
        passwordField.visibleProperty().bind(eyeIcon.pressedProperty().not());

        eyeIcon.pressedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) {
                eyeIcon.setIcon(FontAwesomeIcon.EYE);
            }
            else {
                eyeIcon.setIcon(FontAwesomeIcon.EYE_SLASH);
            }
        });
    }
    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        System.out.println(username + " " + password);
       if (authService.login(username, password)) {
           //
       } else {
           Alert alert = new Alert(Alert.AlertType.ERROR);
           alert.setTitle("Login Failed");
           alert.setHeaderText(null);
           alert.setContentText("Tên đăng nhập hoặc mật khẩu không đúng!");
           alert.showAndWait();
       }
    }
}

package com.store.app.petstore.Controllers;

import com.store.app.petstore.Models.Entities.User;
import com.store.app.petstore.Services.LoginService;
import com.store.app.petstore.Sessions.SessionManager;
import com.store.app.petstore.Views.ModelView;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private FontAwesomeIconView eyeIcon;

    @FXML
    private TextField showPassword;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField usernameField;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorLabel;

    @FXML
    private Hyperlink forgotPasswordLink;

    private final LoginService loginService = LoginService.getInstance();
    private double x, y;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupPasswordVisibility();
        setupLoginButton();
        setupForgotPasswordLink();
    }

    private void setupPasswordVisibility() {
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

    private void setupLoginButton() {
        loginButton.setOnAction(event -> handleLogin());
    }

    private void setupForgotPasswordLink() {
        forgotPasswordLink.setOnAction(event -> handleForgotPassword());
    }

    private void handleForgotPassword() {
        // Close the current login window
        Stage stage = (Stage) forgotPasswordLink.getScene().getWindow();
        stage.close();
        
        // Show the forgot password window
        ModelView.getInstance().getViewFactory().showWindow("forgotpassword");
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Vui lòng nhập đầy đủ thông tin");
            errorLabel.setVisible(true);
            return;
        }

        if (!loginService.isUsernameExists(username)) {
            errorLabel.setText("Tên đăng nhập không tồn tại");
            errorLabel.setVisible(true);
            return;
        }

        if (!loginService.isAccountActive(username)) {
            errorLabel.setText("Tài khoản đã bị khóa");
            errorLabel.setVisible(true);
            return;
        }

        User user = loginService.authenticate(username, password);
        if (user != null) {
            errorLabel.setVisible(false);
            // Set current user in session
            SessionManager.setCurrentUser(user);
            // Close login window
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.close();
            // Show order view
            ModelView.getInstance().getViewFactory().showWindow("order");
        } else {
            errorLabel.setText("Mật khẩu không đúng");
            errorLabel.setVisible(true);
        }
    }

    @FXML
    private void handleDragged(MouseEvent event) {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setX(event.getScreenX() - x);
        stage.setY(event.getScreenY() - y);
    }

    @FXML
    private void handlePressed(MouseEvent event) {
        x = event.getScreenX();
        y = event.getScreenY();
    }
}

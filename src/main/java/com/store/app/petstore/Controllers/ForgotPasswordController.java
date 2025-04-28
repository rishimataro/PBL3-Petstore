package com.store.app.petstore.Controllers;

import com.store.app.petstore.Services.AuthService;
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
    private Label messageLabel;

    @FXML
    private VBox passwordFields;

    @FXML
    private VBox confirmPasswordFields;

    private final AuthService authService;
    private boolean isEmailVerified = false;

    public ForgotPasswordController() {
        this.authService = new AuthService();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupResetButton();
        passwordFields.setVisible(false);
        confirmPasswordFields.setVisible(false);
    }

    private void setupResetButton() {
        resetButton.setOnAction(event -> {
            if (!isEmailVerified) {
                handleEmailVerification();
            } else {
                handlePasswordReset();
            }
        });
    }

    private void handleEmailVerification() {
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            showError("Vui lòng nhập email");
            return;
        }

        // Generate a random password for reset
        String newPassword = generateRandomPassword();
        
        if (authService.resetPassword(email, newPassword)) {
            isEmailVerified = true;
            passwordFields.setVisible(true);
            confirmPasswordFields.setVisible(true);
            resetButton.setText("Đặt lại mật khẩu");
            showSuccess("Vui lòng nhập mật khẩu mới");
        } else {
            showError("Email không tồn tại hoặc tài khoản đã bị khóa");
        }
    }

    private void handlePasswordReset() {
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showError("Vui lòng nhập đầy đủ thông tin");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showError("Mật khẩu không khớp");
            return;
        }

        if (newPassword.length() < 6) {
            showError("Mật khẩu phải có ít nhất 6 ký tự");
            return;
        }

        String email = emailField.getText().trim();
        if (authService.resetPassword(email, newPassword)) {
            showSuccess("Đặt lại mật khẩu thành công");
            // Return to login screen after 2 seconds
            new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        javafx.application.Platform.runLater(() -> {
                            Stage stage = (Stage) resetButton.getScene().getWindow();
                            stage.close();
                            ViewFactory.getInstance().showWindow("login");
                        });
                    }
                },
                2000
            );
        } else {
            showError("Đặt lại mật khẩu thất bại");
        }
    }

    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int index = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    private void showError(String message) {
        messageLabel.setStyle("-fx-text-fill: red;");
        messageLabel.setText(message);
    }

    private void showSuccess(String message) {
        messageLabel.setStyle("-fx-text-fill: green;");
        messageLabel.setText(message);
    }
}
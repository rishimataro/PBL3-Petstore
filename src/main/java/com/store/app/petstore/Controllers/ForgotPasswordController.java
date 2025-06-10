package com.store.app.petstore.Controllers;

import com.store.app.petstore.DAO.UserDAO;
import com.store.app.petstore.Models.Entities.User;
import com.store.app.petstore.Views.StaffFactory;
import com.store.app.petstore.Views.UtilsFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Platform;

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
    public void initialize(URL location, ResourceBundle resources) {
        setupInputValidation();
        setupEventHandlers();
        hidePasswordFields();
    }
    
    private void setupInputValidation() {
        // Email validation
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 100) {
                emailField.setText(oldValue);
                return;
            }
            
            if (!newValue.isEmpty() && !checkFormEmail(newValue)) {
                emailField.setStyle("-fx-border-color: #F44336; -fx-border-width: 1;");
            } else {
                emailField.setStyle("");
            }
        });
        
        // New password validation
        newPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 50) {
                newPasswordField.setText(oldValue);
                return;
            }
            
            if (newValue.length() > 0 && newValue.length() < 6) {
                newPasswordField.setStyle("-fx-border-color: #F44336; -fx-border-width: 1;");
            } else {
                newPasswordField.setStyle("");
                // Check if passwords match if confirm password is not empty
                if (!confirmPasswordField.getText().isEmpty()) {
                    validatePasswordMatch();
                }
            }
        });
        
        // Confirm password validation
        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 50) {
                confirmPasswordField.setText(oldValue);
                return;
            }
            validatePasswordMatch();
        });
    }
    
    private void validatePasswordMatch() {
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        if (confirmPassword.isEmpty() || newPassword.isEmpty()) {
            confirmPasswordField.setStyle("");
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            confirmPasswordField.setStyle("-fx-border-color: #F44336; -fx-border-width: 1;");
        } else {
            confirmPasswordField.setStyle("-fx-border-color: #4CAF50; -fx-border-width: 1;");
        }
    }

    private void setupEventHandlers() {
        resetButton.setOnAction(event -> handleResetPassword());
        backButton.setOnAction(event -> handleBackToLogin());
    }

    private boolean checkFormEmail(String email) {
        return email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    }

    private void handleResetPassword() {
        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng nhập email của bạn");
            emailField.requestFocus();
            return;
        }

        if (!checkFormEmail(email)) {
            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", 
                "Email không hợp lệ.\n" +
                "Ví dụ: example@domain.com");
            emailField.requestFocus();
            return;
        }

        if (!isEmailVerified) {
            User user = UserDAO.findByEmail(email);
            if (user == null) {
                ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Email không tồn tại trong hệ thống");
                return;
            }

            showPasswordFields();
            isEmailVerified = true;
            resetButton.setText("Đặt lại mật khẩu");
            return;
        }

        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng nhập đầy đủ mật khẩu mới");
            if (newPassword.isEmpty()) newPasswordField.requestFocus();
            else confirmPasswordField.requestFocus();
            return;
        }

        if (newPassword.length() < 6) {
            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", 
                "Mật khẩu phải có ít nhất 6 ký tự");
            newPasswordField.requestFocus();
            return;
        }
        
        if (newPassword.length() > 50) {
            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", 
                "Mật khẩu không được vượt quá 50 ký tự");
            newPasswordField.requestFocus();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", 
                "Mật khẩu xác nhận không khớp");
            confirmPasswordField.requestFocus();
            return;
        }

        try {
            User user = UserDAO.findByEmail(email);
            assert user != null;
            user.setPassword(newPassword);
            UserDAO.update(user);

            ControllerUtils.showAlert(Alert.AlertType.CONFIRMATION, "Thành công", "Mật khẩu đã được đặt lại thành công");
            handleBackToLogin();
        } catch (Exception e) {
            e.printStackTrace();
            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể đặt lại mật khẩu. Vui lòng thử lại sau");
        }
    }

    private void handleBackToLogin() {
        Stage currentStage = (Stage) emailField.getScene().getWindow();
        currentStage.close();
        UtilsFactory.getInstance().showWindow("login");
    }

    private void showPasswordFields() {
        passwordFields.setVisible(true);
        passwordFields.setManaged(true);
        confirmPasswordFields.setVisible(true);
        confirmPasswordFields.setManaged(true);
        
        // Focus on new password field when shown
        Platform.runLater(() -> newPasswordField.requestFocus());
    }

    private void hidePasswordFields() {
        passwordFields.setVisible(false);
        passwordFields.setManaged(false);
        confirmPasswordFields.setVisible(false);
        confirmPasswordFields.setManaged(false);
    }
}
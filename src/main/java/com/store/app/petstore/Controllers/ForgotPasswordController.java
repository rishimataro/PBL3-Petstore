package com.store.app.petstore.Controllers;

import com.store.app.petstore.DAO.UserDAO;
import com.store.app.petstore.Models.Entities.User;
import com.store.app.petstore.Views.ViewFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
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

    private UserDAO userDAO = new UserDAO();
    private boolean isEmailVerified = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupEventHandlers();
        hidePasswordFields();
    }

    private void setupEventHandlers() {
        resetButton.setOnAction(event -> handleResetPassword());
        backButton.setOnAction(event -> handleBackToLogin());
    }

    private boolean checkFormEmail(String email) {
        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"))
            return false;

        return true;
    }

    private void handleResetPassword() {
        String email = emailField.getText().trim();
        
        if (email.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng nhập email của bạn");
            return;
        }

        if(!checkFormEmail(email)) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Email không hợp lệ");
            return;
        }

        if (!isEmailVerified) {
            // Kiểm tra email có tồn tại trong hệ thống không
            User user = userDAO.findByEmail(email);
            if (user == null) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Email không tồn tại trong hệ thống");
                return;
            }
            
            // Hiển thị các trường nhập mật khẩu mới
            showPasswordFields();
            isEmailVerified = true;
            resetButton.setText("Đặt lại mật khẩu");
            return;
        }

        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng nhập đầy đủ mật khẩu mới");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Mật khẩu xác nhận không khớp");
            return;
        }

        if (newPassword.length() < 6) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Mật khẩu phải có ít nhất 6 ký tự");
            return;
        }

        try {
            // Cập nhật mật khẩu mới
            User user = userDAO.findByEmail(email);
            user.setPassword(newPassword);
            userDAO.update(user);

            showAlert(Alert.AlertType.CONFIRMATION, "Thành công", "Mật khẩu đã được đặt lại thành công");
            handleBackToLogin();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể đặt lại mật khẩu. Vui lòng thử lại sau");
        }
    }

    private void handleBackToLogin() {
        Stage currentStage = (Stage) emailField.getScene().getWindow();
        currentStage.close();
        ViewFactory.getInstance().showWindow("login");
    }

    private void showPasswordFields() {
        passwordFields.setVisible(true);
        passwordFields.setManaged(true);
        confirmPasswordFields.setVisible(true);
        confirmPasswordFields.setManaged(true);
    }

    private void hidePasswordFields() {
        passwordFields.setVisible(false);
        passwordFields.setManaged(false);
        confirmPasswordFields.setVisible(false);
        confirmPasswordFields.setManaged(false);
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
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
        setupEventHandlers();
        hidePasswordFields();
    }

    private void setupEventHandlers() {
        resetButton.setOnAction(event -> handleResetPassword());
        backButton.setOnAction(event -> handleBackToLogin());
    }

    private boolean checkFormEmail(String email) {
        return email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    private void handleResetPassword() {
        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng nhập email của bạn");
            return;
        }

        if (!checkFormEmail(email)) {
            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Email không hợp lệ");
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
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Mật khẩu xác nhận không khớp");
            return;
        }

        if (newPassword.length() < 6) {
            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Mật khẩu phải có ít nhất 6 ký tự");
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
    }

    private void hidePasswordFields() {
        passwordFields.setVisible(false);
        passwordFields.setManaged(false);
        confirmPasswordFields.setVisible(false);
        confirmPasswordFields.setManaged(false);
    }
}
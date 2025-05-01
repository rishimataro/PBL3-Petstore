package com.store.app.petstore.Controllers;

import com.store.app.petstore.DAO.UserDAO;
import com.store.app.petstore.DAO.StaffDAO;
import com.store.app.petstore.Models.Entities.User;
import com.store.app.petstore.Models.Entities.Staff;
import com.store.app.petstore.Sessions.SessionManager;
import com.store.app.petstore.Views.ViewFactory;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;
import java.util.Objects;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
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

    @FXML
    private ImageView userImage;

    private double x, y;

    UserDAO userDAO = new UserDAO();
    StaffDAO staffDAO = StaffDAO.getInstance();
    public static int idStaffCurrent;
    public static int idAdminCurrent;

    private SessionManager sessionManager = new SessionManager();

    // thiet lap ban dau
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
        forgotPasswordLink.setOnAction(event -> {
            Stage currentStage = (Stage) usernameField.getScene().getWindow();
            currentStage.close();
            ViewFactory.getInstance().showWindow("forgotpassword");
        });
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

    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if(username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng nhập tên đăng nhập và mật khẩu");
            return;
        }

        try {
            // Tìm user theo username
            User user = userDAO.findByUsername(username);
            
            if(user == null || user.getUsername() == null) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Tên đăng nhập không đúng");
                return;
            }

            // Kiểm tra mật khẩu bằng BCrypt
            if(!UserDAO.verify(password, user.getPassword())) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Mật khẩu không đúng");
                return;
            }

            sessionManager.setCurrentUser(user);

            // Close current login window
            Stage currentStage = (Stage) usernameField.getScene().getWindow();
            currentStage.close();

            if(user.getRole().equals(User.ROLE_ADMIN)) {
                idAdminCurrent = user.getUserId();
                ViewFactory.getInstance().showWindow("admin");
            } else if(user.getRole().equals(User.ROLE_USER)) {
                idStaffCurrent = user.getUserId();
                // Lấy thông tin Staff và lưu vào session
                Staff staff = staffDAO.findByUserId(user.getUserId());
                if (staff != null) {
                    sessionManager.setCurrentStaff(staff);
                }
                ViewFactory.getInstance().showWindow("order");
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Vai trò người dùng không hợp lệ");
                sessionManager.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Đã xảy ra lỗi khi đăng nhập");
        }
    }

    // show popup error
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

package com.store.app.petstore.Controllers;

import com.store.app.petstore.DAO.StaffDAO;
import com.store.app.petstore.DAO.UserDAO;
import com.store.app.petstore.Models.Entities.Staff;
import com.store.app.petstore.Models.Entities.User;
import com.store.app.petstore.Sessions.SessionManager;
import com.store.app.petstore.Views.AdminFactory;
import com.store.app.petstore.Views.ViewFactory;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    public static int idStaffCurrent;
    public static int idAdminCurrent;
    private final SessionManager sessionManager = new SessionManager();
    StaffDAO staffDAO = StaffDAO.getInstance();

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
            if (newValue) {
                eyeIcon.setIcon(FontAwesomeIcon.EYE);
            } else {
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
            ViewFactory.getInstance().switchContent("forgotpassword", currentStage);
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

        if (username.isEmpty() || password.isEmpty()) {
            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng nhập tên đăng nhập và mật khẩu");
            return;
        }

        try {
            // Tìm user theo username
            User user = UserDAO.findByUsername(username);

            if (user == null || user.getUsername() == null) {
                ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Tên đăng nhập không đúng");
                return;
            }

            // Kiểm tra tên đăng nhập
            if (!user.getUsername().equals(username)) {
                ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Tên đăng nhập không đúng");
                return;
            }

            // Kiểm tra mật khẩu
            if (!BCrypt.checkpw(password, user.getPassword())) {
                ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Mật khẩu không đúng");
                return;
            }

            SessionManager.setCurrentUser(user);

            Stage currentStage = (Stage) usernameField.getScene().getWindow();

            if (user.getRole().equals(User.ROLE_ADMIN)) {
                idAdminCurrent = user.getUserId();
                AdminFactory.getInstance().switchContent("dashboard", currentStage);
            } else if (user.getRole().equals(User.ROLE_USER)) {
                idStaffCurrent = user.getUserId();
                Staff staff = StaffDAO.findByUserId(user.getUserId());
                if (staff != null) {
                    SessionManager.setCurrentStaff(staff);
                }
                ViewFactory.getInstance().switchContent("dashboard", currentStage);
            } else {
                ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Vai trò người dùng không hợp lệ");
                SessionManager.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Đã xảy ra lỗi khi đăng nhập");
        }
    }
}

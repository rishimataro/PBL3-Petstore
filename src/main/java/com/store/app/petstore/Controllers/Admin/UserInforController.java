package com.store.app.petstore.Controllers.Admin;

import com.store.app.petstore.DAO.UserDAO;
import com.store.app.petstore.Models.Entities.Customer;
import com.store.app.petstore.Models.Entities.User;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class UserInforController implements Initializable {

    @FXML
    private Button btnAdd;

    @FXML
    private ToggleButton btnAdmin;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnFix;

    @FXML
    private Button btnSave;

    @FXML
    private ToggleButton btnStaff;

    @FXML
    private AnchorPane customerInforPopup;

    @FXML
    private FontAwesomeIconView iconHide;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private TextField txtUserid;

    @FXML
    private TextField txtUsername;

    @FXML
    private TextField txtPasswordVisible;

    private int idUserCurrent;
    private UserDAO userDAO = new UserDAO();
    private boolean isNewUser = true;
    private boolean isPasswordVisible = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtUserid.setDisable(true);
        txtUsername.setDisable(true);
        txtPassword.setDisable(true);
        btnAdmin.setDisable(true);
        btnStaff.setDisable(true);

        setupButtonActions();
        setupInitialState();
    }

    private void setupButtonActions() {
        btnAdd.setOnAction(event -> handleAdd());
        btnDelete.setOnAction(event -> handleDelete());
        btnFix.setOnAction(event -> handleFix());
        btnSave.setOnAction(event -> handleSave());
        btnAdmin.setOnAction(event -> handleAdmin());
        btnStaff.setOnAction(event -> handleStaff());
        iconHide.setOnMouseClicked(event -> setupPasswordVisibility());
    }

    private void setupInitialState() {
        btnAdd.setDisable(false);
        btnFix.setDisable(true);
        btnDelete.setDisable(true);
        btnSave.setDisable(true);
    }

    private void setAccountInfo(User user) {
        if (user != null) {
            txtUserid.setText(String.valueOf(user.getUserId()));
            txtUsername.setText(user.getUsername());
            txtPassword.setText(user.getPassword());
            
            if (User.ROLE_ADMIN.equals(user.getRole())) {
                btnAdmin.setSelected(true);
                btnStaff.setSelected(false);
            } else {
                btnAdmin.setSelected(false);
                btnStaff.setSelected(true);
            }
            
            idUserCurrent = user.getUserId();
            isNewUser = false;
        }
    }

    private void handleAdd() {
        clearFields();
        enableEditing();
        isNewUser = true;
        btnAdd.setDisable(true);
        btnFix.setDisable(true);
        btnDelete.setDisable(true);
        btnSave.setDisable(false);
        btnAdmin.setDisable(false);
        btnStaff.setDisable(false);
        
        // Get and display the next available user ID
        int nextId = getNextUserId();
        txtUserid.setText(String.valueOf(nextId));
        idUserCurrent = nextId;
    }

    private void handleDelete() {
        if (idUserCurrent > 0) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Xác nhận xóa");
            alert.setHeaderText("Bạn có chắc chắn muốn xóa người dùng này?");
            alert.setContentText("Hành động này không thể hoàn tác.");

            if (alert.showAndWait().get() == ButtonType.OK) {
                User user = new User();
                user.setUserId(idUserCurrent);
                int result = userDAO.delete(user);
                
                if (result > 0) {
                    showAlert("Thành công", "Xóa người dùng thành công!", Alert.AlertType.INFORMATION);
                    clearFields();
                    setupInitialState();
                } else {
                    showAlert("Lỗi", "Không thể xóa người dùng!", Alert.AlertType.ERROR);
                }
            }
        }
    }

    private void handleFix() {
        enableEditing();
        btnFix.setDisable(true);
        btnDelete.setDisable(true);
        btnSave.setDisable(false);
        btnAdmin.setDisable(false);
        btnStaff.setDisable(false);
    }

    private void handleSave() {
        if (validateInput()) {
            User user = new User();
            if (!isNewUser) {
                user.setUserId(idUserCurrent);
            }
            user.setUsername(txtUsername.getText());
            user.setPassword(txtPassword.getText());
            user.setRole(btnAdmin.isSelected() ? User.ROLE_ADMIN : User.ROLE_USER);
            user.setCreatedAt(LocalDateTime.now());
            user.setActive(true);

            int result;
            if (isNewUser) {
                if (userDAO.checkDuplicate(user.getUsername(), user.getRole())) {
                    showAlert("Lỗi", "Tên người dùng đã tồn tại!", Alert.AlertType.ERROR);
                    return;
                }
                result = userDAO.insert(user);
            } else {
                result = userDAO.update(user);
            }

            if (result > 0) {
                showAlert("Thành công", "Lưu thông tin người dùng thành công!", Alert.AlertType.INFORMATION);
                disableEditing();
                setupInitialState();
            } else {
                showAlert("Lỗi", "Không thể lưu thông tin người dùng!", Alert.AlertType.ERROR);
            }
        }
    }

    private void handleAdmin() {
        btnAdmin.setSelected(true);
        btnStaff.setSelected(false);
    }

    private void handleStaff() {
        btnAdmin.setSelected(false);
        btnStaff.setSelected(true);
    }

    private void setupPasswordVisibility() {
        // Đồng bộ text giữa hai trường
        txtPasswordVisible.textProperty().bindBidirectional(txtPassword.textProperty());

        iconHide.setOnMouseClicked(event -> {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                txtPasswordVisible.setVisible(true);
                txtPasswordVisible.setManaged(true);
                txtPassword.setVisible(false);
                txtPassword.setManaged(false);
                iconHide.setGlyphName("EYE");
            } else {
                txtPasswordVisible.setVisible(false);
                txtPasswordVisible.setManaged(false);
                txtPassword.setVisible(true);
                txtPassword.setManaged(true);
                iconHide.setGlyphName("EYE_SLASH");
            }
        });
    }

    private void clearFields() {
        txtUserid.clear();
        txtUsername.clear();
        txtPassword.clear();
        btnAdmin.setSelected(false);
        btnStaff.setSelected(false);
    }

    private void enableEditing() {
        txtUsername.setDisable(false);
        txtPassword.setDisable(false);
    }

    private void disableEditing() {
        txtUsername.setDisable(true);
        txtPassword.setDisable(true);
        btnAdmin.setDisable(true);
        btnStaff.setDisable(true);
    }

    private boolean validateInput() {
        if (txtUsername.getText().isEmpty() || txtPassword.getText().isEmpty()) {
            showAlert("Lỗi", "Vui lòng điền đầy đủ thông tin!", Alert.AlertType.ERROR);
            return false;
        }
        if (!btnAdmin.isSelected() && !btnStaff.isSelected()) {
            showAlert("Lỗi", "Vui lòng chọn vai trò người dùng!", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private int getNextUserId() {
        ArrayList<User> users = userDAO.findAll();
        if (users == null || users.isEmpty()) {
            return 1;
        }
        return users.stream()
                .mapToInt(User::getUserId)
                .max()
                .orElse(0) + 1;
    }

    public void setUser(User user) {
        setAccountInfo(user);
    }
}

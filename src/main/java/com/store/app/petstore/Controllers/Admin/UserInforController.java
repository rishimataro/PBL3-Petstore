package com.store.app.petstore.Controllers.Admin;

import com.store.app.petstore.Controllers.ControllerUtils;
import com.store.app.petstore.DAO.UserDAO;
import com.store.app.petstore.Models.Entities.User;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.application.Platform;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class UserInforController implements Initializable {

    @FXML
    private Button btnAdd;

    @FXML
    private RadioButton rbtnAdmin;

    @FXML
    private Button btnLock;

    @FXML
    private Button btnRestore;

    @FXML
    private Button btnFix;

    @FXML
    private Button btnSave;

    @FXML
    private RadioButton rbtnStaff;

    @FXML
    private ToggleGroup roleGroup;

    @FXML
    private AnchorPane customerInforPopup;

    @FXML
    private FontAwesomeIconView closeIcon;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private TextField txtUserid;

    @FXML
    private TextField txtUsername;

    private int idUserCurrent;
    private boolean isNewUser = true;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtUserid.setDisable(true);
        txtUsername.setDisable(true);
        txtPassword.setDisable(true);

        rbtnAdmin.setDisable(true);
        rbtnStaff.setDisable(true);

        if (roleGroup == null) {
            roleGroup = new ToggleGroup();
        }

        if (rbtnAdmin.getToggleGroup() != roleGroup) {
            rbtnAdmin.setToggleGroup(roleGroup);
        }
        if (rbtnStaff.getToggleGroup() != roleGroup) {
            rbtnStaff.setToggleGroup(roleGroup);
        }

        rbtnStaff.setSelected(true);
        roleGroup.selectToggle(rbtnStaff);

        setupInputValidation();
        setupButtonActions();
        setupInitialState();
    }
    
    private void setupInputValidation() {
        // Username validation: 3-50 characters, letters, numbers, underscores, dots
        // Must start with a letter, no consecutive special characters
        txtUsername.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("^[a-zA-Z][a-zA-Z0-9_.]*$")) {
                txtUsername.setText(oldValue);
            } else if (newValue.length() > 50) {
                txtUsername.setText(oldValue);
            }
        });
        
        // Password strength indicator
        txtPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 50) {
                txtPassword.setText(oldValue);
            }
        });
    }

    private void setupButtonActions() {
        btnAdd.setOnAction(event -> handleAdd());
        btnLock.setOnAction(event -> handleLock());
        btnRestore.setOnAction(event -> handleRestore());
        btnFix.setOnAction(event -> handleFix());
        btnSave.setOnAction(event -> handleSave());

        if (closeIcon != null) {
            closeIcon.setOnMouseClicked(event -> closeWindow());
        }

        rbtnAdmin.setOnAction(event -> handleRoleSelection(true));
        rbtnStaff.setOnAction(event -> handleRoleSelection(false));

    }

    private void handleRoleSelection(boolean isAdmin) {
        if (isAdmin) {
            roleGroup.selectToggle(rbtnAdmin);
        } else {
            btnLock.setDisable(true);

            if (txtUsername.getText() != null && !txtUsername.getText().isEmpty()) {
                txtUsername.setEditable(false);
            }

            roleGroup.selectToggle(rbtnStaff);
        }

        rbtnAdmin.setSelected(isAdmin);
        rbtnStaff.setSelected(!isAdmin);
    }

    private void setupInitialState() {
        btnAdd.setDisable(false);
        btnFix.setDisable(true);
        btnLock.setDisable(true);
        btnRestore.setDisable(true);
        btnSave.setDisable(true);
        rbtnAdmin.setDisable(true);
        rbtnStaff.setDisable(true);

        rbtnStaff.setSelected(true);
    }

    private void setAccountInfo(User user) {
        if (user != null) {
            txtUserid.setText(String.valueOf(user.getUserId()));
            txtUsername.setText(user.getUsername());
            txtPassword.setText(user.getPassword()); // Display the actual password for editing

            if (User.ROLE_ADMIN.equals(user.getRole())) {
                rbtnAdmin.setSelected(true);
            } else {
                rbtnStaff.setSelected(true);
            }

            idUserCurrent = user.getUserId();
            isNewUser = false;

            btnFix.setDisable(false);
            btnLock.setDisable(user.isActive());
            btnRestore.setDisable(user.isActive());
        }
    }

    private void handleAdd() {
        clearFields();
        enableEditing();
        isNewUser = true;

        btnAdd.setDisable(true);
        btnFix.setDisable(true);
        btnLock.setDisable(true);
        btnSave.setDisable(false);
        rbtnAdmin.setDisable(false);
        rbtnStaff.setDisable(false);

        rbtnStaff.setSelected(true);

        int nextId = getNextUserId();
        txtUserid.setText(String.valueOf(nextId));
        idUserCurrent = nextId;
    }

    private void handleLock() {
        if (idUserCurrent > 0) {
            if (ControllerUtils.showConfirmationAndWait("Xác nhận khóa tài khoản", "Bạn có chắc chắn muốn khóa người dùng này?")) {
                User user = UserDAO.findById(idUserCurrent);
                if (user == null) {
                    ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không tìm thấy người dùng!");
                    return;
                }

                user.setActive(false);
                int result = UserDAO.update(user);

                if (result > 0) {
                    ControllerUtils.showAlert(Alert.AlertType.INFORMATION, "Thành công", "Khóa người dùng thành công!");
                    clearFields();
                    setupInitialState();
                } else {
                    ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể khóa người dùng!");
                }
            }
        }
    }

    private void handleRestore() {
        if (idUserCurrent > 0) {
            if (ControllerUtils.showConfirmationAndWait("Xác nhận khôi phục tài khoản", "Bạn có chắc chắn muốn khôi phục người dùng này?")) {
                User user = UserDAO.findById(idUserCurrent);
                if (user == null) {
                    ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không tìm thấy người dùng!");
                    return;
                }

                user.setActive(true);
                int result = UserDAO.update(user);

                if (result > 0) {
                    ControllerUtils.showAlert(Alert.AlertType.INFORMATION, "Thành công", "Khôi phục người dùng thành công!");
                    clearFields();
                    setupInitialState();
                } else {
                    ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể khôi phục người dùng!");
                }
            }
        }
    }

    private void handleFix() {
        enableEditing();

        btnFix.setDisable(true);
        btnLock.setDisable(true);
        btnRestore.setDisable(true);
        btnAdd.setDisable(true);
        btnSave.setDisable(false);
        rbtnAdmin.setDisable(false);
        rbtnStaff.setDisable(false);
    }

    private void handleSave() {
        Toggle selectedToggle = roleGroup.getSelectedToggle();
        if (selectedToggle == null) {
            rbtnStaff.setSelected(true);
            roleGroup.selectToggle(rbtnStaff);
        }

        if (validateInput()) {
            User user = new User();
            if (!isNewUser) {
                user.setUserId(idUserCurrent);
            }
            user.setUsername(txtUsername.getText());
            user.setPassword(txtPassword.getText()); // Save the updated password

            boolean isAdmin = roleGroup.getSelectedToggle() == rbtnAdmin;

            user.setRole(isAdmin ? User.ROLE_ADMIN : User.ROLE_USER);
            user.setCreatedAt(LocalDateTime.now());
            user.setActive(true);

            int result;
            if (isNewUser) {
                if (UserDAO.checkDuplicate(user.getUsername().trim(), -1)) {
                    ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Tên người dùng đã tồn tại!");
                    return;
                }
                result = UserDAO.insert(user);
                idUserCurrent = Objects.requireNonNull(UserDAO.findByUsername(user.getUsername())).getUserId();
            } else {
                User oldUser = UserDAO.findById(idUserCurrent);
                if (oldUser == null) {
                    ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không tìm thấy người dùng!");
                    return;
                }
                String oldUsername = oldUser.getUsername().trim().toLowerCase();
                String newUsername = user.getUsername().trim().toLowerCase();

                if (!oldUsername.equals(newUsername)) {
                    if (UserDAO.checkDuplicate(user.getUsername().trim(), idUserCurrent)) {
                        ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Tên người dùng đã tồn tại!");
                        return;
                    }
                }
                result = UserDAO.update(user);
            }

            if (result > 0) {
                ControllerUtils.showAlert(Alert.AlertType.INFORMATION, "Thành công", "Lưu thông tin người dùng thành công!");
                disableEditing();
                btnFix.setDisable(false);
                User updatedUser = UserDAO.findById(idUserCurrent);
                btnLock.setDisable(!Objects.requireNonNull(updatedUser).isActive());
                btnRestore.setDisable(updatedUser.isActive());
                btnAdd.setDisable(false);
                btnSave.setDisable(true);
                isNewUser = false;
            } else {
                ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể lưu thông tin người dùng!");
            }
        }
    }

    private void clearFields() {
        txtUserid.clear();
        txtUsername.clear();
        txtPassword.clear();
        rbtnStaff.setSelected(true);
    }

    private void enableEditing() {
        txtUsername.setDisable(false);
        txtPassword.setDisable(false);
        rbtnAdmin.setDisable(false);
        rbtnStaff.setDisable(false);
        
        // Focus on username field when enabling editing
        Platform.runLater(() -> {
            if (txtUsername.getText().isEmpty()) {
                txtUsername.requestFocus();
            } else {
                txtPassword.requestFocus();
            }
        });
    }

    private void disableEditing() {
        rbtnAdmin.setDisable(true);
        rbtnStaff.setDisable(true);
        txtUsername.setDisable(true);
        txtPassword.setDisable(true);
    }

    private boolean validateInput() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();
        
        // Check required fields
        if (username.isEmpty() || password.isEmpty()) {
            ControllerUtils.showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng điền đầy đủ thông tin!");
            if (username.isEmpty()) txtUsername.requestFocus();
            else txtPassword.requestFocus();
            return false;
        }
        
        // Username validation
        if (username.length() < 3 || username.length() > 50) {
            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Tên đăng nhập phải từ 3 đến 50 ký tự!");
            txtUsername.requestFocus();
            return false;
        }
        if (!username.matches("^[a-zA-Z][a-zA-Z0-9_.]*$")) {
            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", 
                "Tên đăng nhập không hợp lệ!\n" +
                "- Bắt đầu bằng chữ cái\n" +
                "- Chỉ chứa chữ cái, số, dấu gạch dưới (_) và dấu chấm (.)\n" +
                "- Không có ký tự đặc biệt khác");
            txtUsername.requestFocus();
            return false;
        }
        if (username.matches(".*[_.]{2,}.*")) {
            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", 
                "Tên đăng nhập không được chứa các ký tự đặc biệt liên tiếp!");
            txtUsername.requestFocus();
            return false;
        }
        
        // Password validation
        if (password.length() < 6 || password.length() > 50) {
            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Mật khẩu phải từ 6 đến 50 ký tự!");
            txtPassword.requestFocus();
            return false;
        }
        
        // Role validation
        if (roleGroup.getSelectedToggle() == null) {
            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng chọn vai trò người dùng!");
            if (rbtnAdmin != null) rbtnAdmin.requestFocus();
            return false;
        }
        
        return true;
    }

    private void closeWindow() {
        Stage stage = (Stage) customerInforPopup.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }

    private int getNextUserId() {
        ArrayList<User> users = UserDAO.findAll();
        if (users.isEmpty()) {
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

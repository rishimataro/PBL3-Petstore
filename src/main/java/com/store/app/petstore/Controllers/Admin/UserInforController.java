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

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    // No longer needed as we have btnRestore
    // @FXML
    // private Button resetActiveButton;

    @FXML
    private FontAwesomeIconView closeIcon;

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
        txtPasswordVisible.setDisable(true);
        txtPasswordVisible.setVisible(false);

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

        setupButtonActions();
        setupInitialState();
    }

    private void setupButtonActions() {
        btnAdd.setOnAction(event -> handleAdd());
        btnLock.setOnAction(event -> handleLock());
        btnRestore.setOnAction(event -> handleRestore());
        btnFix.setOnAction(event -> handleFix());
        btnSave.setOnAction(event -> handleSave());

        // Add close icon action
        if (closeIcon != null) {
            closeIcon.setOnMouseClicked(event -> closeWindow());
        }

        rbtnAdmin.setOnAction(event -> handleRoleSelection(true));
        rbtnStaff.setOnAction(event -> handleRoleSelection(false));

        iconHide.setOnMouseClicked(event -> setupPasswordVisibility());
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
            txtPassword.setText(user.getPassword());

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
                User user = userDAO.findById(idUserCurrent);
                if (user == null) {
                    ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không tìm thấy người dùng!");
                    return;
                }

                user.setActive(false);
                int result = userDAO.update(user);

                if (result > 0) {
                    ControllerUtils.showAlert(Alert.AlertType.INFORMATION, "Thành công", "Khóa người dùng thành công!");
                    clearFields();
                    setupInitialState();
                    closeWindow();
                } else {
                    ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể khóa người dùng!");
                }
            }
        }
    }

    private void handleRestore() {
        if (idUserCurrent > 0) {
            if (ControllerUtils.showConfirmationAndWait("Xác nhận khôi phục tài khoản", "Bạn có chắc chắn muốn khôi phục người dùng này?")) {
                User user = userDAO.findById(idUserCurrent);
                if (user == null) {
                    ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không tìm thấy người dùng!");
                    return;
                }

                user.setActive(true);
                int result = userDAO.update(user);

                if (result > 0) {
                    ControllerUtils.showAlert(Alert.AlertType.INFORMATION, "Thành công", "Khôi phục người dùng thành công!");
                    clearFields();
                    setupInitialState();
                    closeWindow();
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
            user.setPassword(txtPassword.getText());

            boolean isAdmin = false;
            if (roleGroup.getSelectedToggle() == rbtnAdmin) {
                isAdmin = true;
            } else if (roleGroup.getSelectedToggle() == rbtnStaff) {
                isAdmin = false;
            } else if (rbtnAdmin.isSelected()) {
                isAdmin = true;
            } else if (rbtnStaff.isSelected()) {
                isAdmin = false;
            } else {
                rbtnStaff.setSelected(true);
                roleGroup.selectToggle(rbtnStaff);
                isAdmin = false;
            }

            user.setRole(isAdmin ? User.ROLE_ADMIN : User.ROLE_USER);

            user.setCreatedAt(LocalDateTime.now());
            user.setActive(true);

            int result;
            if (isNewUser) {
                if (userDAO.checkDuplicate(user.getUsername().trim(), -1)) {
                    ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Tên người dùng đã tồn tại!");
                    return;
                }
                result = userDAO.insert(user);
                // After inserting a new user, get the generated ID
                idUserCurrent = userDAO.findByUsername(user.getUsername()).getUserId();
            } else {
                User oldUser = userDAO.findById(idUserCurrent);
                if (oldUser == null) {
                    ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không tìm thấy người dùng!");
                    return;
                }
                String oldUsername = oldUser.getUsername().trim().toLowerCase();
                String newUsername = user.getUsername().trim().toLowerCase();

                if (!oldUsername.equals(newUsername)) {
                    if (userDAO.checkDuplicate(user.getUsername().trim(), idUserCurrent)) {
                        ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Tên người dùng đã tồn tại!");
                        return;
                    }
                }
                result = userDAO.update(user);
            }

            if (result > 0) {
                ControllerUtils.showAlert(Alert.AlertType.INFORMATION, "Thành công", "Lưu thông tin người dùng thành công!");
                disableEditing();
                btnFix.setDisable(false);
                User updatedUser = userDAO.findById(idUserCurrent);
                btnLock.setDisable(updatedUser.isActive() == false);
                btnRestore.setDisable(updatedUser.isActive() == true);
                btnAdd.setDisable(false);
                btnSave.setDisable(true);
                isNewUser = false;
                closeWindow();
            } else {
                ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể lưu thông tin người dùng!");
            }
        }
    }

    private void setupPasswordVisibility() {
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
        rbtnStaff.setSelected(true);
    }

    private void enableEditing() {
        txtUsername.setDisable(false);
        txtPassword.setDisable(false);
        txtPasswordVisible.setDisable(false);
        iconHide.setDisable(false);
    }

    private void disableEditing() {
        rbtnAdmin.setDisable(true);
        rbtnStaff.setDisable(true);

        txtUsername.setDisable(true);
        txtPassword.setDisable(true);
        txtPasswordVisible.setDisable(true);
        iconHide.setDisable(true);
    }

    private boolean validateInput() {
        if (txtUsername.getText().isEmpty() || txtPassword.getText().isEmpty()) {
            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng điền đầy đủ thông tin!");
            return false;
        }
        if (roleGroup.getSelectedToggle() == null) {
            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng chọn vai trò người dùng!");
            return false;
        }
        return true;
    }

    /**
     * Closes the current window
     */
    private void closeWindow() {
        Stage stage = (Stage) customerInforPopup.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
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
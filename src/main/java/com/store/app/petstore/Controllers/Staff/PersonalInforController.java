package com.store.app.petstore.Controllers.Staff;

import com.store.app.petstore.DAO.StaffDAO;
import com.store.app.petstore.DAO.UserDAO;
import com.store.app.petstore.Models.Entities.Staff;
import com.store.app.petstore.Models.Entities.User;
import com.store.app.petstore.Sessions.SessionManager;
import com.store.app.petstore.Utils.ValidationUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.store.app.petstore.Controllers.ControllerUtils.showAlert;

public class PersonalInforController implements Initializable {

    @FXML private TextField staff_id;
    @FXML private TextField staff_name;
    @FXML private TextField staff_phone;
    @FXML private TextField staff_email;
    @FXML private TextField staff_role;
    @FXML private TextField staff_address;
    @FXML private PasswordField old_pwd;
    @FXML private PasswordField new_pwd;
    @FXML private Button info_save_btn;
    @FXML private Button pwd_save_btn;
    @FXML private Button changeImageBtn;
    @FXML private Label fullNameLabel;
    @FXML private Label roleLabel;
    @FXML private Label staffIDLabel;
    @FXML private ImageView profileImage;

    private int userId;
    private Staff currentStaff;
    private User currentUser;
    private String originalImagePath;
    private boolean hasUnsavedChanges = false;

    // Default employee information
    private static final String DEFAULT_FULL_NAME = "Chưa cập nhật";
    private static final String DEFAULT_PHONE = "Chưa cập nhật";
    private static final String DEFAULT_EMAIL = "Chưa cập nhật";
    private static final String DEFAULT_ADDRESS = "Chưa cập nhật";
    private static final String DEFAULT_ROLE = "Nhân viên";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupEventHandlers();
        setupValidation();
        loadCurrentUserInfo();
    }

    private void loadCurrentUserInfo() {
        currentUser = SessionManager.getCurrentUser();
        currentStaff = SessionManager.getCurrentStaff();
        
        if (currentUser != null) {
            this.userId = currentUser.getUserId();
        loadStaffInfo();
        } else {
            showAlert(AlertType.ERROR, "Lỗi", "Không tìm thấy thông tin người dùng đăng nhập");
        }
    }

    private void setupEventHandlers() {
        info_save_btn.setOnAction(event -> handleUpdateInfo());
        pwd_save_btn.setOnAction(event -> handleChangePassword());
        changeImageBtn.setOnAction(event -> handleChangeImage());

        staff_name.textProperty().addListener((obs, oldVal, newVal) -> hasUnsavedChanges = true);
        staff_phone.textProperty().addListener((obs, oldVal, newVal) -> hasUnsavedChanges = true);
        staff_email.textProperty().addListener((obs, oldVal, newVal) -> hasUnsavedChanges = true);
        staff_address.textProperty().addListener((obs, oldVal, newVal) -> hasUnsavedChanges = true);
    }

    private void setupValidation() {
        staff_name.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.length() > 100) {
                staff_name.setText(oldVal);
            }
        });

        staff_phone.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.matches("\\d*") && !newVal.isEmpty()) {
                staff_phone.setText(oldVal);
        }
            if (newVal != null && newVal.length() > 11) {
                staff_phone.setText(oldVal);
    }
        });

        staff_email.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.length() > 100) {
                staff_email.setText(oldVal);
    }
        });

        staff_address.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.length() > 255) {
                staff_address.setText(oldVal);
            }
        });

    }

    private void loadStaffInfo() {
            try {
            currentStaff = StaffDAO.findByUserId(userId);
            
            if (currentStaff != null) {
                populateFieldsWithStaffData();
            } else {
                setDefaultStaffInformation();
            }
            
            loadProfileImage();
            hasUnsavedChanges = false;
            
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Lỗi", "Không thể tải thông tin nhân viên: " + e.getMessage());
            setDefaultStaffInformation();
        }
    }

    private void populateFieldsWithStaffData() {
        staff_id.setText(String.valueOf(currentStaff.getStaffId()));
        staff_name.setText(currentStaff.getFullName() != null ? currentStaff.getFullName() : DEFAULT_FULL_NAME);
        staff_phone.setText(currentStaff.getPhone() != null ? currentStaff.getPhone() : DEFAULT_PHONE);
        staff_email.setText(currentStaff.getEmail() != null ? currentStaff.getEmail() : DEFAULT_EMAIL);
        staff_role.setText(currentStaff.getRole() != null ? currentStaff.getRole() : DEFAULT_ROLE);
        staff_address.setText(currentStaff.getAddress() != null ? currentStaff.getAddress() : DEFAULT_ADDRESS);

        fullNameLabel.setText(currentStaff.getFullName() != null ? currentStaff.getFullName() : DEFAULT_FULL_NAME);
        roleLabel.setText(currentStaff.getRole() != null ? currentStaff.getRole().toUpperCase() : DEFAULT_ROLE.toUpperCase());
        staffIDLabel.setText("ID: " + currentStaff.getStaffId());
    }

    private void setDefaultStaffInformation() {
        staff_id.setText("Chưa có");
        staff_name.setText(DEFAULT_FULL_NAME);
        staff_phone.setText(DEFAULT_PHONE);
        staff_email.setText(DEFAULT_EMAIL);
        staff_role.setText(DEFAULT_ROLE);
        staff_address.setText(DEFAULT_ADDRESS);

        fullNameLabel.setText(DEFAULT_FULL_NAME);
        roleLabel.setText(DEFAULT_ROLE.toUpperCase());
        staffIDLabel.setText("ID: Chưa có");

        staff_role.setEditable(true);
    }

    private void loadProfileImage() {
        try {
            String imageUrl = currentUser.getImageUrl();
            originalImagePath = imageUrl;

            if (imageUrl != null && !imageUrl.isEmpty()) {
                Image image = null;
                if (imageUrl.startsWith("/")) {
                    URL resource = getClass().getResource(imageUrl);
                    if (resource != null) {
                        image = new Image(resource.toExternalForm());
                    } else {
                        File imageFile = new File("src/main/resources" + imageUrl);
                        if (imageFile.exists()) {
                            image = new Image(imageFile.toURI().toString());
                        }
                    }
                } else {
                    File imageFile = new File(imageUrl);
                    if (imageFile.exists()) {
                        image = new Image(imageFile.toURI().toString());
                    }
                }
                if (image != null && !image.isError()) {
                    profileImage.setImage(image);
                    return;
                }
            }
            loadDefaultImage();
        } catch (Exception e) {
            System.err.println("Lỗi khi tải ảnh đại diện: " + e.getMessage());
            loadDefaultImage();
        }
    }

    private void loadDefaultImage() {
        try {
            Image defaultImage = new Image(getClass().getResourceAsStream("/Images/User/default.jpg"));
            profileImage.setImage(defaultImage);
        } catch (Exception e) {
            System.err.println("Lỗi khi tải ảnh mặc định: " + e.getMessage());
        }
    }

    @FXML
    private void handleChangeImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh đại diện");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Tất cả ảnh", "*.png", "*.jpg", "*.jpeg"),
            new FileChooser.ExtensionFilter("JPG", "*.jpg"),
            new FileChooser.ExtensionFilter("PNG", "*.png"),
            new FileChooser.ExtensionFilter("JPEG", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(changeImageBtn.getScene().getWindow());
        if (selectedFile == null) {
            return;
        }
        try {
            if (!isValidImageFile(selectedFile)) {
                showAlert(AlertType.WARNING, "Lỗi", "Vui lòng chọn file ảnh hợp lệ (PNG, JPG, JPEG) và kích thước nhỏ hơn 5MB");
                return;
            }

            String newImagePath = saveImageFile(selectedFile);

            Image newImage = new Image(new File(newImagePath).toURI().toString());
            profileImage.setImage(newImage);

            String dbImagePath = "/Images/User/user" + userId + ".jpg";
            currentUser.setImageUrl(dbImagePath);
            if (UserDAO.update(currentUser) > 0) {
                SessionManager.setCurrentUser(currentUser);

                deleteOldImageFile(originalImagePath);
                originalImagePath = newImagePath;

                refreshMenuAvatar();
                showAlert(AlertType.INFORMATION, "Thành công", "Đã cập nhật ảnh đại diện thành công!");
            } else {
                loadProfileImage();
                showAlert(AlertType.ERROR, "Lỗi", "Không thể cập nhật ảnh đại diện trong cơ sở dữ liệu");
            }

        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Lỗi", "Không thể cập nhật ảnh đại diện: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateInfo() {
        try {
            if (!validateStaffInformation()) {
                return;
    }
            String fullName = staff_name.getText().trim();
            String phone = staff_phone.getText().trim();
            String email = staff_email.getText().trim().toLowerCase();
            String address = staff_address.getText().trim();
            String role = staff_role.getText().trim();
            // Show confirmation dialog
            if (!showConfirmationDialog("Cập nhật thông tin", 
                "Bạn có chắc chắn muốn cập nhật thông tin cá nhân không?")) {
                return;
            }

            boolean success = false;
            
            if (currentStaff != null) {
                currentStaff.setFullName(fullName);
                currentStaff.setPhone(phone);
                currentStaff.setEmail(email);
                currentStaff.setAddress(address);
                currentStaff.setRole(role);
                
                success = StaffDAO.update(currentStaff) > 0;
            } else {
                Staff newStaff = new Staff();
                newStaff.setUserId(userId);
                newStaff.setFullName(fullName);
                newStaff.setPhone(phone);
                newStaff.setEmail(email);
                newStaff.setAddress(address);
                newStaff.setRole(role);
                newStaff.setSalary(0.0);
                newStaff.setHireDate(LocalDateTime.now());
                newStaff.setActive(true);
                
                int result = StaffDAO.insert(newStaff);
                if (result > 0) {
                    currentStaff = StaffDAO.findByUserId(userId);
                    success = true;
                }
            }
            
            if (success) {
                SessionManager.setCurrentStaff(currentStaff);

                fullNameLabel.setText(fullName);
                roleLabel.setText(role.toUpperCase());
                if (currentStaff != null) {
                    staffIDLabel.setText("ID: " + currentStaff.getStaffId());
                    staff_id.setText(String.valueOf(currentStaff.getStaffId()));
                }
                
                hasUnsavedChanges = false;
                showAlert(AlertType.INFORMATION, "Thành công", "Cập nhật thông tin cá nhân thành công!");
            } else {
                showAlert(AlertType.ERROR, "Lỗi", "Không thể cập nhật thông tin. Vui lòng thử lại.");
            }
            
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Lỗi", "Lỗi khi cập nhật thông tin: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean showConfirmationDialog(String title, String content) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    @FXML
    private void handleChangePassword() {
        try {
            String oldPassword = old_pwd.getText();
            String newPassword = new_pwd.getText();

            if (!validatePasswordChange(oldPassword, newPassword)) {
                return;
            }

            if (!showConfirmationDialog("Đổi mật khẩu",
                "Bạn có chắc chắn muốn đổi mật khẩu không?")) {
                return;
            }

            if (!BCrypt.checkpw(oldPassword, currentUser.getPassword())) {
                showAlert(AlertType.WARNING, "Lỗi", "Mật khẩu cũ không đúng!");
                old_pwd.clear();
                old_pwd.requestFocus();
                return;
            }

            if (BCrypt.checkpw(newPassword, currentUser.getPassword())) {
                showAlert(AlertType.WARNING, "Lỗi", "Mật khẩu mới phải khác mật khẩu cũ!");
                new_pwd.clear();
                new_pwd.requestFocus();
                return;
            }

            // Hash the new password before setting it
            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            currentUser.setPassword(hashedPassword);

            if (UserDAO.update(currentUser) > 0) {
                SessionManager.setCurrentUser(currentUser);

                old_pwd.clear();
                new_pwd.clear();

                showAlert(AlertType.INFORMATION, "Thành công", "Đổi mật khẩu thành công!");
            } else {
                showAlert(AlertType.ERROR, "Lỗi", "Không thể đổi mật khẩu. Vui lòng thử lại.");
            }

        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Lỗi", "Lỗi khi đổi mật khẩu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateStaffInformation() {
        String fullName = staff_name.getText().trim();
        String phone = staff_phone.getText().trim();
        String email = staff_email.getText().trim();
        String address = staff_address.getText().trim();

        if (fullName.isEmpty() || fullName.equals(DEFAULT_FULL_NAME)) {
            showAlert(AlertType.WARNING, "Lỗi", "Vui lòng nhập họ tên!");
            staff_name.requestFocus();
            return false;
    }

        if (fullName.length() < 6 || fullName.length() > 100) {
            showAlert(AlertType.WARNING, "Lỗi", "Họ tên phải từ 6 đến 100 ký tự!");
            staff_name.requestFocus();
            return false;
        }

        if (phone.isEmpty() || phone.equals(DEFAULT_PHONE)) {
            showAlert(AlertType.WARNING, "Lỗi", "Vui lòng nhập số điện thoại!");
            staff_phone.requestFocus();
            return false;
        }

        if (!ValidationUtils.isValidPhone(phone)) {
            showAlert(AlertType.WARNING, "Lỗi", "Số điện thoại không hợp lệ!");
            staff_phone.requestFocus();
            return false;
        }

        if (email.isEmpty() || email.equals(DEFAULT_EMAIL)) {
            showAlert(AlertType.WARNING, "Lỗi", "Vui lòng nhập email!");
            staff_email.requestFocus();
            return false;
        }

        if (!ValidationUtils.isValidEmail(email)) {
            showAlert(AlertType.WARNING, "Lỗi", "Email không hợp lệ!");
            staff_email.requestFocus();
            return false;
        }

        if (address.isEmpty() || address.equals(DEFAULT_ADDRESS)) {
            showAlert(AlertType.WARNING, "Lỗi", "Vui lòng nhập địa chỉ!");
            staff_address.requestFocus();
            return false;
        }

        return true;
    }

    private boolean validatePasswordChange(String oldPassword, String newPassword) {
        if (oldPassword.isEmpty()) {
            showAlert(AlertType.WARNING, "Lỗi", "Vui lòng nhập mật khẩu cũ!");
            old_pwd.requestFocus();
            return false;
    }

        if (newPassword.isEmpty()) {
            showAlert(AlertType.WARNING, "Lỗi", "Vui lòng nhập mật khẩu mới!");
            new_pwd.requestFocus();
            return false;
        }

        if (newPassword.length() < 6) {
            showAlert(AlertType.WARNING, "Lỗi", "Mật khẩu mới phải có ít nhất 6 ký tự!");
            new_pwd.requestFocus();
            return false;
        }

        if (newPassword.length() > 50) {
            showAlert(AlertType.WARNING, "Lỗi", "Mật khẩu mới không được quá 50 ký tự!");
            new_pwd.requestFocus();
            return false;
        }

        if (oldPassword.equals(newPassword)) {
            showAlert(AlertType.WARNING, "Lỗi", "Mật khẩu mới phải khác mật khẩu cũ!");
            new_pwd.requestFocus();
            return false;
        }

        return true;
    }

    private boolean isValidImageFile(File file) {
        if (file == null || !file.exists()) {
            return false;
    }

        if (file.length() > 5 * 1024 * 1024) {
            return false;
}

        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg");
    }

    private String saveImageFile(File sourceFile) throws IOException {
        Path userImagesDir = Paths.get("src/main/resources/Images/User");
        if (!Files.exists(userImagesDir)) {
            Files.createDirectories(userImagesDir);
        }

        String newFileName = "user" + userId + ".jpg";
        Path targetPath = userImagesDir.resolve(newFileName);

        Files.copy(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return targetPath.toString();
    }

    private void deleteOldImageFile(String imagePath) {
        try {
            if (imagePath != null && !imagePath.isEmpty()) {
                File oldFile = new File(imagePath);
                if (oldFile.exists() && !oldFile.getName().contains("default")) {
                    oldFile.delete();
                }
            }
        } catch (Exception e) {
            System.err.println("Không thể xóa file ảnh cũ: " + e.getMessage());
        }
    }

    private void refreshMenuAvatar() {
        StaffMenuController.updateAvatar();
    }

}
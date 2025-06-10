package com.store.app.petstore.Controllers.Staff;

import com.store.app.petstore.DAO.StaffDAO;
import com.store.app.petstore.DAO.UserDAO;
import com.store.app.petstore.Models.Entities.Staff;
import com.store.app.petstore.Models.Entities.User;
import com.store.app.petstore.Sessions.SessionManager;

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
        // Name validation (2-50 characters, letters and Vietnamese characters only)
        staff_name.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;
            
            // Only allow letters, spaces, and Vietnamese characters
            if (!newVal.matches("[\\p{L} ]*")) {
                staff_name.setText(oldVal != null ? oldVal : "");
                return;
            }
            
            // Limit length to 50 characters
            if (newVal.length() > 50) {
                staff_name.setText(oldVal != null ? oldVal : newVal.substring(0, 50));
            }
            
            // Update hasUnsavedChanges
            if (!newVal.equals(oldVal)) {
                hasUnsavedChanges = true;
            }
        });

        // Phone validation (exactly 10 digits)
        staff_phone.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;
            
            // Only allow digits
            if (!newVal.matches("\\d*")) {
                staff_phone.setText(oldVal != null ? oldVal : "");
                return;
            }
            
            // Limit to 10 digits
            if (newVal.length() > 10) {
                staff_phone.setText(oldVal != null ? oldVal : newVal.substring(0, 10));
            }
            
            // Update hasUnsavedChanges
            if (!newVal.equals(oldVal)) {
                hasUnsavedChanges = true;
            }
        });

        // Email validation
        staff_email.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;
            
            // Limit length to 100 characters
            if (newVal.length() > 100) {
                staff_email.setText(oldVal != null ? oldVal : newVal.substring(0, 100));
                return;
            }
            
            // Show validation feedback
            if (!newVal.isEmpty() && !newVal.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
                staff_email.setStyle("-fx-border-color: #F44336; -fx-border-width: 1;");
            } else {
                staff_email.setStyle("");
            }
            
            // Update hasUnsavedChanges
            if (!newVal.equals(oldVal)) {
                hasUnsavedChanges = true;
            }
        });

        // Address validation (max 200 characters)
        staff_address.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;
            
            // Limit length to 200 characters
            if (newVal.length() > 200) {
                staff_address.setText(oldVal != null ? oldVal : newVal.substring(0, 200));
                return;
            }
            
            // Update hasUnsavedChanges
            if (!newVal.equals(oldVal)) {
                hasUnsavedChanges = true;
            }
        });
        
        // Password validation
        old_pwd.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.length() > 50) {
                old_pwd.setText(oldVal);
            }
        });
        
        new_pwd.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.length() > 50) {
                new_pwd.setText(oldVal);
            }
            updatePasswordStrength(newVal);
        });
    }
    
    private void updatePasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            new_pwd.setStyle("");
            return;
        }
        
        if (password.length() < 6) {
            new_pwd.setStyle("-fx-border-color: #F44336; -fx-border-width: 1;");
        } else {
            new_pwd.setStyle("-fx-border-color: #4CAF50; -fx-border-width: 1;");
        }
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

            // Set the plain text password - it will be hashed in UserDAO
            currentUser.setPassword(newPassword);

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
        String email = staff_email.getText().trim().toLowerCase();
        String address = staff_address.getText().trim();

        // Name validation
        if (fullName.isEmpty() || fullName.equals(DEFAULT_FULL_NAME)) {
            showAlert(AlertType.WARNING, "Lỗi", "Vui lòng nhập họ tên!");
            staff_name.requestFocus();
            return false;
        }

        if (fullName.length() < 2 || fullName.length() > 50) {
            showAlert(AlertType.WARNING, "Lỗi", "Họ tên phải từ 2 đến 50 ký tự!");
            staff_name.requestFocus();
            return false;
        }
        
        if (!fullName.matches("^[\\p{L} ]+$")) {
            showAlert(AlertType.WARNING, "Lỗi", "Họ tên chỉ được chứa chữ cái và dấu cách!");
            staff_name.requestFocus();
            return false;
        }

        if (phone.isEmpty() || phone.equals(DEFAULT_PHONE)) {
            showAlert(AlertType.WARNING, "Lỗi", "Vui lòng nhập số điện thoại!");
            staff_phone.requestFocus();
            return false;
        }

        if (!phone.matches("^\\d{10}$")) {
            showAlert(AlertType.WARNING, "Lỗi", "Số điện thoại phải có đúng 10 chữ số!");
            staff_phone.requestFocus();
            return false;
        }

        if (email.isEmpty() || email.equals(DEFAULT_EMAIL)) {
            showAlert(AlertType.WARNING, "Lỗi", "Vui lòng nhập địa chỉ email!");
            staff_email.requestFocus();
            return false;
        }

        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            showAlert(AlertType.WARNING, "Lỗi", 
                "Địa chỉ email không hợp lệ!\n" +
                "Ví dụ: example@domain.com");
            staff_email.requestFocus();
            return false;
        }

        if (address.length() > 200) {
            showAlert(AlertType.WARNING, "Lỗi", "Địa chỉ không được vượt quá 200 ký tự!");
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
            showAlert(AlertType.WARNING, "Lỗi", "Mật khẩu không được vượt quá 50 ký tự!");
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
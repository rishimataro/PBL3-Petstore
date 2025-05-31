package com.store.app.petstore.Controllers.Staff;

import com.store.app.petstore.Controllers.ControllerUtils;
import com.store.app.petstore.Models.DatabaseManager;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.mindrot.jbcrypt.BCrypt;

public class PersonalInforController {

    @FXML
    private TextField staff_id;
    @FXML
    private TextField staff_name;
    @FXML
    private TextField staff_phone;
    @FXML
    private TextField staff_email;
    @FXML
    private TextField staff_role;
    @FXML
    private TextField staff_address;

    @FXML
    private PasswordField old_pwd;
    @FXML
    private PasswordField new_pwd;

    @FXML
    private Button info_save_btn;
    @FXML
    private Button pwd_save_btn;
    @FXML
    private Button changeImageBtn;

    @FXML
    private Label fullNameLabel;
    @FXML
    private Label roleLabel;
    @FXML
    private Label staffIDLabel;
    @FXML
    private ImageView profileImage;

    private int userId;
    private String currentImagePath;

    @FXML
    private void initialize() {
        info_save_btn.setOnAction(event -> handleUpdateInfo());
        pwd_save_btn.setOnAction(event -> handleChangePassword());
        changeImageBtn.setOnAction(event -> handleChangeImage());
    }

    public void setUserId(int id) {
        this.userId = id;
        loadStaffInfo();
    }

    private void loadStaffInfo() {
        try (Connection conn = DatabaseManager.connect()) {
            String staffSql = "SELECT * FROM Staffs WHERE user_id = ?";
            PreparedStatement staffStmt = conn.prepareStatement(staffSql);
            staffStmt.setInt(1, userId);
            ResultSet staffRs = staffStmt.executeQuery();

            if (staffRs.next()) {
                staff_id.setText(String.valueOf(staffRs.getInt("staff_id")));
                staff_name.setText(staffRs.getString("full_name"));
                staff_phone.setText(staffRs.getString("phone"));
                staff_email.setText(staffRs.getString("email"));
                staff_role.setText(staffRs.getString("role"));
                staff_address.setText(staffRs.getString("address"));

                fullNameLabel.setText(staffRs.getString("full_name"));
                roleLabel.setText(staffRs.getString("role").toUpperCase());
                staffIDLabel.setText(String.valueOf(staffRs.getInt("staff_id")));
            }

            String userSql = "SELECT image_url FROM Users WHERE user_id = ?";
            PreparedStatement userStmt = conn.prepareStatement(userSql);
            userStmt.setInt(1, userId);
            ResultSet userRs = userStmt.executeQuery();

            if (userRs.next()) {
                String imageUrl = userRs.getString("image_url");
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    try {
                        File imageFile = new File(imageUrl);
                        if (imageFile.exists()) {
                            Image image = new Image(imageFile.toURI().toString());
                            profileImage.setImage(image);
                            currentImagePath = imageUrl;
                        } else {
                            loadDefaultImage();
                        }
                    } catch (Exception e) {
                        loadDefaultImage();
                    }
                } else {
                    loadDefaultImage();
                }
            } else {
                loadDefaultImage();
            }
        } catch (Exception e) {
            e.printStackTrace();
            ControllerUtils.showAlert(AlertType.ERROR, "Lỗi", "Không thể tải thông tin: " + e.getMessage());
        }
    }

    private void loadDefaultImage() {
        Image defaultImage = new Image(getClass().getResourceAsStream("/Images/dog.png"));
        profileImage.setImage(defaultImage);
        currentImagePath = null;
    }

    @FXML
    private void handleChangeImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh đại diện");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        File selectedFile = fileChooser.showOpenDialog(changeImageBtn.getScene().getWindow());
        if (selectedFile != null) {
            try {
                File imagesDir = new File("src/main/resources/Images/User");
                if (!imagesDir.exists()) {
                    imagesDir.mkdirs();
                }

                String newFileName = "user_" + userId + "_" + System.currentTimeMillis() +
                        selectedFile.getName().substring(selectedFile.getName().lastIndexOf("."));
                File newFile = new File(imagesDir, newFileName);

                Files.copy(selectedFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                Image newImage = new Image(newFile.toURI().toString());
                profileImage.setImage(newImage);
                currentImagePath = newFile.getAbsolutePath();

                updateImagePathInDatabase(newFile.getAbsolutePath());

                ControllerUtils.showAlert(AlertType.INFORMATION, "Thành công", "Cập nhật ảnh đại diện thành công!");
            } catch (Exception e) {
                e.printStackTrace();
                ControllerUtils.showAlert(AlertType.ERROR, "Lỗi", "Không thể cập nhật ảnh: " + e.getMessage());
            }
        }
    }

    private void updateImagePathInDatabase(String imagePath) {
        try (Connection conn = DatabaseManager.connect()) {
            String sql = "UPDATE Users SET image_url = ? WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, imagePath);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            ControllerUtils.showAlert(AlertType.ERROR, "Lỗi",
                    "Không thể cập nhật đường dẫn ảnh trong database: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateInfo() {
        if (staff_name.getText().isEmpty() || staff_phone.getText().isEmpty() ||
                staff_email.getText().isEmpty() || staff_role.getText().isEmpty() ||
                staff_address.getText().isEmpty()) {
            ControllerUtils.showAlert(AlertType.ERROR, "Lỗi", "Vui lòng điền đầy đủ thông tin!");
            return;
        }

        try (Connection conn = DatabaseManager.connect()) {
            String sql = "UPDATE Staffs SET full_name = ?, phone = ?, email = ?, role = ?, address = ? WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, staff_name.getText());
            stmt.setString(2, staff_phone.getText());
            stmt.setString(3, staff_email.getText());
            stmt.setString(4, staff_role.getText());
            stmt.setString(5, staff_address.getText());
            stmt.setInt(6, userId);

            int updated = stmt.executeUpdate();
            if (updated > 0) {
                fullNameLabel.setText(staff_name.getText());
                roleLabel.setText(staff_role.getText().toUpperCase());
                ControllerUtils.showAlert(AlertType.INFORMATION, "Thành công", "Cập nhật thông tin thành công!");
            } else {
                ControllerUtils.showAlert(AlertType.WARNING, "Cảnh báo", "Không có thông tin nào được cập nhật!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ControllerUtils.showAlert(AlertType.ERROR, "Lỗi", "Không thể cập nhật thông tin: " + e.getMessage());
        }
    }

    @FXML
    private void handleChangePassword() {
        String oldPassword = old_pwd.getText();
        String newPassword = new_pwd.getText();

        if (oldPassword.isEmpty() || newPassword.isEmpty()) {
            ControllerUtils.showAlert(AlertType.ERROR, "Lỗi", "Vui lòng điền đầy đủ mật khẩu cũ và mật khẩu mới!");
            return;
        }

        if (newPassword.length() < 6) {
            ControllerUtils.showAlert(AlertType.ERROR, "Lỗi", "Mật khẩu mới phải có ít nhất 6 ký tự!");
            return;
        }

        if (oldPassword.equals(newPassword)) {
            ControllerUtils.showAlert(AlertType.ERROR, "Lỗi", "Mật khẩu mới không được giống mật khẩu cũ!");
            return;
        }

        try (Connection conn = DatabaseManager.connect()) {
            String checkSql = "SELECT password FROM Users WHERE user_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, userId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                String currentHashedPwd = rs.getString("password");

                if (!BCrypt.checkpw(oldPassword, currentHashedPwd)) {
                    ControllerUtils.showAlert(AlertType.ERROR, "Lỗi", "Mật khẩu cũ không đúng!");
                    return;
                }
            } else {
                ControllerUtils.showAlert(AlertType.ERROR, "Lỗi", "Không tìm thấy người dùng!");
                return;
            }

            String updateSql = "UPDATE Users SET password = ? WHERE user_id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateSql);
            updateStmt.setString(1, BCrypt.hashpw(newPassword, BCrypt.gensalt()));
            updateStmt.setInt(2, userId);

            int updated = updateStmt.executeUpdate();
            if (updated > 0) {
                ControllerUtils.showAlert(AlertType.INFORMATION, "Thành công", "Đổi mật khẩu thành công!");
                old_pwd.clear();
                new_pwd.clear();
            } else {
                ControllerUtils.showAlert(AlertType.ERROR, "Lỗi", "Không thể đổi mật khẩu!");
            }
        } catch (Exception e) {
            ControllerUtils.showAlert(AlertType.ERROR, "Lỗi", "Lỗi khi đổi mật khẩu: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

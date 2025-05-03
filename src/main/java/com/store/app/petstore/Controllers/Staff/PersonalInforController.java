package com.store.app.petstore.Controllers.Staff;

import com.store.app.petstore.Models.DatabaseManager;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.nio.file.Files;
import java.nio.file.Path;
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

   private int userId; // user_id hiện tại (đăng nhập)
   private String currentImagePath; // Đường dẫn ảnh hiện tại

   @FXML
   private void initialize() {
       // Thiết lập sự kiện cho các nút
       info_save_btn.setOnAction(event -> handleUpdateInfo());
       pwd_save_btn.setOnAction(event -> handleChangePassword());
       changeImageBtn.setOnAction(event -> handleChangeImage());
   }

   public void setUserId(int id) {
       this.userId = id;
       loadStaffInfo();
   }

   // Load dữ liệu từ bảng Staffs và Users
   private void loadStaffInfo() {
       try (Connection conn = DatabaseManager.connect()) {
           // Load thông tin từ bảng Staffs
           String staffSql = "SELECT * FROM Staffs WHERE user_id = ?";
           PreparedStatement staffStmt = conn.prepareStatement(staffSql);
           staffStmt.setInt(1, userId);
           ResultSet staffRs = staffStmt.executeQuery();

           if (staffRs.next()) {
               // Cập nhật thông tin vào các trường nhập liệu
               staff_id.setText(String.valueOf(staffRs.getInt("staff_id")));
               staff_name.setText(staffRs.getString("full_name"));
               staff_phone.setText(staffRs.getString("phone"));
               staff_email.setText(staffRs.getString("email"));
               staff_role.setText(staffRs.getString("role"));

               // Cập nhật thông tin hiển thị
               fullNameLabel.setText(staffRs.getString("full_name"));
               roleLabel.setText(staffRs.getString("role").toUpperCase());
               staffIDLabel.setText(String.valueOf(staffRs.getInt("staff_id")));
           }

           // Load ảnh từ bảng Users
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
           showAlert("Lỗi", "Không thể tải thông tin: " + e.getMessage());
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
           new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
       );

       File selectedFile = fileChooser.showOpenDialog(changeImageBtn.getScene().getWindow());
       if (selectedFile != null) {
           try {
               // Tạo thư mục images nếu chưa tồn tại
               File imagesDir = new File("src/main/resources/Images/User");
               if (!imagesDir.exists()) {
                   imagesDir.mkdirs();
               }

               // Tạo tên file mới dựa trên user_id
               String newFileName = "user_" + userId + "_" + System.currentTimeMillis() + 
                                   selectedFile.getName().substring(selectedFile.getName().lastIndexOf("."));
               File newFile = new File(imagesDir, newFileName);

               // Copy file ảnh vào thư mục images
               Files.copy(selectedFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

               // Cập nhật ảnh trong UI
               Image newImage = new Image(newFile.toURI().toString());
               profileImage.setImage(newImage);
               currentImagePath = newFile.getAbsolutePath();

               // Cập nhật đường dẫn ảnh trong database
               updateImagePathInDatabase(newFile.getAbsolutePath());

               showAlert("Thành công", "Đã cập nhật ảnh đại diện!");
           } catch (Exception e) {
               showAlert("Lỗi", "Không thể cập nhật ảnh: " + e.getMessage());
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
           showAlert("Lỗi", "Không thể cập nhật đường dẫn ảnh trong database: " + e.getMessage());
       }
   }

   @FXML
   private void handleUpdateInfo() {
       // Kiểm tra các trường bắt buộc
       if (staff_name.getText().isEmpty() || staff_phone.getText().isEmpty() ||
           staff_email.getText().isEmpty() || staff_role.getText().isEmpty()) {
           showAlert("Lỗi", "Vui lòng điền đầy đủ thông tin!");
           return;
       }

       try (Connection conn = DatabaseManager.connect()) {
           String sql = "UPDATE Staffs SET full_name = ?, phone = ?, email = ?, role = ? WHERE user_id = ?";
           PreparedStatement stmt = conn.prepareStatement(sql);
           stmt.setString(1, staff_name.getText());
           stmt.setString(2, staff_phone.getText());
           stmt.setString(3, staff_email.getText());
           stmt.setString(4, staff_role.getText());
           stmt.setInt(5, userId);

           int updated = stmt.executeUpdate();
           if (updated > 0) {
               // Cập nhật lại thông tin hiển thị
               fullNameLabel.setText(staff_name.getText());
               roleLabel.setText(staff_role.getText().toUpperCase());
               showAlert("Thành công", "Cập nhật thông tin thành công!");
           } else {
               showAlert("Thông báo", "Không có thông tin nào được cập nhật.");
           }
       } catch (Exception e) {
           showAlert("Lỗi", "Không thể cập nhật thông tin: " + e.getMessage());
       }
   }

   @FXML
   private void handleChangePassword() {
       String oldPassword = old_pwd.getText();
       String newPassword = new_pwd.getText();

       // Kiểm tra các trường bắt buộc
       if (oldPassword.isEmpty() || newPassword.isEmpty()) {
           showAlert("Lỗi", "Vui lòng điền đầy đủ mật khẩu cũ và mật khẩu mới!");
           return;
       }

       // Kiểm tra độ dài mật khẩu mới
       if (newPassword.length() < 6) {
           showAlert("Lỗi", "Mật khẩu mới phải có ít nhất 6 ký tự!");
           return;
       }

       // Kiểm tra mật khẩu mới không được giống mật khẩu cũ
       if (oldPassword.equals(newPassword)) {
           showAlert("Lỗi", "Mật khẩu mới không được giống mật khẩu cũ!");
           return;
       }

       try (Connection conn = DatabaseManager.connect()) {
           // Kiểm tra mật khẩu cũ
           String checkSql = "SELECT password FROM Users WHERE user_id = ?";
           PreparedStatement checkStmt = conn.prepareStatement(checkSql);
           checkStmt.setInt(1, userId);
           ResultSet rs = checkStmt.executeQuery();

           if (rs.next()) {
               String currentHashedPwd = rs.getString("password");
               
               if (!BCrypt.checkpw(oldPassword, currentHashedPwd)) {
                   showAlert("Lỗi", "Mật khẩu cũ không đúng!");
                   return;
               }
           } else {
               showAlert("Lỗi", "Không tìm thấy người dùng!");
               return;
           }

           // Cập nhật mật khẩu mới
           String updateSql = "UPDATE Users SET password = ? WHERE user_id = ?";
           PreparedStatement updateStmt = conn.prepareStatement(updateSql);
           updateStmt.setString(1, BCrypt.hashpw(newPassword, BCrypt.gensalt()));
           updateStmt.setInt(2, userId);

           int updated = updateStmt.executeUpdate();
           if (updated > 0) {
               showAlert("Thành công", "Đổi mật khẩu thành công!");
               // Xóa nội dung các trường mật khẩu
               old_pwd.clear();
               new_pwd.clear();
           } else {
               showAlert("Lỗi", "Không thể đổi mật khẩu.");
           }
       } catch (Exception e) {
           showAlert("Lỗi", "Lỗi khi đổi mật khẩu: " + e.getMessage());
           e.printStackTrace();
       }
   }

   private void showAlert(String title, String message) {
       Alert alert = new Alert(AlertType.INFORMATION);
       alert.setTitle(title);
       alert.setHeaderText(null);
       alert.setContentText(message);
       alert.showAndWait();
   }
}

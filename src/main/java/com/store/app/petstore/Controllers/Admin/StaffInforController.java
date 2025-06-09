package com.store.app.petstore.Controllers.Admin;

import com.store.app.petstore.Controllers.ControllerUtils;
import com.store.app.petstore.DAO.StaffDAO;
import com.store.app.petstore.DAO.UserDAO;
import com.store.app.petstore.Models.Entities.Staff;
import com.store.app.petstore.Models.Entities.User;
import com.store.app.petstore.Utils.ImageUtils;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import java.net.URL;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class StaffInforController implements Initializable {

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnFix;

    @FXML
    private Button btnSave;

    @FXML
    private ChoiceBox<String> cbPosition;

    @FXML
    private ChoiceBox<String> cbStatus;

    @FXML
    private AnchorPane staffInforPopup;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtPhone;

    @FXML
    private TextField txtSalary;

    @FXML
    private TextField txtStaffId;

    @FXML
    private TextField txtAddress;

    @FXML
    private FontAwesomeIconView closeIcon;

    @FXML
    private Rectangle imgStaff;

    @FXML
    private FontAwesomeIconView btnChange;

    private int idStaffCurrent;
    private boolean isNewStaff = true;
    private String tempImageUrl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtStaffId.setDisable(true);
        txtName.setDisable(true);
        txtPhone.setDisable(true);
        txtEmail.setDisable(true);
        txtSalary.setDisable(true);
        txtAddress.setDisable(true);
        cbPosition.setDisable(true);
        cbStatus.setDisable(true);

        setupInitialState();
        setupButtonActions();
        setupChoiceBoxes();
    }

    private void setupChoiceBoxes() {
        cbPosition.getItems().addAll("thu ngân", "bán hàng", "chăm sóc thú cưng", "tư vấn");
        cbStatus.getItems().addAll("Hiệu lực", "Nghỉ làm");
    }

    private void setupButtonActions() {
        btnAdd.setOnAction(event -> handleAdd());
        btnDelete.setOnAction(event -> handleDelete());
        btnFix.setOnAction(event -> handleFix());
        btnSave.setOnAction(event -> handleSave());

        if (closeIcon != null) {
            closeIcon.setOnMouseClicked(event -> closeWindow());
        }
    }

    private void setStaffInfo(Staff staff) {
        if(staff != null) {
            txtStaffId.setText(String.valueOf(staff.getStaffId()));
            txtName.setText(staff.getFullName());
            txtPhone.setText(staff.getPhone());
            txtEmail.setText(staff.getEmail());
            txtSalary.setText(String.valueOf(staff.getSalary()));
            txtAddress.setText(staff.getAddress());
            cbPosition.setValue(staff.getRole());
            cbStatus.setValue(staff.isActive() ? "Hiệu lực" : "Nghỉ làm");

            idStaffCurrent = staff.getStaffId();
            isNewStaff = false;

            btnDelete.setDisable(false);
            btnFix.setDisable(false);
            btnSave.setDisable(true);

            loadStaffImage(staff);
        }
    }

    private void setupImage() {
        loadDefaultImage();
        btnChange.setOnMouseClicked(event -> handleChangeImage());
    }

    private void setupInitialState() {
        btnAdd.setDisable(false);
        btnDelete.setDisable(true);
        btnFix.setDisable(true);
        btnSave.setDisable(true);

        setupImage();
    }

    private void handleAdd() {
        clearFields();
        enableEditing();
        isNewStaff = true;

        btnAdd.setDisable(true);
        btnDelete.setDisable(true);
        btnFix.setDisable(true);
        btnSave.setDisable(false);

        int nextId = getNextStaffId();
        txtStaffId.setText(String.valueOf(nextId));
        idStaffCurrent = nextId;
    }

    private void handleDelete() {
        if (idStaffCurrent > 0) {
            if (ControllerUtils.showConfirmationAndWait("Xác nhận khóa tài khoản nhân viên", "Bạn có chắc chắn muốn khóa tài khoản nhân viên này?")) {
                Staff staff = StaffDAO.findById(idStaffCurrent);
                if (staff == null) {
                    ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không tìm thấy nhân viên!");
                    return;
                }

                staff.setActive(false);
                int result = StaffDAO.update(staff);

                User user = UserDAO.findById(staff.getUserId());
                if (user != null) {
                    user.setActive(false);
                    UserDAO.update(user);
                }

                if (result > 0) {
                    ControllerUtils.showAlert(Alert.AlertType.INFORMATION, "Thành công", "Khóa tài khoản nhân viên thành công!");
                    cbStatus.setValue("Nghỉ làm");
                    btnDelete.setDisable(true);
                } else {
                    ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể khóa tài khoản nhân viên!");
                }
            }
        }
    }

    private void handleFix() {
        enableEditing();

        btnAdd.setDisable(true);
        btnDelete.setDisable(true);
        btnFix.setDisable(true);
        btnSave.setDisable(false);
    }

    public static String removeDiacritics(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("").replaceAll("đ", "d").replaceAll("Đ", "D");
    }

    private void handleSave() {
        if (validateInput()) {
            if (isNewStaff) {
                String fullName = txtName.getText();
                String defaultUsername = removeDiacritics(fullName).toLowerCase().replaceAll("\\s+", "");
                String defaultPassword = "123456";

                User user = new User();
                user.setUsername(defaultUsername.trim());
                user.setPassword(defaultPassword);
                user.setRole(User.ROLE_USER);
                user.setCreatedAt(java.time.LocalDateTime.now());
                user.setActive(cbStatus.getValue().equals("Hiệu lực"));

                int userId = UserDAO.insert(user);

                if (userId <= 0) {
                    ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tạo tài khoản người dùng!");
                    return;
                }

                Staff staff = new Staff();
                staff.setUserId(userId);
                staff.setUsername(defaultUsername);
                staff.setPassword(defaultPassword);
                staff.setRole(User.ROLE_USER);
                staff.setActive(cbStatus.getValue().equals("Hiệu lực"));

                staff.setFullName(txtName.getText());
                staff.setPhone(txtPhone.getText());
                staff.setEmail(txtEmail.getText());
                staff.setSalary(Double.parseDouble(txtSalary.getText()));
                staff.setRole(cbPosition.getValue());
                staff.setHireDate(LocalDateTime.now());
                staff.setAddress(txtAddress.getText());

                if(tempImageUrl != null && !tempImageUrl.isEmpty()) {
                    staff.setImageUrl(tempImageUrl);
                }
                else {
                    staff.setImageUrl("/Images/User/user" + userId + ".jpg");
                }

                int result = StaffDAO.insert(staff);
                if (result > 0) {
                    idStaffCurrent = result;
                    isNewStaff = false;
                    ControllerUtils.showAlert(Alert.AlertType.INFORMATION, "Thành công", "Thêm nhân viên thành công!");
                    disableEditing();
                    btnAdd.setDisable(false);
                    btnDelete.setDisable(false);
                    btnFix.setDisable(false);
                    btnSave.setDisable(true);
                    tempImageUrl = null;
                } else {
                    ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể thêm nhân viên!");
                }
            } else {
                Staff staff = StaffDAO.findById(idStaffCurrent);
                if (staff != null) {
                    staff.setFullName(txtName.getText());
                    staff.setPhone(txtPhone.getText());
                    staff.setEmail(txtEmail.getText());
                    staff.setSalary(Double.parseDouble(txtSalary.getText()));
                    staff.setRole(cbPosition.getValue());
                    staff.setActive(cbStatus.getValue().equals("Hiệu lực"));
                    staff.setAddress(txtAddress.getText());

                    int result = StaffDAO.update(staff);
                    if (result > 0) {
                        ControllerUtils.showAlert(Alert.AlertType.INFORMATION, "Thành công", "Cập nhật nhân viên thành công!");
                        disableEditing();
                        btnAdd.setDisable(false);
                        btnDelete.setDisable(false);
                        btnFix.setDisable(false);
                        btnSave.setDisable(true);
                    } else {
                        ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể cập nhật nhân viên!");
                    }
                } else {
                    ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không tìm thấy nhân viên!");
                }
            }
        }
    }

    private void enableEditing() {
        txtName.setDisable(false);
        txtPhone.setDisable(false);
        txtEmail.setDisable(false);
        txtSalary.setDisable(false);
        txtAddress.setDisable(false);
        cbPosition.setDisable(false);
        cbStatus.setDisable(false);
    }

    private void disableEditing() {
        txtName.setDisable(true);
        txtPhone.setDisable(true);
        txtEmail.setDisable(true);
        txtSalary.setDisable(true);
        txtAddress.setDisable(true);
        cbPosition.setDisable(true);
        cbStatus.setDisable(true);
    }

    private boolean validateInput() {
        if (txtName.getText().isEmpty() || txtPhone.getText().isEmpty() ||
                txtEmail.getText().isEmpty() || txtSalary.getText().isEmpty() ||
                cbPosition.getValue() == null || cbStatus.getValue() == null) {
            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng điền đầy đủ thông tin!");
            return false;
        }
        return true;
    }

    private void clearFields() {
        txtStaffId.clear();
        txtName.clear();
        txtPhone.clear();
        txtEmail.clear();
        txtSalary.clear();
        cbPosition.setValue(null);
        cbStatus.setValue(null);
    }

    private int getNextStaffId() {
        ArrayList<Staff> staffs = StaffDAO.findAll();
        if (staffs.isEmpty()) {
            return 1;
        }
        return staffs.stream()
                .mapToInt(Staff::getStaffId)
                .max()
                .orElse(0) + 1;
    }

    public void closeWindow() {
        Stage stage = (Stage) staffInforPopup.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }

    public void setStaff(Staff staff) {
        setStaffInfo(staff);
    }

    private void loadDefaultImage() {
        try {
            InputStream defaultImageStream = getClass().getResourceAsStream("/Images/User/default.jpg");
            if (defaultImageStream != null) {
                Image defaultImg = new Image(defaultImageStream);
                setRectangleImage(defaultImg);
            } else {
                InputStream altImageStream = getClass().getResourceAsStream("/Images/dog.png");
                if (altImageStream != null) {
                    Image altImg = new Image(altImageStream);
                    setRectangleImage(altImg);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadStaffImage(Staff staff) {
        try {
            User user = UserDAO.findById(staff.getUserId());
            if (user != null && user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
                String imageUrl = user.getImageUrl();

                if (imageUrl.startsWith("/")) {
                    InputStream imageStream = getClass().getResourceAsStream(imageUrl);
                    if (imageStream != null) {
                        Image image = new Image(imageStream);
                        setRectangleImage(image);
                        return;
                    }
                }

                File imageFile = new File(imageUrl);
                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString());
                    setRectangleImage(image);
                    return;
                }

                try {
                    String resourceUrl = getClass().getResource(imageUrl).toExternalForm();
                    Image image = new Image(resourceUrl);
                    setRectangleImage(image);
                    return;
                } catch (Exception ex) {
                    // Ignore and continue to default image
                }
            }
            loadDefaultImage();
        } catch (Exception e) {
            e.printStackTrace();
            loadDefaultImage();
        }
    }

    private void setRectangleImage(Image image) {
        if (image == null) {
            return;
        }

        try {
            Canvas canvas = new Canvas(imgStaff.getWidth(), imgStaff.getHeight());
            GraphicsContext gc = canvas.getGraphicsContext2D();

            double imageWidth = image.getWidth();
            double imageHeight = image.getHeight();
            double rectWidth = imgStaff.getWidth();
            double rectHeight = imgStaff.getHeight();

            double scale = Math.max(rectWidth / imageWidth, rectHeight / imageHeight);

            double scaledWidth = imageWidth * scale;
            double scaledHeight = imageHeight * scale;

            double x = (rectWidth - scaledWidth) / 2;
            double y = (rectHeight - scaledHeight) / 2;

            gc.drawImage(image, x, y, scaledWidth, scaledHeight);

            WritableImage snapshot = new WritableImage((int)rectWidth, (int)rectHeight);
            canvas.snapshot(null, snapshot);

            ImagePattern pattern = new ImagePattern(snapshot);
            imgStaff.setFill(pattern);

        } catch (Exception e) {
            e.printStackTrace();

            try {
                ImagePattern pattern = new ImagePattern(image);
                imgStaff.setFill(pattern);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void handleChangeImage() {
        if (idStaffCurrent <= 0) {
            ControllerUtils.showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng lưu thông tin nhân viên trước khi thay đổi ảnh!");
            return;
        }

        Staff staff = StaffDAO.findById(idStaffCurrent);
        if (staff == null) {
            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không tìm thấy thông tin nhân viên!");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh đại diện");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(imgStaff.getScene().getWindow());
        if (selectedFile != null) {
            try {
                File imagesDir = new File("src/main/resources/Images/User");
                if (!imagesDir.exists()) {
                    imagesDir.mkdirs();
                }

                String fileExtension = selectedFile.getName().substring(selectedFile.getName().lastIndexOf("."));
                String newFileName = "user" + staff.getUserId() + fileExtension;
                File newFile = new File(imagesDir, newFileName);

                Files.copy(selectedFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                Image newImage = new Image(newFile.toURI().toString());
                setRectangleImage(newImage);

                User user = UserDAO.findById(staff.getUserId());
                if (user != null) {
                    user.setImageUrl("/Images/User/user" + staff.getUserId() + fileExtension);
                    UserDAO.update(user);
                    ControllerUtils.showAlert(Alert.AlertType.INFORMATION, "Thành công", "Cập nhật ảnh đại diện thành công!");
                } else {
                    ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không tìm thấy thông tin người dùng!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể cập nhật ảnh: " + e.getMessage());
            }
        }
    }
}

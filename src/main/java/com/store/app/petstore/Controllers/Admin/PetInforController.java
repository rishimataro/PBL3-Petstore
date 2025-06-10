package com.store.app.petstore.Controllers.Admin;

import com.store.app.petstore.Controllers.ControllerUtils;
import com.store.app.petstore.DAO.PetDAO;
import com.store.app.petstore.Models.Entities.Pet;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class PetInforController implements Initializable {

    @FXML
    private Button btnAdd;

    @FXML
    private FontAwesomeIconView btnChange;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnFix;

    @FXML
    private Button btnSave;

    @FXML
    private Rectangle imgPet;

    @FXML
    private AnchorPane petInforPopup;

    @FXML
    private RadioButton rbtnCat;

    @FXML
    private RadioButton rbtnDog;

    @FXML
    private TextField txtAge;

    @FXML
    private TextField txtBreed;

    @FXML
    private TextArea txtDescription;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtPetId;

    @FXML
    private TextField txtPrice;

    @FXML
    private ChoiceBox<String> cmbSex;

    private int idPetCurrent;
    private boolean isNewPet = true;
    private String tempImageUrl;
    private ToggleGroup typeGroup;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtPetId.setDisable(true);
        txtName.setDisable(true);
        txtBreed.setDisable(true);
        txtAge.setDisable(true);
        txtPrice.setDisable(true);
        txtDescription.setDisable(true);

        typeGroup = new ToggleGroup();
        rbtnDog.setToggleGroup(typeGroup);
        rbtnCat.setToggleGroup(typeGroup);
        rbtnDog.setDisable(true);
        rbtnCat.setDisable(true);

        cmbSex.getItems().addAll("Đực", "Cái");
        cmbSex.setValue("Đực");
        cmbSex.setDisable(true);

        setupInputValidation();
        setupButtonActions();
        setupInitialState();
    }
    
    private void setupInputValidation() {
        // Name validation: 2-50 characters, letters and Vietnamese characters only
        txtName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[\\p{L} ]*")) {
                txtName.setText(newValue.replaceAll("[^\\p{L} ]", ""));
            }
            if (newValue.length() > 50) {
                txtName.setText(newValue.substring(0, 50));
            }
        });
        
        // Breed validation: 2-50 characters, letters and Vietnamese characters only
        txtBreed.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[\\p{L} ]*")) {
                txtBreed.setText(newValue.replaceAll("[^\\p{L} ]", ""));
            }
            if (newValue.length() > 50) {
                txtBreed.setText(newValue.substring(0, 50));
            }
        });
        
        // Age validation: numbers only, 0-100
        txtAge.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtAge.setText(oldValue);
            } else if (!newValue.isEmpty()) {
                try {
                    int age = Integer.parseInt(newValue);
                    if (age > 100) {
                        txtAge.setText("100");
                    }
                } catch (NumberFormatException e) {
                    // Ignore parse errors
                }
            }
        });
        
        // Price validation: numbers only, minimum 1000
        txtPrice.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtPrice.setText(oldValue);
            } else if (!newValue.isEmpty()) {
                try {
                    long price = Long.parseLong(newValue);
                    if (price > 1_000_000_000) { // 1 billion VNĐ max
                        txtPrice.setText("1000000000");
                    }
                } catch (NumberFormatException e) {
                    // Ignore parse errors
                }
            }
        });
        
        // Description validation: max 500 characters
        txtDescription.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 500) {
                txtDescription.setText(newValue.substring(0, 500));
            }
        });
    }

    public void setPet(Pet pet) {
        if(pet != null) {
            txtPetId.setText(String.valueOf(pet.getPetId()));
            txtName.setText(pet.getName());
            txtBreed.setText(pet.getBreed());
            txtAge.setText(String.valueOf(pet.getAge()));
            txtPrice.setText(String.valueOf(pet.getPrice()));
            txtDescription.setText(pet.getDescription());

            if ("chó".equals(pet.getType())) {
                rbtnDog.setSelected(true);
            } else if ("mèo".equals(pet.getType())) {
                rbtnCat.setSelected(true);
            }

            String sex = pet.getSex();
            if (sex != null && !sex.isEmpty()) {
                if (cmbSex.getItems().contains(sex)) {
                    cmbSex.setValue(sex);
                } else {
                    cmbSex.setValue("Đực");
                }
            }

            idPetCurrent = pet.getPetId();
            isNewPet = false;

            btnDelete.setDisable(false);
            btnFix.setDisable(false);
            btnSave.setDisable(true);

            loadPetImage(pet);
        }
    }

    private void setupImage() {
        loadDefaultImage();
        btnChange.setOnMouseClicked(event -> handleChangeImage());
    }

    private void setupButtonActions() {
        btnAdd.setOnAction(event -> handleAdd());
        btnDelete.setOnAction(event -> handleDelete());
        btnFix.setOnAction(event -> handleFix());
        btnSave.setOnAction(event -> handleSave());
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
        isNewPet = true;

        btnAdd.setDisable(true);
        btnDelete.setDisable(true);
        btnFix.setDisable(true);
        btnSave.setDisable(false);

        int nextId = getNextPetId();
        txtPetId.setText(String.valueOf(nextId));
        idPetCurrent = nextId;
    }

    private void handleDelete() {
        if(idPetCurrent > 0) {
            if(ControllerUtils.showConfirmationAndWait("Xác nhận", "Bạn có chắc chắn muốn xóa thú cưng này không?")) {
                Pet pet = new Pet();
                pet.setPetId(idPetCurrent);

                int result = PetDAO.delete(pet);
                if(result > 0) {
                    ControllerUtils.showAlert(Alert.AlertType.INFORMATION, "Thành công", "Xóa thú cưng thành công!");
                    clearFields();
                    setupInitialState();
                } else {
                    ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa thú cưng!");
                }
                btnDelete.setDisable(true);
                btnFix.setDisable(true);
                btnSave.setDisable(true);
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

    private void handleSave() {
        if(validateInput()) {
            try {
                if(isNewPet) {
                    Pet pet = new Pet();
                    pet.setPetId(idPetCurrent);
                    pet.setName(txtName.getText());
                    pet.setBreed(txtBreed.getText());
                    pet.setAge(Integer.parseInt(txtAge.getText()));
                    pet.setPrice(Integer.parseInt(txtPrice.getText()));
                    pet.setDescription(txtDescription.getText());

                    if (rbtnDog.isSelected()) {
                        pet.setType("chó");
                    } else if (rbtnCat.isSelected()) {
                        pet.setType("mèo");
                    }

                    pet.setSex(cmbSex.getValue());
                    pet.setIsSold(false);

                    if(tempImageUrl != null && !tempImageUrl.isEmpty()) {
                        pet.setImageUrl(tempImageUrl);
                    }
                    else {
                        pet.setImageUrl("/Images/Pet/pet" + pet.getPetId() + ".jpg");
                    }

                    int newPetId = PetDAO.insert(pet);
                    if(newPetId > 0) {
                        ControllerUtils.showAlert(Alert.AlertType.INFORMATION, "Thành công", "Thêm thú cưng thành công!");
                        disableEditing();
                        btnAdd.setDisable(false);
                        btnDelete.setDisable(false);
                        btnFix.setDisable(false);
                        btnSave.setDisable(true);

                        tempImageUrl = null;
                        isNewPet = false;
                    } else {
                        ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể thêm thú cưng!");
                    }
                } else {
                    Pet pet = PetDAO.findById(idPetCurrent);
                    if(pet != null) {
                        pet.setName(txtName.getText());
                        pet.setBreed(txtBreed.getText());
                        pet.setAge(Integer.parseInt(txtAge.getText()));
                        pet.setPrice(Integer.parseInt(txtPrice.getText()));
                        pet.setDescription(txtDescription.getText());

                        if (rbtnDog.isSelected()) {
                            pet.setType("chó");
                        } else if (rbtnCat.isSelected()) {
                            pet.setType("mèo");
                        }

                        pet.setSex(cmbSex.getValue());

                        int result = PetDAO.update(pet);
                        if(result > 0) {
                            ControllerUtils.showAlert(Alert.AlertType.INFORMATION, "Thành công", "Cập nhật thú cưng thành công!");
                            disableEditing();
                            btnAdd.setDisable(false);
                            btnDelete.setDisable(false);
                            btnFix.setDisable(false);
                            btnSave.setDisable(true);
                        } else {
                            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể cập nhật thú cưng!");
                        }
                    } else {
                        ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không tìm thấy thú cưng!");
                    }
                }
            } catch (NumberFormatException e) {
                ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Tuổi và giá phải là số!");
            }
        }
    }

    private void handleChangeImage() {
        if(idPetCurrent <= 0) {
            ControllerUtils.showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng lưu thông tin thú cưng trước khi thay đổi ảnh!");
            return;
        }

        Pet pet;
        if(isNewPet) {
            pet = new Pet();
            pet.setPetId(idPetCurrent);
            pet.setName(txtName.getText());
            if (rbtnDog.isSelected()) {
                pet.setType("chó");
            } else if (rbtnCat.isSelected()) {
                pet.setType("mèo");
            }
            pet.setBreed(txtBreed.getText());
            if(!txtAge.getText().isEmpty()) {
                pet.setAge(Integer.parseInt(txtAge.getText()));
            }
            if(!txtPrice.getText().isEmpty()) {
                pet.setPrice(Integer.parseInt(txtPrice.getText()));
            }
            pet.setDescription(txtDescription.getText());

            ControllerUtils.showAlert(Alert.AlertType.WARNING, "Cảnh báo",
                "Thú cưng chưa được lưu vào cơ sở dữ liệu. Hình ảnh sẽ được cập nhật khi bạn lưu thú cưng.");
        } else {
            // For existing pets
            pet = PetDAO.findById(idPetCurrent);
            if(pet == null) {
                ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không tìm thấy thông tin thú cưng!");
                return;
            }
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh thú cưng");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(imgPet.getScene().getWindow());
        if (selectedFile != null) {
            try {
                File imagesDir = new File("src/main/resources/Images/Pet");
                if (!imagesDir.exists()) {
                    imagesDir.mkdirs();
                }

                String newFileName = "pet" + pet.getPetId() + ".jpg";
                File newFile = new File(imagesDir, newFileName);

                Files.copy(selectedFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                Image newImage = new Image(newFile.toURI().toString());
                setRectangleImage(newImage);

                String relativePath = "/Images/Pet/" + newFileName;
                pet.setImageUrl(relativePath);

                if(!isNewPet) {
                    PetDAO.update(pet);
                    ControllerUtils.showAlert(Alert.AlertType.INFORMATION, "Thành công", "Cập nhật ảnh thú cưng thành công!");
                } else {
                    tempImageUrl = relativePath;
                    ControllerUtils.showAlert(Alert.AlertType.INFORMATION, "Thành công",
                        "Ảnh thú cưng đã được chọn và sẽ được lưu khi bạn lưu thú cưng.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể cập nhật ảnh: " + e.getMessage());
            }
        }
    }

    private void loadPetImage(Pet pet) {
        try {
            String imageUrl = pet.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
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

        } catch (Exception e) {
            e.printStackTrace();
            loadDefaultImage();
        }
    }

    private void loadDefaultImage() {
        try {
            Image fallbackImage = new Image(getClass().getResourceAsStream("/Images/noImage.png"));
            setRectangleImage(fallbackImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setRectangleImage(Image image) {
        if (image == null) {
            return;
        }
        try{
            Canvas canvas = new Canvas(imgPet.getWidth(), imgPet.getHeight());
            GraphicsContext gc = canvas.getGraphicsContext2D();

            double imageWidth = image.getWidth();
            double imageHeight = image.getHeight();
            double rectWidth = imgPet.getWidth();
            double rectHeight = imgPet.getHeight();

            double scale = Math.max(rectWidth / imageWidth, rectHeight / imageHeight);

            double scaledWidth = imageWidth * scale;
            double scaledHeight = imageHeight * scale;

            double x = (rectWidth - scaledWidth) / 2;
            double y = (rectHeight - scaledHeight) / 2;

            gc.drawImage(image, x, y, scaledWidth, scaledHeight);

            WritableImage snapshot = new WritableImage((int)rectWidth, (int)rectHeight);
            canvas.snapshot(null, snapshot);

            ImagePattern pattern = new ImagePattern(snapshot);
            imgPet.setFill(pattern);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                ImagePattern pattern = new ImagePattern(image);
                imgPet.setFill(pattern);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private int getNextPetId() {
        ArrayList<Pet> pets = PetDAO.findAll();
        if(pets.isEmpty()) {
            return 1;
        }
        return pets.stream()
                .mapToInt(Pet::getPetId)
                .max()
                .orElse(0) + 1;
    }

    private void clearFields() {
        txtPetId.clear();
        txtName.clear();
        txtBreed.clear();
        txtAge.clear();
        txtPrice.clear();
        txtDescription.clear();
        rbtnDog.setSelected(false);
        rbtnCat.setSelected(false);
        cmbSex.setValue("Đực");
        tempImageUrl = null;
        loadDefaultImage();
    }

    private boolean validateInput() {
        // Check required fields
        if (txtName.getText().trim().isEmpty() || 
            txtBreed.getText().trim().isEmpty() || 
            txtAge.getText().isEmpty() || 
            txtPrice.getText().isEmpty() ||
            (!rbtnDog.isSelected() && !rbtnCat.isSelected())) {
            
            ControllerUtils.showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng điền đầy đủ thông tin thú cưng!");
            return false;
        }
        
        // Name validation
        String name = txtName.getText().trim();
        if (name.length() < 2 || name.length() > 50) {
            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Tên thú cưng phải có từ 2 đến 50 ký tự!");
            txtName.requestFocus();
            return false;
        }
        
        // Breed validation
        String breed = txtBreed.getText().trim();
        if (breed.length() < 2 || breed.length() > 50) {
            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Giống thú cưng phải có từ 2 đến 50 ký tự!");
            txtBreed.requestFocus();
            return false;
        }
        
        // Age validation
        try {
            int age = Integer.parseInt(txtAge.getText().trim());
            if (age < 0 || age > 100) {
                ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Tuổi phải từ 0 đến 100!");
                txtAge.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Tuổi phải là số!");
            txtAge.requestFocus();
            return false;
        }
        
        // Price validation
        try {
            long price = Long.parseLong(txtPrice.getText().trim());
            if (price < 1000) {
                ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Giá phải từ 1,000 VNĐ trở lên!");
                txtPrice.requestFocus();
                return false;
            }
            if (price > 1_000_000_000) {
                ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Giá không được vượt quá 1,000,000,000 VNĐ!");
                txtPrice.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Giá không hợp lệ!");
            txtPrice.requestFocus();
            return false;
        }
        
        // Description length check (optional field)
        if (txtDescription.getText().length() > 500) {
            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Mô tả không được vượt quá 500 ký tự!");
            txtDescription.requestFocus();
            return false;
        }
        
        return true;
    }

    private void enableEditing() {
        txtName.setDisable(false);
        txtBreed.setDisable(false);
        txtAge.setDisable(false);
        txtPrice.setDisable(false);
        txtDescription.setDisable(false);
        rbtnDog.setDisable(false);
        rbtnCat.setDisable(false);
        cmbSex.setDisable(false);
        
        // Focus on name field when enabling editing
        Platform.runLater(() -> txtName.requestFocus());
    }

    private void disableEditing() {
        txtName.setDisable(true);
        txtBreed.setDisable(true);
        txtAge.setDisable(true);
        txtPrice.setDisable(true);
        txtDescription.setDisable(true);
        rbtnDog.setDisable(true);
        rbtnCat.setDisable(true);
        cmbSex.setDisable(true);
    }
}

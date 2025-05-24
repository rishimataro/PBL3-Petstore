package com.store.app.petstore.Controllers.Admin;

import com.store.app.petstore.Controllers.ControllerUtils;
import com.store.app.petstore.DAO.PetDAO;
import com.store.app.petstore.Models.Entities.Pet;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
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

    @FXML
    private FontAwesomeIconView closeIcon;

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

        // Set up type radio buttons
        typeGroup = new ToggleGroup();
        rbtnDog.setToggleGroup(typeGroup);
        rbtnCat.setToggleGroup(typeGroup);
        rbtnDog.setDisable(true);
        rbtnCat.setDisable(true);

        // Set up sex choice box
        cmbSex.getItems().addAll("Đực", "Cái");
        cmbSex.setValue("Đực"); // Default value
        cmbSex.setDisable(true);

        setupButtonActions();
        setupInitialState();
    }

    public void setPet(Pet pet) {
        if(pet != null) {
            txtPetId.setText(String.valueOf(pet.getPetId()));
            txtName.setText(pet.getName());
            txtBreed.setText(pet.getBreed());
            txtAge.setText(String.valueOf(pet.getAge()));
            txtPrice.setText(String.valueOf(pet.getPrice()));
            txtDescription.setText(pet.getDescription());

            // Set the type radio button
            if ("chó".equals(pet.getType())) {
                rbtnDog.setSelected(true);
            } else if ("mèo".equals(pet.getType())) {
                rbtnCat.setSelected(true);
            }

            // Set the sex choice box
            String sex = pet.getSex();
            if (sex != null && !sex.isEmpty()) {
                if (cmbSex.getItems().contains(sex)) {
                    cmbSex.setValue(sex);
                } else {
                    // Default to "Đực" if the sex value is not in the choice box
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

        if(closeIcon != null) {
            closeIcon.setOnMouseClicked(event -> closeWindow());
        }
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
        if(txtName.getText().trim().isEmpty() || txtBreed.getText().trim().isEmpty()
                || txtAge.getText().isEmpty() || txtPrice.getText().isEmpty()
                || (!rbtnDog.isSelected() && !rbtnCat.isSelected())
                || cmbSex.getValue() == null) {
            ControllerUtils.showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng điền đầy đủ thông tin thú cưng!");
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

    private void closeWindow() {
        Stage stage = (Stage) petInforPopup.getScene().getWindow();
        if(stage != null) {
            stage.close();
        }
    }
}

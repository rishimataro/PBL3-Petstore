package com.store.app.petstore.Controllers.Staff;

import com.store.app.petstore.Models.Entities.Pet;
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

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class PetInforController implements Initializable {

    @FXML
    private ChoiceBox<String> cmbSex;

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

    private ToggleGroup typeGroup;
    private int idPetCurrent;
    private String tempImageUrl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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

            loadPetImage(pet);
        }
    }

    private void setupImage() {
        loadDefaultImage();
    }

    private void setupInitialState() {
        setupImage();
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
        loadDefaultImage();
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
}

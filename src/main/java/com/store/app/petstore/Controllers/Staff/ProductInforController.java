package com.store.app.petstore.Controllers.Staff;

import com.store.app.petstore.Models.Entities.Product;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class ProductInforController implements Initializable {

    @FXML
    private ChoiceBox<String> cmbCatelogy;

    @FXML
    private Rectangle imgProduct;

    @FXML
    private AnchorPane productInforPopup;

    @FXML
    private TextArea txtDescription;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtPrice;

    @FXML
    private TextField txtProductId;

    @FXML
    private TextField txtQuantity;

    private int idProductCurrent;
    private String tempImageUrl;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtProductId.setDisable(true);
        txtName.setDisable(true);
        cmbCatelogy.setDisable(true);
        txtQuantity.setDisable(true);
        txtPrice.setDisable(true);
        txtDescription.setDisable(true);

        setupInitialState();
        setupChoiceBoxes();
    }

    private void setupChoiceBoxes() {
        cmbCatelogy.getItems().addAll("thức ăn", "đồ chơi", "phụ kiện");
    }

    public void setProduct(Product product) {
        if(product != null) {
            txtProductId.setText(String.valueOf(product.getProductId()));
            txtName.setText(product.getName());
            cmbCatelogy.setValue(product.getCategory());
            txtQuantity.setText(String.valueOf(product.getStock()));
            txtPrice.setText(String.valueOf(product.getPrice()));
            txtDescription.setText(product.getDescription());

            idProductCurrent = product.getProductId();

            loadProductImage(product);
        }
    }

    private void setupImage() {
        loadDefaultImage();
    }

    private void setupInitialState() {
        setupImage();
    }

    private void loadProductImage(Product product) {
        try {
            String imageUrl = product.getImageUrl();
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
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            loadDefaultImage();
        }
    }

    private void loadDefaultImage() {
        try {
            String imagePath = "/Images/Product/product";
            if (idProductCurrent > 0) {
                imagePath += idProductCurrent;
            } else {
                imagePath += "1";
            }
            imagePath += ".jpg";

            Image defaultImage = new Image(getClass().getResourceAsStream(imagePath));
            setRectangleImage(defaultImage);
        } catch (Exception ex) {
            ex.printStackTrace();
            // Fallback to noImage.png if the product image is not found
            try {
                Image fallbackImage = new Image(getClass().getResourceAsStream("/Images/noImage.png"));
                setRectangleImage(fallbackImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setRectangleImage(Image image) {
        if (image == null) {
            return;
        }
        try{
            javafx.scene.canvas.Canvas canvas = new Canvas(imgProduct.getWidth(), imgProduct.getHeight());
            GraphicsContext gc = canvas.getGraphicsContext2D();

            double imageWidth = image.getWidth();
            double imageHeight = image.getHeight();
            double rectWidth = imgProduct.getWidth();
            double rectHeight = imgProduct.getHeight();

            double scale = Math.max(rectWidth / imageWidth, rectHeight / imageHeight);

            double scaledWidth = imageWidth * scale;
            double scaledHeight = imageHeight * scale;

            double x = (rectWidth - scaledWidth) / 2;
            double y = (rectHeight - scaledHeight) / 2;

            gc.drawImage(image, x, y, scaledWidth, scaledHeight);

            WritableImage snapshot = new WritableImage((int)rectWidth, (int)rectHeight);
            canvas.snapshot(null, snapshot);

            ImagePattern pattern = new ImagePattern(snapshot);
            imgProduct.setFill(pattern);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                ImagePattern pattern = new ImagePattern(image);
                imgProduct.setFill(pattern);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

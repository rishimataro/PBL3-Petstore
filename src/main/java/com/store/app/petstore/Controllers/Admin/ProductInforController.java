package com.store.app.petstore.Controllers.Admin;

import com.store.app.petstore.Controllers.ControllerUtils;
import com.store.app.petstore.DAO.ProductDAO;
import com.store.app.petstore.Models.Entities.Product;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ProductInforController implements Initializable {

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
    private Rectangle imgProduct;

    @FXML
    private AnchorPane productInforPopup;

    @FXML
    private ChoiceBox<String> cmbCatelogy;

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

    @FXML
    private FontAwesomeIconView closeIcon;

    private int idProductCurrent;
    private boolean isNewProduct = true;
    private String tempImageUrl;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtProductId.setDisable(true);
        txtName.setDisable(true);
        cmbCatelogy.setDisable(true);
        txtQuantity.setDisable(true);
        txtPrice.setDisable(true);
        txtDescription.setDisable(true);

        setupInputValidation();
        setupButtonActions();
        setupInitialState();
        setupChoiceBoxes();
    }
    
    private void setupInputValidation() {
        // Name validation: 2-100 characters, letters, numbers, spaces, and common punctuation
        txtName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[\\p{L}0-9 .,!?()-]*")) {
                txtName.setText(newValue.replaceAll("[^\\p{L}0-9 .,!?()-]", ""));
            }
            if (newValue.length() > 100) {
                txtName.setText(newValue.substring(0, 100));
            }
        });
        
        // Quantity validation: non-negative integers only, max 10,000
        txtQuantity.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtQuantity.setText(oldValue);
            } else if (!newValue.isEmpty()) {
                try {
                    int quantity = Integer.parseInt(newValue);
                    if (quantity > 10000) {
                        txtQuantity.setText("10000");
                    }
                } catch (NumberFormatException e) {
                    // Ignore parse errors
                }
            }
        });
        
        // Price validation: positive integers only, min 1000, max 1,000,000,000
        txtPrice.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtPrice.setText(oldValue);
            } else if (!newValue.isEmpty()) {
                try {
                    long price = Long.parseLong(newValue);
                    if (price > 1_000_000_000) {
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
            isNewProduct = false;

            btnDelete.setDisable(false);
            btnFix.setDisable(false);
            btnSave.setDisable(true);

            loadProductImage(product);
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
        isNewProduct = true;

        btnAdd.setDisable(true);
        btnDelete.setDisable(true);
        btnFix.setDisable(true);
        btnSave.setDisable(false);

        int nextId = getNextProductId();
        txtProductId.setText(String.valueOf(nextId));
        idProductCurrent = nextId;
    }

    private void handleDelete() {
        if(idProductCurrent > 0) {
            if(ControllerUtils.showConfirmationAndWait("Xác nhận", "Bạn có chắc chắn muốn xóa sản phẩm này không?")) {
                Product product = new Product();
                product.setProductId(idProductCurrent);

                int result = ProductDAO.delete(product);
                if(result > 0) {
                    ControllerUtils.showAlert(Alert.AlertType.INFORMATION, "Thành công", "Xóa sản phẩm thành công!");
                    clearFields();
                    setupInitialState();
                } else {
                    ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa sản phẩm!");
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
            if(isNewProduct) {
                Product product = new Product();
                product.setProductId(idProductCurrent);
                product.setName(txtName.getText());
                product.setCategory(cmbCatelogy.getValue());
                product.setStock(Integer.parseInt(txtQuantity.getText()));
                product.setPrice(Integer.parseInt(txtPrice.getText()));
                product.setDescription(txtDescription.getText());

                // If we have a temporary image URL from the change image function, use it
                if(tempImageUrl != null && !tempImageUrl.isEmpty()) {
                    product.setImageUrl(tempImageUrl);
                }
                // Otherwise, use the standard product image path format
                else {
                    product.setImageUrl("/Images/Product/product" + product.getProductId() + ".jpg");
                }

                int result = ProductDAO.insert(product);
                if(result > 0) {
                    ControllerUtils.showAlert(Alert.AlertType.INFORMATION, "Thành công", "Thêm sản phẩm thành công!");
                    disableEditing();
                    btnAdd.setDisable(false);
                    btnDelete.setDisable(false);
                    btnFix.setDisable(false);
                    btnSave.setDisable(true);

                    tempImageUrl = null;
                    isNewProduct = false;
                } else {
                    ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể thêm sản phẩm!");
                }
            } else {
                Product product = ProductDAO.findById(idProductCurrent);
                if(product != null) {
                    product.setName(txtName.getText());
                    product.setCategory(cmbCatelogy.getValue());
                    product.setStock(Integer.parseInt(txtQuantity.getText()));
                    product.setPrice(Integer.parseInt(txtPrice.getText()));
                    product.setDescription(txtDescription.getText());

                    int result = ProductDAO.update(product);
                    if(result > 0) {
                        ControllerUtils.showAlert(Alert.AlertType.INFORMATION, "Thành công", "Cập nhật sản phẩm thành công!");
                        disableEditing();
                        btnAdd.setDisable(false);
                        btnDelete.setDisable(false);
                        btnFix.setDisable(false);
                        btnSave.setDisable(true);
                    } else {
                        ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể cập nhật sản phẩm!");
                    }
                } else {
                    ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không tìm thấy sản phẩm!");
                }
            }
        }
    }

    private void handleChangeImage(){
        if(idProductCurrent <= 0) {
            ControllerUtils.showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng lưu thông tin sản phẩm trước khi thay đổi ảnh!");
            return;
        }

        Product product;
        if(isNewProduct) {
            product = new Product();
            product.setProductId(idProductCurrent);
            product.setName(txtName.getText());
            product.setCategory(cmbCatelogy.getValue());
            if(!txtQuantity.getText().isEmpty()) {
                product.setStock(Integer.parseInt(txtQuantity.getText()));
            }
            if(!txtPrice.getText().isEmpty()) {
                product.setPrice(Integer.parseInt(txtPrice.getText()));
            }
            product.setDescription(txtDescription.getText());
        } else {
            // For existing products
            product = ProductDAO.findById(idProductCurrent);
            if(product == null) {
                ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không tìm thấy thông tin sản phẩm!");
                return;
            }
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh đại diện");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(imgProduct.getScene().getWindow());
        if (selectedFile != null) {
            try {
                File imagesDir = new File("src/main/resources/Images/Product");
                if (!imagesDir.exists()) {
                    imagesDir.mkdirs();
                }

                String newFileName = "product" + product.getProductId() + ".jpg";
                File newFile = new File(imagesDir, newFileName);

                Files.copy(selectedFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                Image newImage = new Image(newFile.toURI().toString());
                setRectangleImage(newImage);

                String relativePath = "/Images/Product/" + newFileName;
                product.setImageUrl(relativePath);

                if(!isNewProduct) {
                    ProductDAO.update(product);
                    ControllerUtils.showAlert(Alert.AlertType.INFORMATION, "Thành công", "Cập nhật ảnh đại diện thành công!");
                } else {
                    tempImageUrl = relativePath;
                    ControllerUtils.showAlert(Alert.AlertType.INFORMATION, "Thành công",
                        "Ảnh đại diện đã được chọn và sẽ được lưu khi bạn lưu sản phẩm.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể cập nhật ảnh: " + e.getMessage());
            }
        }
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
            String imagePath = "/Images/noImage.png";

            Image defaultImage = new Image(getClass().getResourceAsStream(imagePath));
            setRectangleImage(defaultImage);
        } catch (Exception ex) {
            ex.printStackTrace();
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

    private int getNextProductId() {
        ArrayList<Product> products = ProductDAO.findAll();
        if(products.isEmpty()) {
            return 1;
        }
        return products.stream()
                .mapToInt(Product::getProductId)
                .max()
                .orElse(0) + 1;
    }

    private void clearFields() {
        txtProductId.clear();
        txtName.clear();
        cmbCatelogy.setValue(null);
        txtQuantity.clear();
        txtPrice.clear();
        txtDescription.clear();
        tempImageUrl = null;
        loadDefaultImage();
    }

    private boolean validateInput() {
        // Check required fields
        if (txtName.getText().trim().isEmpty() || 
            cmbCatelogy.getValue() == null ||
            txtQuantity.getText().isEmpty() || 
            txtPrice.getText().isEmpty()) {
            
            ControllerUtils.showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng điền đầy đủ thông tin sản phẩm!");
            return false;
        }
        
        // Name validation
        String name = txtName.getText().trim();
        if (name.length() < 2 || name.length() > 100) {
            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Tên sản phẩm phải có từ 2 đến 100 ký tự!");
            txtName.requestFocus();
            return false;
        }
        
        // Quantity validation
        try {
            int quantity = Integer.parseInt(txtQuantity.getText().trim());
            if (quantity < 0) {
                ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Số lượng không được âm!");
                txtQuantity.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Số lượng không hợp lệ!");
            txtQuantity.requestFocus();
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
        cmbCatelogy.setDisable(false);
        txtQuantity.setDisable(false);
        txtPrice.setDisable(false);
        txtDescription.setDisable(false);
        
        // Focus on name field when enabling editing
        Platform.runLater(() -> txtName.requestFocus());
    }

    private void disableEditing() {
        txtName.setDisable(true);
        cmbCatelogy.setDisable(true);
        txtQuantity.setDisable(true);
        txtPrice.setDisable(true);
        txtDescription.setDisable(true);
    }

}

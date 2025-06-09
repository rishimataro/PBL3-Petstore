package com.store.app.petstore.Controllers.Staff;

import com.store.app.petstore.Controllers.ControllerUtils;
import com.store.app.petstore.Models.Entities.Item;
import com.store.app.petstore.Models.Entities.Pet;
import com.store.app.petstore.Models.Entities.Product;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.InputStream;
import java.util.function.Consumer;

public class ItemList2Controller {
    @FXML
    private FontAwesomeIconView downIcon;
    @FXML
    private ImageView itemImage;
    @FXML
    private Label itemName;
    @FXML
    private Label quantity;
    @FXML
    private Label total;
    @FXML
    private Label stockLabel;
    @FXML
    private FontAwesomeIconView trashIcon;
    @FXML
    private Label unitPrice;
    @FXML
    private FontAwesomeIconView upIcon;
    @FXML
    private Item item;

    private int initialStock;
    private static final String DEFAULT_IMAGE_PATH = "/Images/noImage.png";

    private Runnable onQuantityChanged;
    private Runnable onStockChanged;
    private Consumer<AnchorPane> onDeleteCallback;
    private AnchorPane parentPane;

    public Item getItem() {
        return item;
    }

    public void setData(Item item) {
        this.item = item;
        itemName.setText(item.getName());
        unitPrice.setText(ControllerUtils.formatCurrency(item.getPrice()));
        quantity.setText("1");
        updateTotal();

        loadItemImage(item.getImageUrl());

        if (item instanceof Product product) {
            initialStock = product.getStock();
            stockLabel.setText("Còn lại: " + (product.getStock() - 1));
        } else {
            stockLabel.setVisible(false);
            upIcon.setVisible(false);
            downIcon.setVisible(false);
            quantity.setVisible(false);
        }
    }

    private void loadItemImage(String imagePath) {
        try {
            InputStream imageStream = getClass().getResourceAsStream(imagePath);
            if (imageStream != null) {
                Image img = new Image(imageStream);
                processImageForSquareView(img);
            } else {
                loadDefaultImage();
            }
        } catch (Exception e) {
            System.err.println("Error loading image '" + imagePath + "': " + e.getMessage());
            loadDefaultImage();
        }
    }

    private void processImageForSquareView(Image image) {
        if (image == null) {
            loadDefaultImage();
            return;
        }

        itemImage.setImage(image);
        double width = image.getWidth();
        double height = image.getHeight();

        double size;
        double x = 0;
        double y = 0;

        if (width > height) {
            size = height;
            x = (width - height) / 2;
            y = 0;
        } else if (height > width) {
            size = width;
            x = 0;
            y = (height - width) / 2;
        } else {
            size = width;
            x = 0;
            y = 0;
        }

        itemImage.setViewport(new Rectangle2D(x, y, size, size));

        itemImage.setPreserveRatio(true);
        itemImage.setSmooth(true);
        itemImage.setCache(true);

        itemImage.setFitWidth(itemImage.getFitWidth());
        itemImage.setFitHeight(itemImage.getFitHeight());
    }

    private void loadDefaultImage() {
        try {
            InputStream defaultImageStream = getClass().getResourceAsStream(DEFAULT_IMAGE_PATH);
            if (defaultImageStream != null) {
                Image defaultImg = new Image(defaultImageStream);
                processImageForSquareView(defaultImg);
            } else {
                System.err.println("Default image not found at: " + DEFAULT_IMAGE_PATH);
            }
        } catch (Exception ex) {
            System.err.println("Error loading default image: " + ex.getMessage());
        }
    }

    public double getTotal() {
        String totalText = total.getText();
        return ControllerUtils.parseCurrency(totalText.isEmpty() ? "0" : totalText);
    }

    public int getQuantity() {
        String quantityText = quantity.getText();
        return Integer.parseInt(quantityText.isEmpty() ? "0" : quantityText);
    }

    public void addItem(int amount) {
        int currentAmount = getQuantity();
        int newAmount = currentAmount + amount;

        if (newAmount < 0) {
            newAmount = 0;
        }

        if (this.item instanceof Pet && newAmount > 1) {
            newAmount = 1;
        }

        quantity.setText(String.valueOf(newAmount));
        updateTotal();
    }

    public void setOnStockChanged(Runnable callback) {
        this.onStockChanged = callback;
    }

    public void setOnQuantityChanged(Runnable callback) {
        this.onQuantityChanged = callback;
    }

    private void updateTotal() {
        double price = ControllerUtils.parseCurrency(unitPrice.getText());
        int quantityValue = getQuantity();
        double totalValue = price * quantityValue;
        total.setText(ControllerUtils.formatCurrency(totalValue));

        if (onQuantityChanged != null) {
            onQuantityChanged.run();
        }
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        if (onDeleteCallback != null && parentPane != null) {
            onDeleteCallback.accept(parentPane);
        }
    }

    public void setOnDeleteCallback(Consumer<AnchorPane> callback) {
        this.onDeleteCallback = callback;
    }

    public void setParentPane(AnchorPane pane) {
        this.parentPane = pane;
    }

    @FXML
    private void handleUpIcon() {
        int currentAmount = getQuantity();

        if (item instanceof Product) {
            if (currentAmount < initialStock) {
                quantity.setText(String.valueOf(currentAmount + 1));
                updateTotal();
                stockLabel.setText("Còn lại: " + (initialStock - (currentAmount + 1)));

                if (onStockChanged != null) {
                    onStockChanged.run();
                }
            }
        } else {
            ControllerUtils.showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Không thể tăng số lượng thú cưng!");
        }
    }

    @FXML
    private void handleDownIcon() {
        if (item instanceof Product) {
            if (initialStock == 0) {
                ControllerUtils.showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Sản phẩm đã hết hàng!");
                return;
            }

            int currentAmount = getQuantity();
            if (currentAmount > 1) {
                quantity.setText(String.valueOf(currentAmount - 1));
                updateTotal();
                stockLabel.setText("Còn lại: " + (initialStock - (currentAmount - 1)));

                if (onStockChanged != null) {
                    onStockChanged.run();
                }
            }
        } else {
            ControllerUtils.showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Không thể giảm số lượng thú cưng!");
        }
    }
}

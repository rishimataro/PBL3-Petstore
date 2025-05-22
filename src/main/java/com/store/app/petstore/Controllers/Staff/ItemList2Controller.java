package com.store.app.petstore.Controllers.Staff;

import com.store.app.petstore.Models.Entities.Item;
import com.store.app.petstore.Models.Entities.Pet;
import com.store.app.petstore.Models.Entities.Product;
import com.store.app.petstore.Controllers.ControllerUtils;
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
import java.util.Objects;
import java.util.function.Consumer;

public class ItemList2Controller {
    // UI Components
    @FXML private FontAwesomeIconView downIcon;
    @FXML private ImageView itemImage;
    @FXML private Label itemName;
    @FXML private Label quantity;
    @FXML private Label total;
    @FXML private Label stockLabel;
    @FXML private FontAwesomeIconView trashIcon;
    @FXML private Label unitPrice;
    @FXML private FontAwesomeIconView upIcon;

    // Data
    @FXML private Item item;
    private int initialStock;

    // Callbacks
    private Runnable onQuantityChanged;
    private Runnable onStockChanged;
    private Consumer<AnchorPane> onDeleteCallback;
    private AnchorPane parentPane;

    // Default image path
    private static final String DEFAULT_IMAGE_PATH = "/images/logo.png";

    // No initialization needed

    /**
     * Sets the item data and updates the UI
     * @param item The item to display
     */
    public void setData(Item item) {
        this.item = item;
        itemName.setText(item.getName());
        unitPrice.setText(ControllerUtils.formatCurrency(item.getPrice()));
        quantity.setText("1");
        updateTotal();

        // Load the item image with proper processing
        loadItemImage(item.getImageUrl());

        if (item instanceof Product product) {
            initialStock = product.getStock();
            stockLabel.setText("Còn lại: " + (product.getStock() - 1));
        } else {
            // For pets, hide quantity controls
            stockLabel.setVisible(false);
            upIcon.setVisible(false);
            downIcon.setVisible(false);
            quantity.setVisible(false);
        }
    }

    /**
     * Loads an image from the given path and sets it to the item image view.
     * Processes the image to fill a square, cropping if necessary, without squeezing.
     * Falls back to a default image if the specified image cannot be loaded.
     *
     * @param imagePath The path to the image resource
     */
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

    /**
     * Processes the image to fit a square view by cropping (if necessary) without squeezing.
     * This method implements the "fill square, can crop image, do not squeeze image" requirement.
     *
     * @param image The original image to process
     */
    private void processImageForSquareView(Image image) {
        if (image == null) {
            loadDefaultImage();
            return;
        }

        // Set the image to the ImageView
        itemImage.setImage(image);

        // Get image dimensions
        double width = image.getWidth();
        double height = image.getHeight();

        // Determine the crop dimensions to make it square
        double size;
        double x = 0;
        double y = 0;

        if (width > height) {
            // Landscape image: crop the sides
            size = height;
            x = (width - height) / 2;
            y = 0;
        } else if (height > width) {
            // Portrait image: crop the top and bottom
            size = width;
            x = 0;
            y = (height - width) / 2;
        } else {
            // Already square
            size = width; // or height, they're the same
            x = 0;
            y = 0;
        }

        // Apply the viewport to crop the image
        itemImage.setViewport(new Rectangle2D(x, y, size, size));

        // Ensure the ImageView preserves the ratio and doesn't squeeze the image
        itemImage.setPreserveRatio(true);
        itemImage.setSmooth(true);
        itemImage.setCache(true);

        // Make sure the ImageView fills its allocated space
        itemImage.setFitWidth(itemImage.getFitWidth());
        itemImage.setFitHeight(itemImage.getFitHeight());
    }

    /**
     * Loads the default image when the item image cannot be loaded
     */
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

    /**
     * Gets the current item
     * @return The current item
     */
    public Item getItem() {
        return item;
    }

    /**
     * Gets the total price for this item
     * @return The total price
     */
    public double getTotal() {
        String totalText = total.getText();
        return ControllerUtils.parseCurrency(totalText.isEmpty() ? "0" : totalText);
    }

    /**
     * Gets the current quantity
     * @return The current quantity
     */
    public int getQuantity() {
        String quantityText = quantity.getText();
        return Integer.parseInt(quantityText.isEmpty() ? "0" : quantityText);
    }

    /**
     * Adds the specified amount to the current quantity
     * @param amount The amount to add
     */
    public void addItem(int amount) {
        int currentAmount = getQuantity();
        int newAmount = currentAmount + amount;

        // Ensure quantity is not negative
        if (newAmount < 0) {
            newAmount = 0;
        }

        // Limit pets to quantity of 1
        if (this.item instanceof Pet && newAmount > 1) {
            newAmount = 1;
        }

        quantity.setText(String.valueOf(newAmount));
        updateTotal();
    }

    /**
     * Sets the callback to be called when stock changes
     * @param callback The callback to call
     */
    public void setOnStockChanged(Runnable callback) {
        this.onStockChanged = callback;
    }

    /**
     * Sets the callback to be called when quantity changes
     * @param callback The callback to call
     */
    public void setOnQuantityChanged(Runnable callback) {
        this.onQuantityChanged = callback;
    }

    /**
     * Updates the total price based on the current quantity
     */
    private void updateTotal() {
        double price = ControllerUtils.parseCurrency(unitPrice.getText());
        int quantityValue = getQuantity();
        double totalValue = price * quantityValue;
        total.setText(ControllerUtils.formatCurrency(totalValue));

        if (onQuantityChanged != null) {
            onQuantityChanged.run();
        }
    }

    /**
     * Handles the delete button click
     * @param event The action event
     */
    @FXML
    private void handleDelete(ActionEvent event) {
        if (onDeleteCallback != null && parentPane != null) {
            onDeleteCallback.accept(parentPane);
        }
    }

    /**
     * Sets the callback to be called when the delete button is clicked
     * @param callback The callback to call
     */
    public void setOnDeleteCallback(Consumer<AnchorPane> callback) {
        this.onDeleteCallback = callback;
    }

    /**
     * Sets the parent pane for this controller
     * @param pane The parent pane
     */
    public void setParentPane(AnchorPane pane) {
        this.parentPane = pane;
    }

    /**
     * Handles the up icon click (increase quantity)
     */
    @FXML
    private void handleUpIcon() {
        int currentAmount = getQuantity();

        if (item instanceof Product product) {
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

    /**
     * Handles the down icon click (decrease quantity)
     */
    @FXML
    private void handleDownIcon() {
        if (item instanceof Product product) {
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

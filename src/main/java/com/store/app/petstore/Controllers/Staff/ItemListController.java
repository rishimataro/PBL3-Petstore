package com.store.app.petstore.Controllers.Staff;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import com.store.app.petstore.Models.Entities.Item;
import com.store.app.petstore.Models.Entities.Pet;
import com.store.app.petstore.Models.Entities.Product;
import com.store.app.petstore.Controllers.ControllerUtils;

import java.io.InputStream;

/**
 * Controller for the item list view that displays individual items (pets or products)
 * in a grid layout for the staff interface.
 */
public class ItemListController {

    // UI Components - Icons
    @FXML private FontAwesomeIconView addIcon;
    @FXML private FontAwesomeIconView closeIcon;

    // UI Components - Item Details
    @FXML private ImageView itemImage;
    @FXML private Label itemName;
    @FXML private Label itemPrice;
    @FXML private Separator sep;
    @FXML private GridPane tagPane;

    // UI Components - Tags
    @FXML private Label itemType;
    @FXML private Label itemBreed;
    @FXML private Label itemGender;

    // Data
    @FXML private Item item;

    // Default image path
    private static final String DEFAULT_IMAGE_PATH = "/images/logo.png";

    /**
     * Sets the data for this item view
     *
     * @param item The item (Pet or Product) to display
     */
    public void setData(Item item) {
        this.item = item;

        // Set basic item information
        itemName.setText(item.getName());
        itemPrice.setText(ControllerUtils.formatCurrency(item.getPrice()));
        itemType.setText(item.getType());

        // Set specific information based on item type
        if (item instanceof Pet pet) {
            itemBreed.setText(pet.getBreed());
            itemGender.setText(pet.getSex());
        } else if (item instanceof Product product) {
            itemBreed.setText(product.getCategory());
            itemGender.setText(""); // Products don't have gender
        }

        // Load the item image
        loadItemImage(item.getImageUrl());
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
     * Handles the close icon click event
     * This method can be implemented to handle closing or removing the item from view
     */
    @FXML
    private void handleClose() {
        // This method can be implemented if needed
        // Currently not used but kept for future implementation
    }

    /**
     * Gets the item displayed in this view
     *
     * @return The item object
     */
    public Item getItem() {
        return item;
    }
}

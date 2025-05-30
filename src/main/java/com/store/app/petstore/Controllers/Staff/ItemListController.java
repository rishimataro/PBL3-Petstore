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

    @FXML
    private FontAwesomeIconView addIcon;
    @FXML
    private FontAwesomeIconView closeIcon;
    @FXML
    private ImageView itemImage;
    @FXML
    private Label itemName;
    @FXML
    private Label itemPrice;
    @FXML
    private Separator sep;
    @FXML
    private GridPane tagPane;
    @FXML
    private Label itemType;
    @FXML
    private Label itemBreed;
    @FXML
    private Label itemGender;
    @FXML private Item item;

    private static final String DEFAULT_IMAGE_PATH = "/images/logo.png";

    public Item getItem() {
        return item;
    }

    public void setData(Item item) {
        this.item = item;

        itemName.setText(item.getName());
        itemPrice.setText(ControllerUtils.formatCurrency(item.getPrice()));
        itemType.setText(item.getType());

        if (item instanceof Pet pet) {
            itemBreed.setText(pet.getBreed());
            itemGender.setText(pet.getSex());
        } else if (item instanceof Product product) {
            itemBreed.setText(product.getCategory());
            itemGender.setText("");
        }

        loadItemImage(item.getImageUrl());
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
}

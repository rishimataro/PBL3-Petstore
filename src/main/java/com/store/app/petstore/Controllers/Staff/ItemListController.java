package com.store.app.petstore.Controllers.Staff;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import com.store.app.petstore.Models.Entities.Item;
import com.store.app.petstore.Models.Entities.Pet;
import com.store.app.petstore.Models.Entities.Product;
import com.store.app.petstore.Controllers.ControllerUtils;

public class ItemListController {

    @FXML
    private FontAwesomeIconView addIcon;

    @FXML
    private FontAwesomeIconView closeIcon;

    @FXML
    private ImageView imgPet;

    @FXML
    private Label namePet;

    @FXML
    private Label pricePet;

    @FXML
    private Separator sep;

    @FXML
    private Label tagBreed;

    @FXML
    private GridPane tagPane;

    @FXML
    private Label tagGender;

    @FXML
    private Label tagType;

    @FXML
    private Item item;

    public void setData(Item item) {
        this.item = item;
        namePet.setText(item.getName());
        pricePet.setText(ControllerUtils.formatCurrency(item.getPrice()));
        tagType.setText(item.getType());

        if (item instanceof Pet pet) {
            tagBreed.setText(pet.getBreed());
            tagGender.setText(pet.getSex());
        } else if (item instanceof Product product) {
            tagBreed.setText(product.getCategory());
            tagGender.setText(product.getStock() + "");
        }

        try {
            var imageStream = getClass().getResourceAsStream(item.getImageUrl());
            if (imageStream != null) {
                Image img = new Image(imageStream);
                imgPet.setImage(img);
            } else {
                Image defaultImg = new Image(getClass().getResourceAsStream("/images/logo.png"));
                imgPet.setImage(defaultImg);
            }
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
            try {
                Image defaultImg = new Image(getClass().getResourceAsStream("/images/logo.png"));
                imgPet.setImage(defaultImg);
            } catch (Exception ex) {
                System.err.println("Error loading default image: " + ex.getMessage());
            }
        }
    }

    @FXML
    private void handleClose() {

    }
}

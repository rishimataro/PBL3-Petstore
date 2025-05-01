package com.store.app.petstore.Controllers.Staff;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import com.store.app.petstore.Models.Entities.Pet;

import java.util.Objects;

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
    private Pet p;

    public void setData(Pet p) {
        this.p = p;
        namePet.setText(p.getName());
        pricePet.setText(p.getPrice() + "");
        tagBreed.setText(p.getBreed());
        tagGender.setText(p.getSex());
        tagType.setText(p.getType());

        Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(p.getImageUrl())));
        imgPet.setImage(img);
    }

    @FXML
    private void handleClose() {
        // This method will be called when the close button is clicked
        // It will close the parent tab
        // This requires a reference to the parent Tab
        // For simplicity, we'll assume the parent is a TabPane and we'll find the parent Tab
        // This is a simplified approach and may need to be adjusted based on the actual structure
        // In a real app, you'd use a more robust way to find the parent Tab
        // For now, we'll just log the action
        System.out.println("Close button clicked. Closing tab.");
    }
}

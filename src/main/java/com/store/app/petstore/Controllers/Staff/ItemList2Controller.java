package com.store.app.petstore.Controllers.Staff;

import com.store.app.petstore.Models.Entities.Pet;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class ItemList2Controller {

    @FXML
    private FontAwesomeIconView downIcon;

    @FXML
    private ImageView imgPet;

    @FXML
    private Label namePet;

    @FXML
    private Label quality;

    @FXML
    private Label total;

    @FXML
    private FontAwesomeIconView trashIcon;

    @FXML
    private Label unitPrice;

    @FXML
    private FontAwesomeIconView upIcon;

    @FXML
    private Pet p;

    public void setData(Pet p) {
        this.p = p;
        namePet.setText(p.getName());
        unitPrice.setText(p.getPrice() + "");
        quality.setText("00");
        total.setText("00");

        Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(p.getImageUrl())));
        imgPet.setImage(img);
    }
}

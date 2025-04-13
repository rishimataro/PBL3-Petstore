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
        tagGender.setText(p.getGender());
        tagType.setText(p.getType());

        Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(p.getImageUrl())));
        imgPet.setImage(img);
    }
}

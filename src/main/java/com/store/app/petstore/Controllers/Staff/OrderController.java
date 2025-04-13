package com.store.app.petstore.Controllers.Staff;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;

import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

import com.store.app.petstore.Models.Entities.Pet;

public class OrderController implements Initializable {
    @FXML
    private GridPane grid;

    @FXML
    private ScrollPane scrollPane;

    private ArrayList<Pet> pets = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int row = 0;
        int col = 0;
        ArrayList<Pet> pets = getPets();

        for (Pet pet : pets) {
            try {

                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/FXML/Staff/ItemList.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                ItemListController itemController = fxmlLoader.getController();

                if (pet != null) {
                    itemController.setData(pet);
                }

                if (col == 4) {
                    row++;
                    col = 0;
                }

                grid.add(anchorPane, col++, row);
                GridPane.setMargin(anchorPane, new Insets(10));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private ArrayList<Pet> getPets() {
        ArrayList<Pet> pets = new ArrayList<>();
        Pet p;

        for(int i = 0; i < 20; i++) {
            p = new Pet();
            p.setName("LUCKY");
            p.setType("Chó");
            p.setBreed("Shiba");
            p.setImageUrl("/Images/dog.png");
            pets.add(p);
        }

        return pets;
    }

}

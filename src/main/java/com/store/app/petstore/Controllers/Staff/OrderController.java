package com.store.app.petstore.Controllers.Staff;

import com.store.app.petstore.DAO.PetDAO;
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
import javafx.scene.layout.Region;
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
    private final GridPane grid = new GridPane();

    @FXML
    private ScrollPane scrollPane;

    private final ArrayList<Pet> pets = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int row = 0;
        int col = 0;
        pets.addAll(getPets());

        for (int i = 0; i < pets.size(); i++) {
            try {

                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/FXML/Staff/ItemList.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                ItemListController itemController = fxmlLoader.getController();

                if (pets.get(i) != null) {
                    itemController.setData(pets.get(i));
                }

                if (col == 4) {
                    row++;
                    col = 0;
                }

                grid.add(anchorPane, col++, row);
                GridPane.setMargin(anchorPane, new Insets(7));

            } catch (IOException e) {
                e.printStackTrace();
            }

            grid.setMinWidth(Region.USE_COMPUTED_SIZE);
            grid.setPrefWidth(Region.USE_COMPUTED_SIZE);
            grid.setMaxWidth(Double.MAX_VALUE);

            grid.setMinHeight(Region.USE_COMPUTED_SIZE);
            grid.setPrefHeight(Region.USE_COMPUTED_SIZE);
            grid.setMaxHeight(Double.MAX_VALUE);

            scrollPane.setContent(grid);
        }
    }

    private ArrayList<Pet> getPets() {
        PetDAO petDAO = PetDAO.getInstance();
        return new ArrayList<>(petDAO.findAll());
    }
}

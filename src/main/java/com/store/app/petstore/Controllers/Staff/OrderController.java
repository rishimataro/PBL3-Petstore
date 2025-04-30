// #Ai
package com.store.app.petstore.Controllers.Staff;

import com.store.app.petstore.Controllers.Staff.ItemListController;
import com.store.app.petstore.DAO.PetDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.store.app.petstore.Models.Entities.Pet;

public class OrderController implements Initializable {
    @FXML
    private TabPane tabPane;

    private final GridPane grid = new GridPane();
    private final ArrayList<Pet> pets = new ArrayList<>();

    @FXML
    private ScrollPane scrollPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int row = 0;
        int col = 0;
        pets.addAll(getPets());

        for (int i = 0; i < pets.size(); i++) {
            final int index = i; // Make index final for use in lambda
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

                // Add click handler to the pet item
                // Get current Tab AI?
                anchorPane.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        Pet selectedPet = pets.get(index);

                        // Create a new tab
                        Tab newTab = new Tab("Đơn hàng " + (tabPane.getTabs().size() + 1));
                        FXMLLoader tabLoader = new FXMLLoader(getClass().getResource("/FXML/Staff/ItemList.fxml"));
                        AnchorPane tabContent = null;
                        try {
                            tabContent = tabLoader.load();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        ItemListController tabController = tabLoader.getController();
                        tabController.setData(selectedPet);

                        newTab.setContent(tabContent);
                        tabPane.getTabs().add(newTab);
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        grid.setMinWidth(Region.USE_COMPUTED_SIZE);
        grid.setPrefWidth(Region.USE_COMPUTED_SIZE);
        grid.setMaxWidth(Double.MAX_VALUE);

        grid.setMinHeight(Region.USE_COMPUTED_SIZE);
        grid.setPrefHeight(Region.USE_COMPUTED_SIZE);
        grid.setMaxHeight(Double.MAX_VALUE);

        scrollPane.setContent(grid);
    }

    private ArrayList<Pet> getPets() {
        PetDAO petDAO = PetDAO.getInstance();
        return new ArrayList<>(petDAO.findAll());
    }
}

package com.store.app.petstore.Controllers.Staff;

import com.store.app.petstore.Controllers.Staff.ItemListController;
import com.store.app.petstore.DAO.PetDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
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
        pets.addAll(getPets());

        setupGrid();
        setupScrollPane();
    }

    private void setupGrid() {
        int row = 0;
        int col = 0;

        for (int i = 0; i < pets.size(); i++) {
            final int index = i;
            try {
                AnchorPane itemPane = loadItemView(pets.get(i));
                itemPane.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        createNewTab(pets.get(index));
                    }
                });

                if (col == 4) {
                    row++;
                    col = 0;
                }

                grid.add(itemPane, col++, row);
                GridPane.setMargin(itemPane, new Insets(7));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setupScrollPane() {
        grid.setMinWidth(Region.USE_COMPUTED_SIZE);
        grid.setPrefWidth(Region.USE_COMPUTED_SIZE);
        grid.setMaxWidth(Double.MAX_VALUE);

        grid.setMinHeight(Region.USE_COMPUTED_SIZE);
        grid.setPrefHeight(Region.USE_COMPUTED_SIZE);
        grid.setMaxHeight(Double.MAX_VALUE);

        scrollPane.setContent(grid);
    }

    private AnchorPane loadItemView(Pet pet) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Staff/ItemList.fxml"));
        AnchorPane pane = loader.load();
        ItemListController controller = loader.getController();
        controller.setData(pet);
        return pane;
    }

    private void createNewTab(Pet pet) {
        Tab newTab = new Tab("Đơn hàng " + (tabPane.getTabs().size() + 1));
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Staff/ItemList.fxml"));
            AnchorPane content = loader.load();
            ItemListController controller = loader.getController();
            controller.setData(pet);
            newTab.setContent(content);
            tabPane.getTabs().add(newTab);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load tab content", e);
        }
    }

    private ArrayList<Pet> getPets() {
        PetDAO petDAO = PetDAO.getInstance();
        return new ArrayList<>(petDAO.findAll());
    }
}

package com.store.app.petstore.Controllers.Staff;

import com.store.app.petstore.DAO.PetDAO;
import com.store.app.petstore.Models.Entities.Pet;
import com.store.app.petstore.Controllers.TabManager; // Assuming TabManager is in this package or imported
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class OrderController implements Initializable {

    @FXML private TabPane tabPane;
    @FXML private ScrollPane scrollPane;
    @FXML private Button createNewTabButton;

    private final GridPane grid = new GridPane();
    private final List<Pet> petList = new ArrayList<>();
    private TabManager tabManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tabManager = new TabManager(tabPane);
        loadPetList();
        configureGridLayout();
        populateGridWithPets();
        configureScrollPane();
    }

    private void loadPetList() {
        petList.addAll(PetDAO.getInstance().findAll());
    }

    private void configureScrollPane() {
        grid.setMinSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        grid.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        grid.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        scrollPane.setContent(grid);
    }

    private void populateGridWithPets() {
        int column = 0;
        int row = 0;

        for (int i = 0; i < petList.size(); i++) {
            Pet pet = petList.get(i);
            try {
                AnchorPane petPane = createPetItemPane(pet);

                int petIndex = i; // used for lambda capture
                petPane.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        tabManager.openTab("pet_" + pet.getId());
                    }
                });

                if (column == 4) {
                    column = 0;
                    row++;
                }

                grid.add(petPane, column++, row);
                GridPane.setMargin(petPane, new Insets(7));
            } catch (IOException e) {
                System.err.println("Failed to load pet item view: " + e.getMessage());
            }
        }
    }

    private AnchorPane createPetItemPane(Pet pet) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Staff/ItemList.fxml"));
        AnchorPane pane = loader.load();
        ItemListController controller = loader.getController();
        controller.setData(pet);
        return pane;
    }

    private void configureGridLayout() {
        grid.setHgap(0);
        grid.setVgap(0);
    }
}

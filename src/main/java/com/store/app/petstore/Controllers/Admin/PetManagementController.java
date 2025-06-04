package com.store.app.petstore.Controllers.Admin;

import com.store.app.petstore.Controllers.ControllerUtils;
import com.store.app.petstore.DAO.PetDAO;
import com.store.app.petstore.Models.Entities.Pet;
import com.store.app.petstore.Views.AdminFactory;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class PetManagementController implements Initializable {

    @FXML
    private Button addPetButton;

    @FXML
    private FontAwesomeIconView closeIcon;

    @FXML
    private ChoiceBox<String> cmbSex;

    @FXML
    private ChoiceBox<String> cmbStatus;

    @FXML
    private ChoiceBox<String> cmbType;

    @FXML
    private TableColumn<Pet, String> colBreed;

    @FXML
    private TableColumn<Pet, Integer> colID;

    @FXML
    private TableColumn<Pet, String> colName;

    @FXML
    private TableColumn<Pet, Integer> colPrice;

    @FXML
    private TableColumn<Pet, String> colSex;

    @FXML
    private TableColumn<Pet, String> colStatus;

    @FXML
    private TableColumn<Pet, String> colType;

    @FXML
    private TableView<Pet> petTableView;

    @FXML
    private AnchorPane root;

    @FXML
    private FontAwesomeIconView searchIcon;

    @FXML
    private TextField searchTextField;

    @FXML
    private Button viewDetailsButton;

    private Pet selectedPet;
    private FilteredList<Pet> filteredPetList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableSize();
        setupTableView();
        loadPets();
        setupChoiceBoxes();
        setupEventHandlers();
        setupSearch();
    }

    private void setupTableSize() {
        colID.prefWidthProperty().bind(petTableView.widthProperty().multiply(0.05));
        colName.prefWidthProperty().bind(petTableView.widthProperty().multiply(0.2));
        colType.prefWidthProperty().bind(petTableView.widthProperty().multiply(0.2));
        colBreed.prefWidthProperty().bind(petTableView.widthProperty().multiply(0.15));
        colSex.prefWidthProperty().bind(petTableView.widthProperty().multiply(0.10));
        colPrice.prefWidthProperty().bind(petTableView.widthProperty().multiply(0.15));
        colStatus.prefWidthProperty().bind(petTableView.widthProperty().multiply(0.15));
    }

    private void setupTableView() {
        // Set up cell value factories for each column
        colID.setCellValueFactory(new PropertyValueFactory<>("petId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colBreed.setCellValueFactory(new PropertyValueFactory<>("breed"));
        colSex.setCellValueFactory(new PropertyValueFactory<>("sex"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colStatus.setCellValueFactory(cellData -> {
            boolean isSold = cellData.getValue().getIsSold();
            return new SimpleStringProperty(isSold ? "Đã bán" : "Còn");
        });

        // Set up row click handler
        petTableView.setRowFactory(tv -> {
            TableRow<Pet> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 1) {
                    selectedPet = row.getItem();
                    viewDetailsButton.setDisable(false);
                }
            });
            return row;
        });
    }

    private void loadPets() {
        ArrayList<Pet> pets = PetDAO.findAll();
        ObservableList<Pet> petList = FXCollections.observableArrayList(pets);
        filteredPetList = new FilteredList<>(petList, p -> true);
        petTableView.setItems(filteredPetList);
    }

    private void setupChoiceBoxes() {
        cmbType.getItems().addAll("Tất cả", "chó", "mèo");
        cmbType.setValue("Tất cả");

        cmbSex.getItems().addAll("Tất cả", "đực", "cái");
        cmbSex.setValue("Tất cả");

        cmbStatus.getItems().addAll("Tất cả", "Còn", "Đã bán");
        cmbStatus.setValue("Tất cả");

        cmbType.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            applyFilters();
        });

        cmbSex.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            applyFilters();
        });

        cmbStatus.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            applyFilters();
        });
    }

    private void setupEventHandlers() {
        addPetButton.setOnAction(event -> {
            openPetInfoPopup(null);
        });

        viewDetailsButton.setOnAction(event -> {
            if (selectedPet != null) {
                openPetInfoPopup(selectedPet);
            }
        });

        closeIcon.setOnMouseClicked(event -> {
            searchTextField.setText("");
        });

        searchIcon.setOnMouseClicked(event -> {
            applyFilters();
        });

        searchTextField.setOnAction(event -> {
            applyFilters();
        });
    }

    private void setupSearch() {
        viewDetailsButton.setDisable(true);
    }

    private void applyFilters() {
        String searchText = searchTextField.getText().toLowerCase().trim();
        String typeFilter = cmbType.getValue();
        String sexFilter = cmbSex.getValue();
        String statusFilter = cmbStatus.getValue();

        filteredPetList.setPredicate(pet -> {
            boolean matchesSearch = searchText.isEmpty() ||
                    pet.getName().toLowerCase().contains(searchText);

            boolean matchesType = typeFilter.equals("Tất cả") ||
                    pet.getType().equals(typeFilter);

            boolean matchesSex = sexFilter.equals("Tất cả") ||
                    pet.getSex().equals(sexFilter);

            boolean matchesStatus = statusFilter.equals("Tất cả") ||
                    (statusFilter.equals("Còn") && !pet.getIsSold()) ||
                    (statusFilter.equals("Đã bán") && pet.getIsSold());

            return matchesSearch && matchesType && matchesSex && matchesStatus;
        });
    }

    private void openPetInfoPopup(Pet pet) {
        Stage currentStage = (Stage) root.getScene().getWindow();
        Stage popupStage = AdminFactory.getInstance().showPopup("pet", currentStage, true, pet);

        if (popupStage != null) {
            popupStage.setOnHiding(event -> {
                loadPets();
                applyFilters();
            });
        }
    }

    public void refreshTable() {
        loadPets();
        applyFilters();
    }
}

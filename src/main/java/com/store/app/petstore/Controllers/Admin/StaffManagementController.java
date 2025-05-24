package com.store.app.petstore.Controllers.Admin;

import com.store.app.petstore.Controllers.ControllerUtils;
import com.store.app.petstore.DAO.StaffDAO;
import com.store.app.petstore.DAO.UserDAO;
import com.store.app.petstore.Models.Entities.Staff;
import com.store.app.petstore.Models.Entities.User;
import com.store.app.petstore.Views.AdminFactory;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.SimpleIntegerProperty;
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

public class StaffManagementController implements Initializable {

    @FXML
    private AnchorPane root;

    @FXML
    private Button addStaffButton;

    @FXML
    private FontAwesomeIconView closeIcon;

    @FXML
    private FontAwesomeIconView searchIcon;

    @FXML
    private TableColumn<Staff, String> colEmail;

    @FXML
    private TableColumn<Staff, String> colFullName;

    @FXML
    private TableColumn<Staff, Integer> colID;

    @FXML
    private TableColumn<Staff, String> colRole;

    @FXML
    private TableColumn<Staff, String> colStatus;

    @FXML
    private TableColumn<Staff, String> colPhone;

    @FXML
    private TableColumn<Staff, String> colAddress;

    @FXML
    private ChoiceBox<String> roleChoiceBox;

    @FXML
    private TextField searchTextField;

    @FXML
    private TableView<Staff> staffTableView;

    @FXML
    private ChoiceBox<String> statusChoiceBox;

    @FXML
    private Button viewDetailsButton;

    private FilteredList<Staff> filteredStaffList;
    private Staff selectedStaff;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableSize();
        setupTableView();
        loadStaffs();
        setupChoiceBoxes();
        setupEventHandlers();
        setupSearch();
    }

    private void setupTableSize() {
        colID.prefWidthProperty().bind(staffTableView.widthProperty().multiply(0.05));
        colFullName.prefWidthProperty().bind(staffTableView.widthProperty().multiply(0.20));
        colPhone.prefWidthProperty().bind(staffTableView.widthProperty().multiply(0.15));
        colEmail.prefWidthProperty().bind(staffTableView.widthProperty().multiply(0.20));
        colAddress.prefWidthProperty().bind(staffTableView.widthProperty().multiply(0.15));
        colRole.prefWidthProperty().bind(staffTableView.widthProperty().multiply(0.13));
        colStatus.prefWidthProperty().bind(staffTableView.widthProperty().multiply(0.12));
    }

    private void setupTableView() {
        colID.setCellValueFactory(new PropertyValueFactory<>("staffId"));
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colStatus.setCellValueFactory(cellData -> {
            boolean isActive = cellData.getValue().isActive();
            String status = isActive ? "Hiệu lực" : "Nghỉ làm";
            return new SimpleStringProperty(status);
        });

        staffTableView.setRowFactory(tv -> {
            TableRow<Staff> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 1) {
                    selectedStaff = row.getItem();
                    viewDetailsButton.setDisable(false);
                }
            });
            return row;
        });
    }

    private void loadStaffs(){
        ArrayList<Staff> staffs = StaffDAO.findAll();
        ObservableList<Staff> staffList = FXCollections.observableArrayList(staffs);
        filteredStaffList = new FilteredList<>(staffList, p -> true);
        staffTableView.setItems(filteredStaffList);
    }

    private void setupChoiceBoxes() {
        roleChoiceBox.getItems().addAll("Tất cả", "Thu ngân", "Bán hàng", "Chăm sóc thú cưng", "Tư vấn");
        roleChoiceBox.setValue("Tất cả");

        statusChoiceBox.getItems().addAll("Tất cả", "Hiệu lực", "Nghỉ làm");
        statusChoiceBox.setValue("Tất cả");

        roleChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            applyFilters();
        });

        statusChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            applyFilters();
        });
    }

    private void applyFilters() {
        String searchText = searchTextField.getText().toLowerCase().trim();
        String roleFilter = roleChoiceBox.getValue();
        String statusFilter = statusChoiceBox.getValue();

        filteredStaffList.setPredicate(staff -> {
            boolean matchesSearch = searchText.isEmpty() ||
                    staff.getFullName().toLowerCase().contains(searchText);

            boolean matchesRole = roleFilter.equals("Tất cả") ||
                    (roleFilter.equals("Thu ngân") && staff.getRole().equals("thu ngân")) ||
                    (roleFilter.equals("Bán hàng") && staff.getRole().equals("bán hàng")) ||
                    (roleFilter.equals("Chăm sóc thú cưng") && staff.getRole().equals("chăm sóc thú cưng")) ||
                    (roleFilter.equals("Tư vấn") && staff.getRole().equals("tư vấn"));

            boolean matchesStatus = statusFilter.equals("Tất cả") ||
                    (statusFilter.equals("Hiệu lực") && staff.isActive()) ||
                    (statusFilter.equals("Nghỉ làm") && !staff.isActive());

            return matchesSearch && matchesRole && matchesStatus;
        });
    }

    private void setupEventHandlers() {
        addStaffButton.setOnAction(event -> {
            openStaffInfoPopup(null);
        });

        viewDetailsButton.setOnAction(event -> {
            if (selectedStaff != null) {
                openStaffInfoPopup(selectedStaff);
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

    private void openStaffInfoPopup(Staff staff) {
        Stage currentStage = (Stage) root.getScene().getWindow();
        Stage popupStage = AdminFactory.getInstance().showPopup("staff", currentStage, true, staff);

        if (popupStage != null) {
            popupStage.setOnHiding(event -> {
                loadStaffs();
                applyFilters();
            });
        }
    }

    private void setupSearch() {
        viewDetailsButton.setDisable(true);
    }

    public void refreshTable() {
        loadStaffs();
        applyFilters();
    }

}

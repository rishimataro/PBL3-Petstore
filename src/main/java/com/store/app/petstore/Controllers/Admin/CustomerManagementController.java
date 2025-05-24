package com.store.app.petstore.Controllers.Admin;

import com.store.app.petstore.Controllers.ControllerUtils;
import com.store.app.petstore.DAO.CustomerDAO;
import com.store.app.petstore.Models.Entities.Customer;
import com.store.app.petstore.Views.AdminFactory;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
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
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class CustomerManagementController implements Initializable {

    @FXML
    private Button addCustomerButton;

    @FXML
    private FontAwesomeIconView closeIcon;

    @FXML
    private ChoiceBox<String> cmbTotalSpend;

    @FXML
    private TableColumn<Customer, Integer> colID;

    @FXML
    private TableColumn<Customer, String> colName;

    @FXML
    private TableColumn<Customer, String> colPhone;

    @FXML
    private TableColumn<Customer, Double> colTotalSpend;

    @FXML
    private TableView<Customer> customerTableView;

    @FXML
    private AnchorPane root;

    @FXML
    private FontAwesomeIconView searchIcon;

    @FXML
    private TextField searchTextField;

    @FXML
    private Button viewDetailsButton;

    private Customer selectedCustomer;
    private FilteredList<Customer> filteredCustomerList;
    private static Map<Integer, Customer> customerMap;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableSize();
        setupTableView();
        loadCustomers();
        setupChoiceBoxes();
        setupEventHandlers();
        setupSearch();
    }

    private void setupTableSize() {
        colID.prefWidthProperty().bind(customerTableView.widthProperty().multiply(0.05));
        colName.prefWidthProperty().bind(customerTableView.widthProperty().multiply(0.43));
        colPhone.prefWidthProperty().bind(customerTableView.widthProperty().multiply(0.26));
        colTotalSpend.prefWidthProperty().bind(customerTableView.widthProperty().multiply(0.26));

    }

    private void setupTableView() {
        colID.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colTotalSpend.setCellValueFactory(new PropertyValueFactory<>("totalSpend"));

        // Format the total spend column to display as currency
        colTotalSpend.setCellFactory(column -> new TableCell<Customer, Double>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.0f VND", amount));
                }
            }
        });

        customerTableView.setRowFactory(tv -> {
            TableRow<Customer> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 1) {
                    selectedCustomer = row.getItem();
                    viewDetailsButton.setDisable(false);
                }
            });
            return row;
        });
    }

    private void loadCustomers() {
        ArrayList<Customer> customers = CustomerDAO.findAllWithTotalSpend();
        ObservableList<Customer> customerList = FXCollections.observableArrayList(customers);
        filteredCustomerList = new FilteredList<>(customerList, p -> true);
        customerTableView.setItems(filteredCustomerList);

        // Create a map for quick lookup
        customerMap = new HashMap<>();
        for (Customer customer : customers) {
            customerMap.put(customer.getCustomerId(), customer);
        }
    }

    private void setupChoiceBoxes() {
        cmbTotalSpend.getItems().addAll("Tất cả", "Cao đến thấp", "Thấp đến cao");
        cmbTotalSpend.setValue("Tất cả");

        cmbTotalSpend.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            applyFilters();
        });
    }

    private void setupEventHandlers() {
        addCustomerButton.setOnAction(event -> {
            openCustomerInfoPopup(null);
        });

        viewDetailsButton.setOnAction(event -> {
            if (selectedCustomer != null) {
                openCustomerInfoPopup(selectedCustomer);
            }
        });

        closeIcon.setOnMouseClicked(event -> {
            searchTextField.setText("");
            applyFilters();
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
        String spendFilter = cmbTotalSpend.getValue();

        // First apply the search filter
        filteredCustomerList.setPredicate(customer -> {
            boolean matchesSearch = searchText.isEmpty() ||
                    customer.getFullName().toLowerCase().contains(searchText) ||
                    customer.getPhone().toLowerCase().contains(searchText);

            return matchesSearch;
        });

        // Then apply sorting based on total spend
        if (spendFilter.equals("Cao đến thấp")) {
            customerTableView.setItems(filteredCustomerList.sorted((c1, c2) ->
                Double.compare(c2.getTotalSpend(), c1.getTotalSpend())
            ));
        } else if (spendFilter.equals("Thấp đến cao")) {
            customerTableView.setItems(filteredCustomerList.sorted((c1, c2) ->
                Double.compare(c1.getTotalSpend(), c2.getTotalSpend())
            ));
        } else {
            // Default sorting by customer ID
            customerTableView.setItems(filteredCustomerList.sorted((c1, c2) ->
                Integer.compare(c1.getCustomerId(), c2.getCustomerId())
            ));
        }
    }

    private void openCustomerInfoPopup(Customer customer) {
        Stage currentStage = (Stage) root.getScene().getWindow();
        Stage popupStage = AdminFactory.getInstance().showPopup("customer", currentStage, true, customer);

        if (popupStage != null) {
            popupStage.setOnHiding(event -> {
                loadCustomers();
                applyFilters();
            });
        }
    }

    public void refreshTable() {
        loadCustomers();
        applyFilters();
    }
}

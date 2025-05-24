package com.store.app.petstore.Controllers.Admin;

import com.store.app.petstore.Controllers.ControllerUtils;
import com.store.app.petstore.DAO.DiscountDAO;
import com.store.app.petstore.Models.Entities.Discount;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class DiscountManagementController implements Initializable {

    @FXML
    private Button addDiscountButton;

    @FXML
    private FontAwesomeIconView closeIcon;

    @FXML
    private TableColumn<Discount, String> colCode;

    @FXML
    private TableColumn<Discount, LocalDate> colEndDate;

    @FXML
    private TableColumn<Discount, Integer> colID;

    @FXML
    private TableColumn<Discount, Double> colMaxDiscount;

    @FXML
    private TableColumn<Discount, Double> colMinOrder;

    @FXML
    private TableColumn<Discount, LocalDate> colStartDate;

    @FXML
    private TableColumn<Discount, String> colStatus;

    @FXML
    private TableColumn<Discount, String> colType;

    @FXML
    private TableColumn<Discount, Double> colDiscountValue;

    @FXML
    private TableView<Discount> discountTableView;

    @FXML
    private AnchorPane root;

    @FXML
    private FontAwesomeIconView searchIcon;

    @FXML
    private TextField searchTextField;

    @FXML
    private ChoiceBox<String> cmbType;

    @FXML
    private ChoiceBox<String> cmbStatus;

    @FXML
    private Button viewDetailsButton;

    private Discount selectedDiscount;
    private FilteredList<Discount> filteredDiscountList;
    private static Map<Integer, Discount> discountMap;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableSize();
        setupTableView();
        loadDiscounts();
        setupChoiceBoxes();
        setupEventHandlers();
        setupSearch();
    }

    private void setupTableSize() {
        colID.prefWidthProperty().bind(discountTableView.widthProperty().multiply(0.05));
        colCode.prefWidthProperty().bind(discountTableView.widthProperty().multiply(0.10));
        colType.prefWidthProperty().bind(discountTableView.widthProperty().multiply(0.10));
        colStartDate.prefWidthProperty().bind(discountTableView.widthProperty().multiply(0.12));
        colEndDate.prefWidthProperty().bind(discountTableView.widthProperty().multiply(0.12));
        colDiscountValue.prefWidthProperty().bind(discountTableView.widthProperty().multiply(0.11));
        colMaxDiscount.prefWidthProperty().bind(discountTableView.widthProperty().multiply(0.11));
        colMinOrder.prefWidthProperty().bind(discountTableView.widthProperty().multiply(0.14));
        colStatus.prefWidthProperty().bind(discountTableView.widthProperty().multiply(0.15));
    }

    private void setupTableView() {
        colID.setCellValueFactory(new PropertyValueFactory<>("discountId"));
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));

        colType.setCellValueFactory(cellData -> {
            String type = cellData.getValue().getDiscountType();
            String displayType = "percent".equals(type) ? "Phần trăm" : "Cố định";
            return new SimpleStringProperty(displayType);
        });

        colDiscountValue.setCellValueFactory(new PropertyValueFactory<>("value"));
        colMaxDiscount.setCellValueFactory(new PropertyValueFactory<>("maxDiscountValue"));
        colMinOrder.setCellValueFactory(new PropertyValueFactory<>("minOrderValue"));

        colStartDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colStartDate.setCellFactory(column -> new TableCell<Discount, LocalDate>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                }
            }
        });

        colEndDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        colEndDate.setCellFactory(column -> new TableCell<Discount, LocalDate>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                }
            }
        });

        colStatus.setCellValueFactory(cellData -> {
            Discount discount = cellData.getValue();
            LocalDate today = LocalDate.now();
            String status;

            if (today.isBefore(discount.getStartDate())) {
                status = "Sắp diễn ra";
            } else if (today.isAfter(discount.getEndDate())) {
                status = "Đã kết thúc";
            } else {
                status = "Đang hoạt động";
            }

            return new SimpleStringProperty(status);
        });

        discountTableView.setRowFactory(tv -> {
            TableRow<Discount> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 1) {
                    selectedDiscount = row.getItem();
                    viewDetailsButton.setDisable(false);
                }
            });
            return row;
        });
    }

    private void loadDiscounts() {
        ArrayList<Discount> discounts = DiscountDAO.findAll();
        ObservableList<Discount> discountList = FXCollections.observableArrayList(discounts);
        filteredDiscountList = new FilteredList<>(discountList, p -> true);
        discountTableView.setItems(filteredDiscountList);

        // Create a map for quick lookup
        discountMap = new HashMap<>();
        for (Discount discount : discounts) {
            discountMap.put(discount.getDiscountId(), discount);
        }
    }

    private void setupChoiceBoxes() {
        cmbType.getItems().addAll("Tất cả", "Phần trăm", "Cố định");
        cmbType.setValue("Tất cả");

        cmbStatus.getItems().addAll("Tất cả", "Đang hoạt động", "Sắp diễn ra", "Đã kết thúc");
        cmbStatus.setValue("Tất cả");

        cmbType.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            applyFilters();
        });

        cmbStatus.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            applyFilters();
        });
    }

    private void setupEventHandlers() {
        addDiscountButton.setOnAction(event -> {
            openDiscountInfoPopup(null);
        });

        viewDetailsButton.setOnAction(event -> {
            if (selectedDiscount != null) {
                openDiscountInfoPopup(selectedDiscount);
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
        String typeFilter = cmbType.getValue();
        String statusFilter = cmbStatus.getValue();
        LocalDate today = LocalDate.now();

        filteredDiscountList.setPredicate(discount -> {
            boolean matchesSearch = searchText.isEmpty() ||
                    discount.getCode().toLowerCase().contains(searchText);

            boolean matchesType = typeFilter.equals("Tất cả") ||
                    (typeFilter.equals("Phần trăm") && "percent".equals(discount.getDiscountType())) ||
                    (typeFilter.equals("Cố định") && "fixed".equals(discount.getDiscountType()));

            boolean matchesStatus;
            if (statusFilter.equals("Tất cả")) {
                matchesStatus = true;
            } else if (statusFilter.equals("Đang hoạt động")) {
                matchesStatus = !today.isBefore(discount.getStartDate()) && !today.isAfter(discount.getEndDate());
            } else if (statusFilter.equals("Sắp diễn ra")) {
                matchesStatus = today.isBefore(discount.getStartDate());
            } else { // "Đã kết thúc"
                matchesStatus = today.isAfter(discount.getEndDate());
            }

            return matchesSearch && matchesType && matchesStatus;
        });
    }

    private void openDiscountInfoPopup(Discount discount) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Admin/DiscountInfor.fxml"));
            Parent root = loader.load();

            DiscountInforController controller = loader.getController();
            if (discount != null) {
                controller.setDiscount(discount);
            }

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            loadDiscounts();
            applyFilters();

        } catch (IOException e) {
            e.printStackTrace();
            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể mở cửa sổ thông tin khuyến mãi!");
        }
    }

    public void refreshTable() {
        loadDiscounts();
        applyFilters();
    }
}

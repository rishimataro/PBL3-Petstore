package com.store.app.petstore.Controllers.Admin;

import com.store.app.petstore.Controllers.ControllerUtils;
import com.store.app.petstore.DAO.ProductDAO;
import com.store.app.petstore.Models.Entities.Product;
import com.store.app.petstore.Views.AdminFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.text.NumberFormat;
import java.util.*;

public class InventoryController implements Initializable {

    @FXML
    private ComboBox<String> categoryComboBox;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private Button resetButton;

    @FXML
    private Label totalProductsLabel;

    @FXML
    private Label totalValueLabel;

    @FXML
    private Label lowStockLabel;

    @FXML
    private Label outOfStockLabel;

    @FXML
    private TableView<Product> inventoryTable;

    @FXML
    private TableColumn<Product, Integer> idColumn;

    @FXML
    private TableColumn<Product, String> nameColumn;

    @FXML
    private TableColumn<Product, String> categoryColumn;

    @FXML
    private TableColumn<Product, Integer> stockColumn;

    @FXML
    private TableColumn<Product, String> priceColumn;

    @FXML
    private TableColumn<Product, String> valueColumn;

    @FXML
    private TableColumn<Product, String> statusColumn;

    @FXML
    private TableColumn<Product, Void> actionsColumn;

    @FXML
    private Button exportButton;

    @FXML
    private Button printButton;

    @FXML
    private Button backButton;

    private final ProductDAO productDAO = ProductDAO.getInstance();
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    private ObservableList<Product> productList;
    private static final int LOW_STOCK_THRESHOLD = 10;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        setupFilters();
        setupButtons();
        loadData();
    }

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("productId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));

        priceColumn.setCellValueFactory(cellData -> {
            int price = cellData.getValue().getPrice();
            return new SimpleStringProperty(currencyFormatter.format(price));
        });

        valueColumn.setCellValueFactory(cellData -> {
            Product product = cellData.getValue();
            int value = product.getPrice() * product.getStock();
            return new SimpleStringProperty(currencyFormatter.format(value));
        });

        statusColumn.setCellValueFactory(cellData -> {
            int stock = cellData.getValue().getStock();
            String status;
            if (stock <= 0) {
                status = "Hết hàng";
            } else if (stock <= LOW_STOCK_THRESHOLD) {
                status = "Sắp hết";
            } else {
                status = "Bình thường";
            }
            return new SimpleStringProperty(status);
        });

        statusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "Hết hàng":
                            getStyleClass().add("status-out");
                            break;
                        case "Sắp hết":
                            getStyleClass().add("status-low");
                            break;
                        case "Bình thường":
                            getStyleClass().add("status-normal");
                            break;
                    }
                }
            }
        });

        setupActionsColumn();
    }

    private void setupActionsColumn() {
        Callback<TableColumn<Product, Void>, TableCell<Product, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Product, Void> call(final TableColumn<Product, Void> param) {
                return new TableCell<>() {
                    private final Button editButton = new Button("Sửa");
                    
                    {
                        editButton.getStyleClass().add("edit-button");
                        editButton.setOnAction(event -> {
                            Product product = getTableView().getItems().get(getIndex());
                            handleEditProduct(product);
                        });
                    }
                    
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(editButton);
                        }
                    }
                };
            }
        };
        
        actionsColumn.setCellFactory(cellFactory);
    }

    private void setupFilters() {
        Set<String> categories = new HashSet<>();
        for (Product product : productDAO.findAll()) {
            if (product.getCategory() != null && !product.getCategory().isEmpty()) {
                categories.add(product.getCategory());
            }
        }

        List<String> categoryList = new ArrayList<>(categories);
        Collections.sort(categoryList);
        categoryList.add(0, "Tất cả");

        categoryComboBox.setItems(FXCollections.observableArrayList(categoryList));
        categoryComboBox.getSelectionModel().selectFirst();

        searchButton.setOnAction(e -> applyFilters());

        resetButton.setOnAction(e -> resetFilters());
    }

    private void setupButtons() {
        exportButton.setOnAction(e -> exportInventory());

        printButton.setOnAction(e -> printInventory());

        backButton.setOnAction(e -> navigateToDashboard());
    }

    private void loadData() {
        ArrayList<Product> products = productDAO.findAll();
        productList = FXCollections.observableArrayList(products);
        inventoryTable.setItems(productList);

        updateSummary(products);
    }

    private void updateSummary(List<Product> products) {
        int totalProducts = products.size();
        long totalValue = 0;
        int lowStockCount = 0;
        int outOfStockCount = 0;
        
        for (Product product : products) {
            int stock = product.getStock();
            int price = product.getPrice();
            
            totalValue += (long) stock * price;
            
            if (stock <= 0) {
                outOfStockCount++;
            } else if (stock <= LOW_STOCK_THRESHOLD) {
                lowStockCount++;
            }
        }
        
        totalProductsLabel.setText(String.valueOf(totalProducts));
        totalValueLabel.setText(currencyFormatter.format(totalValue));
        lowStockLabel.setText(String.valueOf(lowStockCount));
        outOfStockLabel.setText(String.valueOf(outOfStockCount));
    }

    private void applyFilters() {
        String category = categoryComboBox.getValue();
        String searchText = searchField.getText().trim().toLowerCase();
        
        ArrayList<Product> filteredProducts;
        
        if ("Tất cả".equals(category)) {
            filteredProducts = productDAO.findAll();
        } else {
            filteredProducts = productDAO.findByCategory(category);
        }

        if (!searchText.isEmpty()) {
            filteredProducts.removeIf(product -> 
                !product.getName().toLowerCase().contains(searchText));
        }
        
        productList = FXCollections.observableArrayList(filteredProducts);
        inventoryTable.setItems(productList);

        updateSummary(filteredProducts);
    }

    private void resetFilters() {
        categoryComboBox.getSelectionModel().selectFirst();
        searchField.clear();
        loadData();
    }

    private void handleEditProduct(Product product) {
        ControllerUtils.showInformationAndWait("Chỉnh sửa sản phẩm", 
                "Đang chỉnh sửa sản phẩm: " + product.getName());
    }

    private void exportInventory() {
        ControllerUtils.showInformationAndWait("Xuất báo cáo", 
                "Đang xuất báo cáo tồn kho...");
    }

    private void printInventory() {
        ControllerUtils.showInformationAndWait("In báo cáo", 
                "Đang in báo cáo tồn kho...");
    }

    private void navigateToDashboard() {
        Stage currentStage = (Stage) backButton.getScene().getWindow();
        AdminFactory.getInstance().switchContent("dashboard", currentStage);
    }
}

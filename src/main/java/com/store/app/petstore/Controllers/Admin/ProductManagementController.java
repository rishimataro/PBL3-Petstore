package com.store.app.petstore.Controllers.Admin;

import com.store.app.petstore.DAO.ProductDAO;
import com.store.app.petstore.Models.Entities.Product;
import com.store.app.petstore.Views.AdminFactory;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ResourceBundle;

public class ProductManagementController implements Initializable {

    @FXML
    private AnchorPane root;

    @FXML
    private Button addProductNutton;

    @FXML
    private ChoiceBox<String> catelogyChoiceBox;

    @FXML
    private FontAwesomeIconView closeIcon;

    @FXML
    private ChoiceBox<String> cmbSort;

    @FXML
    private TableColumn<Product, String> colCatelogy;

    @FXML
    private TableColumn<Product, Integer> colID;

    @FXML
    private TableColumn<Product, String> colName;

    @FXML
    private TableColumn<Product, Integer> colPrice;

    @FXML
    private TableColumn<Product, String> colStatus;

    @FXML
    private TableColumn<Product, Integer> colStock;

    @FXML
    private TableView<Product> productTableView;

    @FXML
    private FontAwesomeIconView searchIcon;

    @FXML
    private TextField searchTextField;

    @FXML
    private Button viewDetailsButton;

    private Product selectedProduct;
    private FilteredList<Product> filteredProductList;
    private SortedList<Product> sortedProductList;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableSize();
        setupTableView();
        loadProducts();
        setupChoiceBoxes();
        setupEventHandlers();
        setupSearch();
    }

    private void setupTableSize() {
        colID.prefWidthProperty().bind(productTableView.widthProperty().multiply(0.05));
        colName.prefWidthProperty().bind(productTableView.widthProperty().multiply(0.30));
        colCatelogy.prefWidthProperty().bind(productTableView.widthProperty().multiply(0.19));
        colPrice.prefWidthProperty().bind(productTableView.widthProperty().multiply(0.15));
        colStock.prefWidthProperty().bind(productTableView.widthProperty().multiply(0.15));
        colStatus.prefWidthProperty().bind(productTableView.widthProperty().multiply(0.16));
    }

    private void setupTableView() {
        colID.setCellValueFactory(new PropertyValueFactory<>("productId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCatelogy.setCellValueFactory(new PropertyValueFactory<>("category"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colStatus.setCellValueFactory(cellData -> {
            boolean isSold = cellData.getValue().getIsSold();
            return new SimpleStringProperty(isSold ? "Đã bán" : "Còn hàng");
        });

        productTableView.setRowFactory(tv -> {
            TableRow<Product> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 1) {
                    selectedProduct = row.getItem();
                    viewDetailsButton.setDisable(false);
                }
            });
            return row;
        });
    }

    private void loadProducts() {
        ArrayList<Product> products = ProductDAO.findAll();
        ObservableList<Product> productList = FXCollections.observableArrayList(products);
        filteredProductList = new FilteredList<>(productList, p -> true);
        sortedProductList = new SortedList<>(filteredProductList);

        productTableView.setItems(sortedProductList);

        applyFilters();
    }

    private void setupChoiceBoxes() {
        catelogyChoiceBox.getItems().addAll("Tất cả", "Thức ăn", "Đồ chơi", "Phụ kiện");
        catelogyChoiceBox.setValue("Tất cả");

        cmbSort.getItems().addAll("Mới nhất", "Giá thấp đến cao", "Giá cao đến thấp");
        cmbSort.setValue("Mới nhất");

        catelogyChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            applyFilters();
        });

        cmbSort.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            applyFilters();
        });
    }

    private void applyFilters() {
        String searchText = searchTextField.getText() != null ? searchTextField.getText().toLowerCase().trim() : "";
        String catelogyFilter = catelogyChoiceBox.getValue() != null ? catelogyChoiceBox.getValue() : "Tất cả";
        String sortFilter = cmbSort.getValue() != null ? cmbSort.getValue() : "Mới nhất";

        filteredProductList.setPredicate(product -> {
            if (product == null) return false;

            boolean matchesSearch = searchText.isEmpty() ||
                    product.getName().toLowerCase().contains(searchText);

            boolean matchesCatelogy = catelogyFilter.equals("Tất cả") ||
                    (catelogyFilter.equals("Thức ăn") && product.getCategory().equals("thức ăn")) ||
                    (catelogyFilter.equals("Đồ chơi") && product.getCategory().equals("đồ chơi")) ||
                    (catelogyFilter.equals("Phụ kiện") && product.getCategory().equals("phụ kiện"));

            return matchesSearch && matchesCatelogy;
        });

        Comparator<Product> comparator = null;
        if ("Giá thấp đến cao".equals(sortFilter)) {
            comparator = Comparator.comparing(Product::getPrice);
        } else if ("Giá cao đến thấp".equals(sortFilter)) {
            comparator = Comparator.comparing(Product::getPrice).reversed();
        } else {
            comparator = Comparator.comparing(Product::getProductId);
        }

        sortedProductList.setComparator(comparator);
    }

    private void setupEventHandlers() {
        addProductNutton.setOnAction(event -> {
            openProductInfoPopup(null);
        });

        viewDetailsButton.setOnAction(event -> {
            if (selectedProduct != null) {
                openProductInfoPopup(selectedProduct);
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

    private void openProductInfoPopup(Product product) {
        Stage currentStage = (Stage) root.getScene().getWindow();
        Stage popupStage = AdminFactory.getInstance().showPopup("product", currentStage, true, product);

        if (popupStage != null) {
            popupStage.setOnHiding(event -> {
                loadProducts();
                applyFilters();
            });
        }
    }
    public void refreshTable() {
        loadProducts();
        applyFilters();
    }
}

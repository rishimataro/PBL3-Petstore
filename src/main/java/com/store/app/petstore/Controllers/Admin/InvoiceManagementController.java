package com.store.app.petstore.Controllers.Admin;

import com.store.app.petstore.Controllers.ControllerUtils;
import com.store.app.petstore.Controllers.Staff.PetItemDetailController;
import com.store.app.petstore.Controllers.Staff.ProductItemDetailController;
import com.store.app.petstore.DAO.*;
import com.store.app.petstore.Models.Entities.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class InvoiceManagementController {

    @FXML
    private TableView<Order> invoice_table;
    @FXML
    private TableColumn<Order, Number> sttCol;
    @FXML
    private TableColumn<Order, Integer> invoice_idCol;
    @FXML
    private TableColumn<Order, String> invoice_customerCol;
    @FXML
    private TableColumn<Order, LocalDateTime> invoice_timeCol;
    @FXML
    private TableColumn<Order, Double> invoice_totalBillCol;
    @FXML
    private TableColumn<Order, String> invoice_statusCol;
    @FXML
    private TextField discountCode;
    @FXML
    private Label detailDiscountValue;
    @FXML
    private TextField searchInvoice;
    @FXML
    private DatePicker start_datepicker;
    @FXML
    private DatePicker end_datepicker;
    @FXML
    private VBox productListVBox;
    @FXML
    private CheckBox allowDeletedInvoice;

    static private Map<Integer, Product> productMap;
    static private Map<Integer, Pet> petMap;

    @FXML
    private Label detailInvoiceID;
    @FXML
    private Label detailInvoiceValue;
    @FXML
    private Label DetailInvoiceDiscount;
    @FXML
    private Label detailInvoiceTotal;
    @FXML
    private TextField totalRevenue;

    private double totalRevenueValue;
    private Map<Integer, Customer> customerMap;

    @FXML
    private FontAwesomeIconView clear_invoice_search;

    @FXML
    public void initialize() {
        sttCol.prefWidthProperty().bind(invoice_table.widthProperty().multiply(0.1));
        invoice_idCol.prefWidthProperty().bind(invoice_table.widthProperty().multiply(0.15));
        invoice_customerCol.prefWidthProperty().bind(invoice_table.widthProperty().multiply(0.35));
        invoice_timeCol.prefWidthProperty().bind(invoice_table.widthProperty().multiply(0.2));
        invoice_totalBillCol.prefWidthProperty().bind(invoice_table.widthProperty().multiply(0.1));
        invoice_statusCol.prefWidthProperty().bind(invoice_table.widthProperty().multiply(0.1));

        sttCol.setCellValueFactory(
                col -> new ReadOnlyObjectWrapper<>(invoice_table.getItems().indexOf(col.getValue()) + 1));
        sttCol.setSortable(false);

        invoice_idCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));

        invoice_customerCol.setCellFactory(column -> new TableCell<Order, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                } else {
                    Order order = (Order) getTableRow().getItem();
                    Customer customer = customerMap.get(order.getCustomerId());
                    setText(customer != null ? customer.getFullName() : "(Không tìm thấy)");
                }
            }
        });

        invoice_timeCol.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        invoice_totalBillCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        invoice_statusCol.setCellFactory(column -> new TableCell<Order, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                } else {
                    Order order = (Order) getTableRow().getItem();
                    boolean isDeleted = order.isDeleted();
                    setText(isDeleted ? "Đã huỷ" : "Hiệu lực");
                }
            }
        });

        invoice_timeCol.setCellFactory(column -> new TableCell<Order, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null
                        : item.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            }
        });
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysAgo = today.minusDays(30);
        start_datepicker.setValue(thirtyDaysAgo);
        end_datepicker.setValue(today);
        loadInvoicesWithFilter();

        invoice_table.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                Order order = invoice_table.getSelectionModel().getSelectedItem();
                if (order != null) {
                    loadProductDetails(order);
                }
            }
        });
        allowDeletedInvoice.selectedProperty().addListener((obs, oldVal, newVal) -> {
            loadInvoicesWithFilter();
        });
        searchInvoice.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                loadInvoicesWithFilter();
            }
        });
        clear_invoice_search.setOnMouseClicked(event -> {
            searchInvoice.clear();
        });
        start_datepicker.getEditor().setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case DELETE, BACK_SPACE -> start_datepicker.setValue(null);
                default -> {
                }
            }
        });
        end_datepicker.getEditor().setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case DELETE, BACK_SPACE -> end_datepicker.setValue(null);

                default -> {
                }
            }
        });
        //

        productMap = ProductDAO.findAll().stream()
                .collect(Collectors.toMap(Product::getProductId, p -> p));
        petMap = Objects.requireNonNull(PetDAO.findAll()).stream()
                .collect(Collectors.toMap(Pet::getPetId, p -> p));
    }

    @FXML
    private void onSearchClicked() {
        loadInvoicesWithFilter();
    }

    private void loadProductDetails(Order order) {
        productListVBox.getChildren().clear();
        List<OrderDetail> listOrderDetail = OrderDetailDAO.getInstance().findByOrderId(order.getOrderId());

        DiscountInfo(order);
        detailInvoiceID.setText(order.getOrderId() + "");
        detailInvoiceValue.setText(String.format("%, .0f VNĐ", order.getTotalPrice()));
        for (OrderDetail detail : listOrderDetail) {
            Node itemNode = createItemNode(detail, productMap, petMap);
            if (itemNode != null) {
                productListVBox.getChildren().add(itemNode);
            }
        }
    }

    private Node createItemNode(OrderDetail detail, Map<Integer, Product> productMap, Map<Integer, Pet> petMap) {
        try {
            FXMLLoader loader;
            Parent node;

            switch (detail.getItemType()) {
                case "product":
                    loader = new FXMLLoader(getClass().getResource("/FXML/Staff/ProductItemDetail.fxml"));
                    node = loader.load();
                    ProductItemDetailController prodCtrl = loader.getController();
                    prodCtrl.setData(productMap.get(detail.getItemId()), detail);
                    return node;

                case "pet":
                    loader = new FXMLLoader(getClass().getResource("/FXML/Staff/PetItemDetail.fxml"));
                    node = loader.load();
                    PetItemDetailController petCtrl = loader.getController();
                    petCtrl.setData(petMap.get(detail.getItemId()), detail);
                    return node;

                default:
                    System.err.println("Unknown item type: " + detail.getItemType());
                    return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadInvoicesWithFilter() {
        LocalDateTime startDateTime = (start_datepicker.getValue() != null) ? start_datepicker.getValue().atStartOfDay()
                : null;
        LocalDateTime endDateTime = (end_datepicker.getValue() != null) ? end_datepicker.getValue().atTime(23, 59, 59)
                : null;
        boolean allowDeleted = allowDeletedInvoice.isSelected();
        customerMap = Objects.requireNonNull(CustomerDAO.findAll())
                .stream()
                .collect(Collectors.toMap(Customer::getCustomerId, customer -> customer));

        String keyword = searchInvoice.getText().toLowerCase().trim();
        List<Order> filtered = OrderDAO
                .findAll()
                .stream()
                .filter(order -> {
                    LocalDateTime orderDate = order.getOrderDate();

                    boolean dateMatch = true;
                    if (orderDate != null) {
                        if (startDateTime != null) {
                            dateMatch &= !orderDate.isBefore(startDateTime);
                        }
                        if (endDateTime != null) {
                            dateMatch &= !orderDate.isAfter(endDateTime);
                        }
                    }

                    boolean statusMatch = allowDeleted || !order.isDeleted();

                    boolean matchSearch = keyword.isEmpty()
                            || Integer.toString(order.getOrderId()).contains(keyword)
                            || customerMap.get(order.getCustomerId()).getFullName().toLowerCase().contains(keyword);
                    return dateMatch && statusMatch && matchSearch;
                })
                .collect(Collectors.toList());
        invoice_table.setItems(FXCollections.observableArrayList(filtered));
        calculateTotalRevenue(filtered);
    }

    private void calculateTotalRevenue(List<Order> filteredOrders) {
        double total = 0;
        for (Order order : filteredOrders) {
            if (!order.isDeleted()) {
                total += order.getTotalPrice();
            }
        }

        totalRevenue.setText(String.format("%, .0f VNĐ", total));
    }

    @FXML
    private void DeleteInvoiceClicked() {
        Order selectedOrder = invoice_table.getSelectionModel().getSelectedItem();

        if (selectedOrder == null) {
            ControllerUtils.showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn hóa đơn để huỷ!");
            return;
        }

        if (ControllerUtils.showConfirmationAndWait("Xác nhận huỷ hoá đơn",
                "Bạn có chắc chắn muốn huỷ hoá đơn này không?")) {
            selectedOrder.setDeleted(true);
            OrderDAO.update(selectedOrder);
            loadInvoicesWithFilter();
        }

    }

    private void DiscountInfo(Order order) {
        Discount dc = DiscountDAO.findById(order.getDiscountId());
        double total_value = order.getTotalPrice();

        double max_discount_value = 0.0;
        double discount_value = 0.0;
        double discount_min_value = 0.0;
        String discount_type = "";
        String discount_code = "";
        if (dc != null) {
            max_discount_value = dc.getMaxDiscountValue();
            discount_value = dc.getValue();
            discount_min_value = dc.getMinOrderValue();
            discount_type = dc.getDiscountType();
            discount_code = dc.getCode();
        }

        double discount = 0.0;
        if (discount_min_value > total_value) {
            discount = 0.0;
        } else if (discount_type.equals("percent")) {
            discount = ((total_value * discount_value) / 100);
            if (discount > max_discount_value) {
                discount = max_discount_value;
            }
        } else if (discount_type.equals("fixed")) {
            discount = discount_value;
        }
        discountCode.setText(discount_code);
        detailDiscountValue.setText(String.format("%, .0f VNĐ", discount_value));
        detailInvoiceTotal.setText(String.format("%, .0f VNĐ", total_value - discount));
    }
}

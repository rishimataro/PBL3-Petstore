package com.store.app.petstore.Controllers.Staff;

import com.store.app.petstore.DAO.*;
import com.store.app.petstore.Models.Entities.*;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class BillHistoryController {
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
    private Button search_btn;
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
    public void initialize() {
        sttCol.prefWidthProperty().bind(invoice_table.widthProperty().multiply(0.1));
        invoice_idCol.prefWidthProperty().bind(invoice_table.widthProperty().multiply(0.15));
        invoice_customerCol.prefWidthProperty().bind(invoice_table.widthProperty().multiply(0.35));
        invoice_timeCol.prefWidthProperty().bind(invoice_table.widthProperty().multiply(0.2));
        invoice_totalBillCol.prefWidthProperty().bind(invoice_table.widthProperty().multiply(0.1));
        invoice_statusCol.prefWidthProperty().bind(invoice_table.widthProperty().multiply(0.1));

        sttCol.setCellValueFactory(col ->
                new ReadOnlyObjectWrapper<>(invoice_table.getItems().indexOf(col.getValue()) + 1)
        );
        sttCol.setSortable(false);

        invoice_idCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        Map<Integer, Customer> customerMap = CustomerDAO.getInstance().findAll()
                .stream()
                .collect(Collectors.toMap(Customer::getCustomerId, customer -> customer));

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
        invoice_statusCol.setCellFactory(column -> new TableCell<Order, String>(){
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
        // Định dạng ngày giờ hiển thị
        invoice_timeCol.setCellFactory(column -> new TableCell<Order, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            }
        });

//        ObservableList<Order> orders = FXCollections.observableArrayList(OrderDAO.getInstance().findAll());
//        invoice_table.setItems(orders);
        loadInvoicesWithFilter();
        //event
        invoice_table.setOnMouseClicked(event -> {
            if(event.getClickCount() == 1) {
                Order order = invoice_table.getSelectionModel().getSelectedItem();
                if (order != null) {
                    loadProductDetails(order);
                }
            }
        });
        allowDeletedInvoice.selectedProperty().addListener((obs, oldVal, newVal) -> {
            loadInvoicesWithFilter();
        });
        productMap = ProductDAO.getInstance().findAll().stream()
            .collect(Collectors.toMap(Product::getProductId, p -> p));
        petMap = PetDAO.getInstance().findAll().stream()
            .collect(Collectors.toMap(Pet::getPetId, p -> p));
    }
    @FXML
    private void onSearchClicked(){
//        LocalDate start = start_datepicker.getValue();
//        LocalDate end = end_datepicker.getValue();
//        LocalDateTime startDateTime = (start_datepicker.getValue() != null) ? start_datepicker.getValue().atStartOfDay() : null;
//        LocalDateTime endDateTime = (end_datepicker.getValue() != null) ? end_datepicker.getValue().atTime(23, 59, 59) : null;
//
//        List<Order> filtered = OrderDAO.getInstance()
//                .findAll()
//                .stream()
//                .filter(order -> {
//                    LocalDateTime orderDate = order.getOrderDate();
//                    return (orderDate != null) && (
//                            (startDateTime == null && endDateTime == null) || // Không lọc gì cả
//                                    (startDateTime != null && endDateTime == null && (orderDate.isEqual(startDateTime) || orderDate.isAfter(startDateTime))) ||
//                                    (startDateTime == null && endDateTime != null && (orderDate.isEqual(endDateTime) || orderDate.isBefore(endDateTime))) ||
//                                    (startDateTime != null && endDateTime != null &&
//                                            (orderDate.isEqual(startDateTime) || orderDate.isAfter(startDateTime)) &&
//                                            (orderDate.isEqual(endDateTime) || orderDate.isBefore(endDateTime)))
//                    );
//                })
//                .collect(Collectors.toList());
//        invoice_table.setItems(FXCollections.observableArrayList(filtered));
        loadInvoicesWithFilter();

    }

private void loadProductDetails(Order order) {
    productListVBox.getChildren().clear();
    List<OrderDetail> listOrderDetail = OrderDetailDAO.getInstance().findByOrderId(order.getOrderId());

//    Map<Integer, Product> productMap = ProductDAO.getInstance().findAll().stream()
//            .collect(Collectors.toMap(Product::getProductId, p -> p));

//    Map<Integer, Pet> petMap = PetDAO.getInstance().findAll().stream()
//            .collect(Collectors.toMap(Pet::getPetId, p -> p));
    double discount = 0;
    detailInvoiceID.setText(order.getOrderId() + "");
    detailInvoiceValue.setText(order.getTotalPrice() + "");
    detailInvoiceTotal.setText(order.getTotalPrice() - discount + "");
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
        LocalDateTime startDateTime = (start_datepicker.getValue() != null) ? start_datepicker.getValue().atStartOfDay() : null;
        LocalDateTime endDateTime = (end_datepicker.getValue() != null) ? end_datepicker.getValue().atTime(23, 59, 59) : null;
        boolean allowDeleted = allowDeletedInvoice.isSelected();

        List<Order> filtered = OrderDAO.getInstance()
                .findAll()
                .stream()
                .filter(order -> {
                    LocalDateTime orderDate = order.getOrderDate();

                    // 1. Lọc theo ngày
                    boolean dateMatch = true;
                    if (orderDate != null) {
                        if (startDateTime != null) {
                            dateMatch &= !orderDate.isBefore(startDateTime);
                        }
                        if (endDateTime != null) {
                            dateMatch &= !orderDate.isAfter(endDateTime);
                        }
                    }

                    // 2. Lọc theo trạng thái (nếu không cho phép hiện bản ghi bị xóa)
                    boolean statusMatch = allowDeleted || !order.isDeleted();

                    return dateMatch && statusMatch;
                })
                .collect(Collectors.toList());

        invoice_table.setItems(FXCollections.observableArrayList(filtered));
    }
    @FXML
    private void DeleteInvoiceClicked(){
        Order selectedOrder = invoice_table.getSelectionModel().getSelectedItem();

        if (selectedOrder == null) {
            showAlert("Vui lòng chọn hóa đơn để huỷ!", Alert.AlertType.WARNING);
            return;
        }

        // Xác nhận huỷ
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận huỷ hoá đơn");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Bạn có chắc chắn muốn huỷ hoá đơn này?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            selectedOrder.setDeleted(true);  // hoặc status khác tuỳ hệ thống
            OrderDAO.getInstance().update(selectedOrder); // nhớ phải có hàm update
            loadInvoicesWithFilter(); // reload lại bảng dựa theo filter hiện tại
        }
//        selectedOrder.setDeleted(true);  // hoặc status khác tuỳ hệ thống
//        OrderDAO.getInstance().update(selectedOrder); // nhớ phải có hàm update
//        loadInvoicesWithFilter(); // reload lại bảng dựa theo filter hiện tại
    }
    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}



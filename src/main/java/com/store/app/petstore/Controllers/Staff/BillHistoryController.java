package com.store.app.petstore.Controllers.Staff;

import com.store.app.petstore.DAO.CustomerDAO;
import com.store.app.petstore.Models.Entities.Customer;
import com.store.app.petstore.Models.Entities.User;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.store.app.petstore.Models.Entities.Order;
import com.store.app.petstore.DAO.OrderDAO;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
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
    public void initialize() {
        sttCol.prefWidthProperty().bind(invoice_table.widthProperty().multiply(0.1));
        invoice_idCol.prefWidthProperty().bind(invoice_table.widthProperty().multiply(0.15));
        invoice_customerCol.prefWidthProperty().bind(invoice_table.widthProperty().multiply(0.25));
        invoice_timeCol.prefWidthProperty().bind(invoice_table.widthProperty().multiply(0.35));
        invoice_totalBillCol.prefWidthProperty().bind(invoice_table.widthProperty().multiply(0.15));

        sttCol.setCellValueFactory(col ->
                new ReadOnlyObjectWrapper<>(invoice_table.getItems().indexOf(col.getValue()) + 1)
        );
        sttCol.setSortable(false);

        invoice_idCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));
//        invoice_customerCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        // Cập nhật cách hiển thị tên khách hàng
//        invoice_customerCol.setCellFactory(column -> new TableCell<Order, String>() {
//            @Override
//            protected void updateItem(String item, boolean empty) {
//                super.updateItem(item, empty);
//                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
//                    setText(null);
//                } else {
//                    Order order = (Order) getTableRow().getItem();
//                    Customer customer = CustomerDAO.getInstance().findById(order.getCustomerId()); // Giả sử có phương thức getCustomer()
//                    setText(customer != null ? customer.getFullName() : "");
//                }
//            }
//        });
        Map<Integer, Customer> customerMap = CustomerDAO.getInstance().findAll()
                .stream()
                .collect(Collectors.toMap(Customer::getCustomerId, customer -> customer));

        // Bước 2: Cài đặt cell value factory cho customerId (đơn giản)
//        invoice_customerCol.setCellValueFactory(cellData ->
//                new ReadOnlyObjectWrapper<>(cellData.getValue().getCustomerId())
//        );

        // Bước 3: Cài đặt hiển thị từ cache
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

        // Định dạng ngày giờ hiển thị
        invoice_timeCol.setCellFactory(column -> new TableCell<Order, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            }
        });

        ObservableList<Order> orders = FXCollections.observableArrayList(OrderDAO.getInstance().findAll());
        invoice_table.setItems(orders);
    }


}

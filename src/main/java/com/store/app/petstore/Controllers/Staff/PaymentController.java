package com.store.app.petstore.Controllers.Staff;

import com.store.app.petstore.Controllers.ControllerUtils;
import com.store.app.petstore.DAO.*;
import com.store.app.petstore.Models.Entities.*;
import com.store.app.petstore.Sessions.SessionManager;
import com.store.app.petstore.Views.StaffFactory;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.control.Tab;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.ArrayList;

import static com.store.app.petstore.Controllers.Staff.HoaDonPDF.xuatHoaDonPDF;

public class PaymentController implements Initializable {
    @FXML
    private AnchorPane root;

    @FXML
    private Button addCustomerBtn;

    @FXML
    private Button backbtn;

    @FXML
    private FontAwesomeIconView clearSearchIcon;

    @FXML
    private GridPane customerFormGrid;

    @FXML
    private TextField customerIdField;

    @FXML
    private VBox customerInfoBox;

    @FXML
    private Label customerInfoLabel;

    @FXML
    private TextField customerNameField;

    @FXML
    private TextField customerPhoneField;

    @FXML
    private Label discountAmountLabel;

    @FXML
    private Label finalAmountLabel;

    @FXML
    private Button fixCustomerbtn;

    @FXML
    private TextField orderTimeField;

    @FXML
    private Label otherInfoLabel;

    @FXML
    private Button paymentBtn;

    @FXML
    private GridPane paymentInfoGrid;

    @FXML
    private TextField searchCustomerField;

    @FXML
    private FontAwesomeIconView searchIcon;

    @FXML
    private GridPane staffFormGrid;

    @FXML
    private TextField staffIdField;

    @FXML
    private TextField staffNameField;

    @FXML
    private Label totalAmountLabel;

    @FXML
    private TextField voucherCodeField;

    @FXML
    private VBox orderDetailsBox;

    private Customer customer;
    private Staff staff;
    private boolean isNewCustomer = true;
    private int currentCustomerId;
    private int currentStaffId;

    private Order currentOrder;
    private ArrayList<OrderDetail> orderDetails;
    private Map<Integer, Product> productMap;
    private Map<Integer, Pet> petMap;
    private Discount currentDiscount;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        customerIdField.setDisable(true);
        customerNameField.setDisable(true);
        customerPhoneField.setDisable(true);

        staffIdField.setDisable(true);
        staffNameField.setDisable(true);
        orderTimeField.setDisable(true);

        voucherCodeField.setDisable(true);

        setupStaffInfo();
        setupButtonActions();

        searchCustomerField.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                handleSearchCustomer();
            }
        });

        Order order = SessionManager.getCurrentOrder();
        ArrayList<OrderDetail> orderDetails = SessionManager.getCurrentOrderDetails();
        Map<Integer, Product> products = SessionManager.getCurrentOrderProducts();
        Map<Integer, Pet> pets = SessionManager.getCurrentOrderPets();
        Discount discount = SessionManager.getCurrentDiscount();
        if (order != null && orderDetails != null && products != null && pets != null) {
            setOrderData(order, orderDetails, products, pets, discount);
        }
    }

    private void handleAddCustomer() {
        Stage currentStage = (Stage) root.getScene().getWindow();
        Stage popupStage = StaffFactory.getInstance().showPopup("customer", currentStage, true, customer);

        if (popupStage != null) {
            popupStage.setOnHiding(event -> {
                Customer newCustomer = (Customer) popupStage.getUserData();
                if (newCustomer != null) {
                    customer = newCustomer;
                    setupCustomerInfo();
                }
            });
        }
    }

    private void handleSearchCustomer() {
        String searchText = searchCustomerField.getText().trim();

        if (searchText.isEmpty()) {
            ControllerUtils.showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập thông tin tìm kiếm.");
            return;
        }

        Customer foundCustomer = CustomerDAO.findByPhone(searchText);

        if (foundCustomer != null) {
            customer = foundCustomer;
            setupCustomerInfo();
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Cảnh báo");
            alert.setHeaderText(null);
            alert.setContentText(
                    "Không tìm thấy khách hàng nào với số điện thoại này. Bạn có muốn thêm khách hàng mới không?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                handleAddCustomer();
            }
        }
    }

    private void handleFixCustomer() {
        if (customer == null) {
            ControllerUtils.showAlert(Alert.AlertType.WARNING, "Cảnh báo",
                    "Vui lòng chọn khách hàng trước khi sửa thông tin.");
            return;
        }

        Stage currentStage = (Stage) root.getScene().getWindow();
        Stage popupStage = StaffFactory.getInstance().showPopup("customer", currentStage, true, customer);

        if (popupStage != null) {
            popupStage.setOnHiding(event -> {
                if (customer != null) {
                    Customer updatedCustomer = CustomerDAO.findById(customer.getCustomerId());
                    if (updatedCustomer != null) {
                        customer = updatedCustomer;
                        setupCustomerInfo();
                    }
                }
            });
        }
    }

    private void handleConfirmPayment() throws IOException {
        if (customer == null) {
            ControllerUtils.showAlert(Alert.AlertType.WARNING, "Cảnh báo",
                    "Vui lòng chọn khách hàng trước khi thanh toán.");
            return;
        }

        if (currentOrder == null) {
            ControllerUtils.showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Đơn hàng không hợp lệ.");
            return;
        }

        if (currentOrder.getTotalPrice() <= 0) {
            ControllerUtils.showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Tổng tiền không hợp lệ.");
            return;
        }

        if (ControllerUtils.showConfirmationAndWait("Xác nhận thanh toán",
                "Bạn có chắc chắn muốn thanh toán đơn hàng này không?")) {
            currentOrder.setCustomerId(customer.getCustomerId());
            if (currentDiscount != null) {
                currentOrder.setDiscountId(currentDiscount.getDiscountId());
            }
            int orderId = OrderDAO.insert(currentOrder);
            if (orderId == 0) {
                ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể lưu đơn hàng vào cơ sở dữ liệu.");
                return;
            }

            ArrayList<OrderDetail> details = null;

            for (OrderDetail detail : orderDetails) {
                detail.setOrderId(orderId);
                OrderDetailDAO.insert(detail);

                if ("pet".equals(detail.getItemType())) {
                    Pet pet = petMap.get(detail.getItemId());
                    if (pet != null) {
                        pet.setIsSold(true);
                        PetDAO.update(pet);
                    }
                } else if ("product".equals(detail.getItemType())) {
                    Product product = productMap.get(detail.getItemId());
                    if (product != null) {
                        product.setStock(product.getStock() - detail.getQuantity());
                        ProductDAO.update(product);
                    }
                }
                details.add(detail);
            }

            String fileName = "hoadon_" + orderId + ".pdf";
            HoaDonPDF.xuatHoaDonPDF(fileName, currentOrder, orderDetails, productMap, petMap, currentDiscount);

            Tab currentTab = SessionManager.getCurrentTab();
            if (currentTab != null) {
                SessionManager.setTabToRemove(currentTab);
            }

            SessionManager.clearCurrentOrder();
            handleBack();
        }
    }

    private void handleBack() {
        Stage currentStage = (Stage) root.getScene().getWindow();
        StaffFactory.getInstance().switchContent("order", currentStage);
    }

    private void setupCustomerInfo() {
        if (customer != null) {
            customerIdField.setText(String.valueOf(customer.getCustomerId()));
            customerNameField.setText(customer.getFullName());
            customerPhoneField.setText(customer.getPhone());

            fixCustomerbtn.setDisable(false);

            customerFormGrid.setVisible(true);
            customerInfoBox.setVisible(true);
        } else {
            customerIdField.clear();
            customerNameField.clear();
            customerPhoneField.clear();

            fixCustomerbtn.setDisable(true);

            customerFormGrid.setVisible(false);
            customerInfoBox.setVisible(false);
        }
    }

    private void setupStaffInfo() {
        SessionManager.setCurrentStaff(StaffDAO.findByUserId(SessionManager.getCurrentUser().getUserId()));
        staff = SessionManager.getCurrentStaff();
        if (staff != null) {
            staffIdField.setText(String.valueOf(staff.getStaffId()));
            staffNameField.setText(staff.getFullName());
            orderTimeField.setText(ControllerUtils.getCurrentDateTime());
        } else {
            staffIdField.clear();
            staffNameField.clear();
            orderTimeField.clear();
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

    private void loadOrderDetails() {
        if (currentOrder != null && orderDetails != null) {
            orderDetailsBox.getChildren().clear();
            for (OrderDetail detail : orderDetails) {
                Node itemNode = createItemNode(detail, productMap, petMap);
                if (itemNode != null) {
                    orderDetailsBox.getChildren().add(itemNode);
                }
            }
            double totalAmount = currentOrder.getTotalPrice();
            double discountAmount = 0;
            if (currentDiscount != null) {
                voucherCodeField.setText(currentDiscount.getCode());
                if (currentDiscount.getDiscountType().equals("percent")) {
                    discountAmount = totalAmount * (currentDiscount.getValue() / 100.0);
                    if (currentDiscount.getMaxDiscountValue() > 0) {
                        discountAmount = Math.min(discountAmount, currentDiscount.getMaxDiscountValue());
                    }
                } else {
                    discountAmount = currentDiscount.getValue();
                }
            }
            totalAmountLabel.setText(String.format("%,.0f VNĐ", totalAmount));
            discountAmountLabel.setText(String.format("%,.0f VNĐ", discountAmount));
            finalAmountLabel.setText(String.format("%,.0f VNĐ", totalAmount - discountAmount));
        }
    }

    private void handleClearSearch() {
        searchCustomerField.clear();
        customer = null;
        setupCustomerInfo();
    }

    private void setupButtonActions() {
        addCustomerBtn.setOnAction(event -> handleAddCustomer());
        fixCustomerbtn.setOnAction(event -> handleFixCustomer());
        paymentBtn.setOnAction(event -> {
            try {
                handleConfirmPayment();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        backbtn.setOnAction(event -> handleBack());
        searchIcon.setOnMouseClicked(event -> handleSearchCustomer());
        clearSearchIcon.setOnMouseClicked(event -> handleClearSearch());
    }

    public void setOrderData(Order order, ArrayList<OrderDetail> details, Map<Integer, Product> products,
            Map<Integer, Pet> pets, Discount discount) {
        this.currentOrder = order;
        this.orderDetails = details;
        this.productMap = products;
        this.petMap = pets;
        this.currentDiscount = discount;

        loadOrderDetails();
    }
}

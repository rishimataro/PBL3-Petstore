package com.store.app.petstore.Controllers.Staff;

import com.store.app.petstore.Controllers.ControllerUtils;
import com.store.app.petstore.DAO.CustomerDAO;
import com.store.app.petstore.DAO.StaffDAO;
import com.store.app.petstore.Models.Entities.Customer;
import com.store.app.petstore.Models.Entities.Staff;
import com.store.app.petstore.Sessions.SessionManager;
import com.store.app.petstore.Views.ViewFactory;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.swing.text.View;
import java.net.URL;
import java.util.ResourceBundle;

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

    private CustomerDAO customerDAO = new CustomerDAO();
    private StaffDAO staffDAO = new StaffDAO();

    private SessionManager sessionManager;
    private Customer customer;
    private Staff staff;
    private boolean isNewCustomer = true;
    private int currentCustomerId;
    private int currentStaffId;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupPaymentInfo();
        setupStaffInfo();

        setupButtonActions();
    }

    private void handleAddCustomer() {
        Stage currentStage = (Stage) root.getScene().getWindow();
        Stage popupStage = ViewFactory.getInstance().showPopup("customer", currentStage, true, customer);

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

    private void handleFixCustomer() {
        if (customer == null) {
            ControllerUtils.showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn khách hàng trước khi sửa thông tin.");
            return;
        }

        Stage currentStage = (Stage) root.getScene().getWindow();
        Stage popupStage = ViewFactory.getInstance().showPopup("customer", currentStage, true, customer);
        
        if (popupStage != null) {
            popupStage.setOnHiding(event -> {
                if (customer != null) {
                    Customer updatedCustomer = customerDAO.findById(customer.getCustomerId());
                    if (updatedCustomer != null) {
                        customer = updatedCustomer;
                        setupCustomerInfo();
                    }
                }
            });
        }
    }

    private void handleConfirmPayment() {

    }

    private void handleBack() {
        Stage currentStage = (Stage) root.getScene().getWindow();
        ViewFactory.getInstance().switchContent("order", currentStage);
    }

    private void handleSearchCustomer() {

    }

    private void setupCustomerInfo() {
        if (customer != null) {
            // Hiển thị thông tin khách hàng
            customerIdField.setText(String.valueOf(customer.getCustomerId()));
            customerNameField.setText(customer.getFullName());
            customerPhoneField.setText(customer.getPhone());
            
            // Hiển thị thông tin khác
            customerInfoLabel.setText("Thông tin khách hàng");
            otherInfoLabel.setText("Thông tin khác");
            
            // Enable các nút liên quan đến customer
            fixCustomerbtn.setDisable(false);
            
            // Hiển thị form thông tin khách hàng
            customerFormGrid.setVisible(true);
            customerInfoBox.setVisible(true);
        } else {
            // Xóa thông tin khách hàng
            customerIdField.clear();
            customerNameField.clear();
            customerPhoneField.clear();
            
            // Ẩn thông tin khác
            customerInfoLabel.setText("");
            otherInfoLabel.setText("");
            
            // Disable các nút liên quan đến customer
            fixCustomerbtn.setDisable(true);
            
            // Ẩn form thông tin khách hàng
            customerFormGrid.setVisible(false);
            customerInfoBox.setVisible(false);
        }
    }

    private void setupStaffInfo() {

    }

    private void setupPaymentInfo() {

    }

    private void handleClearSearch() {

    }

    private void setupButtonActions() {
        addCustomerBtn.setOnAction(event -> handleAddCustomer());
        fixCustomerbtn.setOnAction(event -> handleFixCustomer());
        paymentBtn.setOnAction(event -> handleConfirmPayment());
        backbtn.setOnAction(event -> handleBack());
        searchIcon.setOnMouseClicked(event -> handleSearchCustomer());
        clearSearchIcon.setOnMouseClicked(event -> handleClearSearch());
    }
}

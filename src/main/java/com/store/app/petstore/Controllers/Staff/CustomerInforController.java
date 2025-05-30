package com.store.app.petstore.Controllers.Staff;

import com.store.app.petstore.Controllers.ControllerUtils;
import com.store.app.petstore.DAO.CustomerDAO;
import com.store.app.petstore.Models.Entities.Customer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

// Staff
public class CustomerInforController implements Initializable {

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnFix;

    @FXML
    private Button btnSave;

    @FXML
    private TextField txtCustomerId;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtPhone;

    private int idCustomerCurrent;
    private boolean isNewCustomer = true;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtCustomerId.setDisable(true);
        txtName.setDisable(true);
        txtPhone.setDisable(true);

        setupButtonActions();
        setupInitialState();
    }

    private void setupInitialState() {
        btnAdd.setDisable(false);
        btnFix.setDisable(true);
        btnSave.setDisable(true);
    }

    public void setCustomer(Customer customer) {
        if (customer != null) {
            isNewCustomer = false;
            idCustomerCurrent = customer.getCustomerId();
            txtCustomerId.setText(String.valueOf(customer.getCustomerId()));
            txtName.setText(customer.getFullName());
            txtPhone.setText(customer.getPhone());

            btnAdd.setDisable(true);
            btnFix.setDisable(false);
            btnSave.setDisable(true);
        } else {
            clearFields();
            setupInitialState();
        }
    }

    private void clearFields() {
        txtCustomerId.clear();
        txtName.clear();
        txtPhone.clear();
        isNewCustomer = true;
    }

    private int getNextCustomerId() {
        ArrayList<Customer> customers = CustomerDAO.findAll();
        if (customers == null || customers.isEmpty()) {
            return 1;
        }
        return customers.stream()
                .mapToInt(Customer::getCustomerId)
                .max()
                .orElse(0) + 1;
    }

    private void handleAdd() {
        txtName.setDisable(false);
        txtPhone.setDisable(false);
        btnAdd.setDisable(true);
        btnSave.setDisable(false);
        clearFields();

        int nextId = getNextCustomerId();
        txtCustomerId.setText(String.valueOf(nextId));
    }

    @FXML
    void handleSaveCustomer() {
        String name = txtName.getText();
        String phone = txtPhone.getText();

        if (name.isEmpty() || phone.isEmpty()) {
            ControllerUtils.showAlert(AlertType.ERROR, "Lỗi", "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        try {
            Customer customer = new Customer();
            customer.setFullName(name);
            customer.setPhone(phone);

            if (isNewCustomer) {
                int result = CustomerDAO.insert(customer);
                if (result > 0) {
                    idCustomerCurrent = result;
                    isNewCustomer = false;
                    ControllerUtils.showAlert(AlertType.INFORMATION, "Thành công", "Thêm khách hàng thành công!");

                    btnAdd.setDisable(false);
                    btnFix.setDisable(false);
                    btnSave.setDisable(true);
                    txtName.setDisable(true);
                    txtPhone.setDisable(true);
                } else {
                    ControllerUtils.showAlert(AlertType.ERROR, "Lỗi", "Không thể thêm khách hàng!");
                }
            } else {
                customer.setCustomerId(idCustomerCurrent);
                if (CustomerDAO.update(customer) > 0) {
                    ControllerUtils.showAlert(AlertType.INFORMATION, "Thành công", "Cập nhật khách hàng thành công!");
                    txtName.setDisable(true);
                    txtPhone.setDisable(true);
                    btnAdd.setDisable(false);
                    btnFix.setDisable(false);
                    btnSave.setDisable(true);
                } else {
                    ControllerUtils.showAlert(AlertType.ERROR, "Lỗi", "Không thể cập nhật khách hàng!");
                }
            }
            clearFields();
        } catch (Exception e) {
            ControllerUtils.showAlert(AlertType.ERROR, "Lỗi", "Không thể lưu thông tin khách hàng!");
        }
    }

    private void handleFix() {
        txtName.setDisable(false);
        txtPhone.setDisable(false);
        btnAdd.setDisable(true);
        btnFix.setDisable(true);
        btnSave.setDisable(false);
    }

    private void setupButtonActions() {
        btnAdd.setOnAction(event -> handleAdd());
        btnFix.setOnAction(event -> handleFix());
        btnSave.setOnAction(event -> handleSaveCustomer());
    }
}

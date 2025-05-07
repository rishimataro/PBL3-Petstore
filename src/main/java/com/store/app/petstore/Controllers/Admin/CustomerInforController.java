package com.store.app.petstore.Controllers.Admin;

import com.store.app.petstore.DAO.CustomerDAO;
import com.store.app.petstore.Models.Entities.Customer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;
import com.store.app.petstore.Controllers.ControllerUtils;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.ArrayList;

public class CustomerInforController implements Initializable {

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnDelete;

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
    private CustomerDAO customerDAO = new CustomerDAO();
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
        btnDelete.setDisable(true);
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
            btnDelete.setDisable(false);
            btnSave.setDisable(true);
        } else {
            clearFields();
            setupInitialState();
        }
    }

    public void setCustomerData(Customer customer) {
        if (customer != null) {
            // Set các trường dữ liệu từ customer vào form
            txtCustomerId.setText(String.valueOf(customer.getCustomerId()));
            txtName.setText(customer.getFullName());
            txtPhone.setText(customer.getPhone());
            // Thêm các trường khác nếu cần
        }
    }

    private void clearFields() {
        txtCustomerId.clear();
        txtName.clear();
        txtPhone.clear();
        isNewCustomer = true;
    }

    private int getNextCustomerId() {
        ArrayList<Customer> customers = customerDAO.findAll();
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

    private void handleDelete() {
        if (idCustomerCurrent > 0) {
            Customer customer = new Customer();
            customer.setCustomerId(idCustomerCurrent);

            if (customerDAO.delete(customer) > 0) {
                if(ControllerUtils.showConfirmationAndWait("Xác nhận", "Bạn có chắc chắn muốn xóa khách hàng này không?")) {
                    ControllerUtils.showAlert(AlertType.INFORMATION, "Thành công", "Xóa khách hàng thành công!");
                    clearFields();
                    setupInitialState();
                } else {
                    ControllerUtils.showAlert(AlertType.INFORMATION, "Thông báo", "Đã hủy xóa khách hàng!");
                }
            } else {
                ControllerUtils.showAlert(AlertType.ERROR, "Lỗi", "Không thể xóa khách hàng!");
            }
        }
    }

    @FXML
    void handleSaveCustomer(ActionEvent event) {
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
                int result = customerDAO.insert(customer);
                if (result > 0) {
                    idCustomerCurrent = result;
                    isNewCustomer = false;
                    ControllerUtils.showAlert(AlertType.INFORMATION, "Thành công", "Thêm khách hàng thành công!");

                    btnAdd.setDisable(false);
                    btnDelete.setDisable(false);
                    btnFix.setDisable(false);
                    btnSave.setDisable(true);
                    txtName.setDisable(true);
                    txtPhone.setDisable(true);
                } else {
                    ControllerUtils.showAlert(AlertType.ERROR, "Lỗi", "Không thể thêm khách hàng!");
                }
            } else {
                customer.setCustomerId(idCustomerCurrent);
                if (customerDAO.update(customer) > 0) {
                    ControllerUtils.showAlert(AlertType.INFORMATION, "Thành công", "Cập nhật khách hàng thành công!");
                    txtName.setDisable(true);
                    txtPhone.setDisable(true);
                    btnAdd.setDisable(false);
                    btnDelete.setDisable(false);
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
        btnDelete.setDisable(true);
        btnSave.setDisable(false);
    }

    private void handleSave() {
        String name = txtName.getText();
        String phone = txtPhone.getText();

        if (name.isEmpty() || phone.isEmpty()) {
            ControllerUtils.showAlert(AlertType.ERROR, "Lỗi", "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        Customer customer = new Customer();
        customer.setFullName(name);
        customer.setPhone(phone);

        if (isNewCustomer) {
            int result = customerDAO.insert(customer);
            if (result > 0) {
                idCustomerCurrent = result;
                isNewCustomer = false;
                ControllerUtils.showAlert(AlertType.INFORMATION, "Thành công", "Thêm khách hàng thành công!");

                btnAdd.setDisable(false);
                btnDelete.setDisable(false);
                btnFix.setDisable(false);
                btnSave.setDisable(true);
                txtName.setDisable(true);
                txtPhone.setDisable(true);
            } else {
                ControllerUtils.showAlert(AlertType.ERROR, "Lỗi", "Không thể thêm khách hàng!");
            }
        } else {
            customer.setCustomerId(idCustomerCurrent);
            if (customerDAO.update(customer) > 0) {
                ControllerUtils.showAlert(AlertType.INFORMATION, "Thành công", "Cập nhật khách hàng thành công!");
                txtName.setDisable(true);
                txtPhone.setDisable(true);
                btnAdd.setDisable(false);
                btnDelete.setDisable(false);
                btnFix.setDisable(false);
                btnSave.setDisable(true);
            } else {
                ControllerUtils.showAlert(AlertType.ERROR, "Lỗi", "Không thể cập nhật khách hàng!");
            }
        }
    }

    private void setupButtonActions() {
        btnAdd.setOnAction(event -> handleAdd());
        btnDelete.setOnAction(event -> handleDelete());
        btnFix.setOnAction(event -> handleFix());
        btnSave.setOnAction(event -> handleSaveCustomer(null));
    }
}
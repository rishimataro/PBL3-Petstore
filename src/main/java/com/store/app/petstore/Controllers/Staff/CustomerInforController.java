package com.store.app.petstore.Controllers.Staff;

import com.store.app.petstore.DAO.CustomerDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

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
    CustomerDAO customerDAO = new CustomerDAO();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtCustomerId.setDisable(true);
        txtName.setDisable(true);
        txtPhone.setDisable(true);

        setupButtonActions();
    }

    private void handleAdd() {

    }

    private void handleDelete() {

    }

    private void handleFix() {

    }

    private void handleSave() {

    }

    private void setupButtonActions() {
        btnAdd.setOnAction(event -> handleAdd());
        btnDelete.setOnAction(event -> handleDelete());
        btnFix.setOnAction(event -> handleFix());
        btnSave.setOnAction(event -> handleSave());
    }
}

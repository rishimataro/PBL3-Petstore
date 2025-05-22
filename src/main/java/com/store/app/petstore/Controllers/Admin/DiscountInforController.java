package com.store.app.petstore.Controllers.Admin;

import com.store.app.petstore.DAO.DiscountDAO;
import com.store.app.petstore.DAO.UserDAO;
import com.store.app.petstore.Models.Entities.Discount;
import com.store.app.petstore.Models.Entities.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class DiscountInforController implements Initializable {

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnFix;

    @FXML
    private Button btnSave;

    @FXML
    private ChoiceBox<String> cbType;

    @FXML
    private AnchorPane discountInforPopup;

    @FXML
    private DatePicker dpEndDate;

    @FXML
    private DatePicker dpStartDate;

    @FXML
    private TextField txtCode;

    @FXML
    private TextField txtDiscountId;

    @FXML
    private TextField txtDiscountValue;

    @FXML
    private TextField txtMaxValue;

    private int idDiscountCurrent;
    private boolean isNewDiscount = true;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtDiscountId.setDisable(true);
        txtCode.setDisable(true);
        txtDiscountValue.setDisable(true);
        txtMaxValue.setDisable(true);
        dpStartDate.setDisable(true);
        dpEndDate.setDisable(true);

        setupChoiceBox();
        setupButtonActions();
        setupInitialState();
    }

    private void setupChoiceBox() {
        cbType.getItems().addAll("Phần trăm", "Cố định");
        cbType.setValue("Cố định");
    }

    private void setupButtonActions() {

    }

    private void setupInitialState() {

    }

    private void handleAdd() {

    }

    private void handleDelete() {

    }

    private void handleFix() {

    }

    private void handleSave() {

    }

    private void handleAdmin() {

    }

    private void handleStaff() {

    }

    private void handlePassword() {

    }

    private void setDiscountInfor(Discount discount) {

    }

    private void clearFields() {
        txtDiscountId.clear();
        txtCode.clear();
        txtDiscountValue.clear();
        txtMaxValue.clear();
        dpStartDate.setValue(null);
        dpEndDate.setValue(null);
    }

    private int getNextDiscountId() {
        return 0;
    }
}

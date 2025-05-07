package com.store.app.petstore.Controllers.Admin;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class StaffInforController implements Initializable {

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnFix;

    @FXML
    private FontAwesomeIconView btnRefresh;

    @FXML
    private Button btnSave;

    @FXML
    private ChoiceBox<String> cbPosition;

    @FXML
    private ChoiceBox<String> cbStatus;

    @FXML
    private AnchorPane staffInforPopup;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtPhone;

    @FXML
    private TextField txtSalary;

    @FXML
    private TextField txtStaffId;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}

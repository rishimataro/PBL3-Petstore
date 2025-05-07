package com.store.app.petstore.Controllers.Admin;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class ProductInforController implements Initializable {

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
    private AnchorPane productInforPopup;

    @FXML
    private TextField txtCategory;

    @FXML
    private TextArea txtDescription;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtPrice;

    @FXML
    private TextField txtProductId;

    @FXML
    private TextField txtQuantity;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}

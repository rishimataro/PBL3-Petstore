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

public class PetInforController implements Initializable {

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnCat;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnDog;

    @FXML
    private Button btnFix;

    @FXML
    private FontAwesomeIconView btnRefresh;

    @FXML
    private Button btnSave;

    @FXML
    private AnchorPane petInforPopup;

    @FXML
    private TextField txtAge;

    @FXML
    private TextField txtBreed;

    @FXML
    private TextArea txtDescription;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtPetId;

    @FXML
    private TextField txtPrice;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }


}

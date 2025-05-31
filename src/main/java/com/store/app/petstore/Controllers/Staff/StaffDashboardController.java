package com.store.app.petstore.Controllers.Staff;

import com.store.app.petstore.Models.Entities.Staff;
import com.store.app.petstore.Sessions.SessionManager;
import com.store.app.petstore.Views.ViewFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class StaffDashboardController implements Initializable {
    @FXML
    private VBox AccountCard;

    @FXML
    private VBox AddCustomerCard;

    @FXML
    private VBox BillHistoryCard;

    @FXML
    private VBox OrderCard;

    @FXML
    private Button btnLogout;

    @FXML
    private AnchorPane root;

    @FXML
    private Label staffNameLabel;

    private Staff currentStaff;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentStaff = SessionManager.getCurrentStaff();
        setupStaffname();
        setMenuCard();
    }

    private void setupStaffname() {
        if (currentStaff != null) {
            staffNameLabel.setText(currentStaff.getFullName());
        } else {
            staffNameLabel.setText("Chưa cập nhật thông tin");
        }
    }

    private void setMenuCard() {
        AccountCard.setOnMouseClicked(event -> {
            Stage currentStage = (Stage) root.getScene().getWindow();
            ViewFactory.getInstance().switchContent("profile", currentStage);
        });

        AddCustomerCard.setOnMouseClicked(event -> {
            Stage currentStage = (Stage) root.getScene().getWindow();
            ViewFactory.getInstance().showPopup("customer", currentStage, true);
        });

        BillHistoryCard.setOnMouseClicked(event -> {
            Stage currentStage = (Stage) root.getScene().getWindow();
            ViewFactory.getInstance().switchContent("billhistory", currentStage);
        });

        OrderCard.setOnMouseClicked(event -> {
            Stage currentStage = (Stage) root.getScene().getWindow();
            ViewFactory.getInstance().switchContent("order", currentStage);
        });

        btnLogout.setOnMouseClicked(event -> {
            SessionManager.clear();
            Stage currentStage = (Stage) root.getScene().getWindow();
            ViewFactory.getInstance().switchContent("login", currentStage);
        });
    }
}

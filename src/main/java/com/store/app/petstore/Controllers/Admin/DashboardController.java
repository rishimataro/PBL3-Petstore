package com.store.app.petstore.Controllers.Admin;

import com.store.app.petstore.Controllers.ControllerUtils;
import com.store.app.petstore.Sessions.SessionManager;
import com.store.app.petstore.Views.AdminFactory;
import com.store.app.petstore.Views.ViewFactory;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    @FXML
    private Button accountsButton;

    @FXML
    private Button employeesButton;

    @FXML
    private Button customersButton;

    @FXML
    private Button petsButton;

    @FXML
    private Button productsButton;

    @FXML
    private Button promotionsButton;

    @FXML
    private Button revenueReportButton;

    @FXML
    private Button salesReportButton;

    @FXML
    private Button inventoryReportButton;

    @FXML
    private Button customerReportButton;

    @FXML
    private Button logoutButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupButtons();
    }

    private void setupButtons() {
        accountsButton.setOnAction(e -> navigateTo("usermanagement"));
        employeesButton.setOnAction(e -> navigateTo("staffmanagement"));
        customersButton.setOnAction(e -> navigateTo("customermanagement"));
        petsButton.setOnAction(e -> navigateTo("petmanagement"));
        productsButton.setOnAction(e -> navigateTo("productmanagement"));
        promotionsButton.setOnAction(e -> navigateTo("discountmanagement"));

        revenueReportButton.setOnAction(e -> navigateTo("revenue"));
        salesReportButton.setOnAction(e -> navigateTo("bestselling"));
        inventoryReportButton.setOnAction(e -> navigateTo("inventory"));
        customerReportButton.setOnAction(e -> navigateTo("customerreport"));

        logoutButton.setOnAction(e -> handleLogout());
    }

    private void navigateTo(String viewName) {
        Stage currentStage = (Stage) logoutButton.getScene().getWindow();
        AdminFactory.getInstance().switchContent(viewName, currentStage);
    }

    private void handleLogout() {
        Stage currentStage = (Stage) logoutButton.getScene().getWindow();
        if (ControllerUtils.showConfirmationAndWait("Đăng xuất",
                "Bạn có chắc chắn muốn đăng xuất không?\nNhấn OK để xác nhận.")) {
            SessionManager.clear();
            ViewFactory.getInstance().switchContent("login", currentStage);
        }
    }
}

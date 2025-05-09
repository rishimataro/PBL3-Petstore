package com.store.app.petstore.Views;

import com.store.app.petstore.Controllers.Admin.Statistic.BestSellerController;
import com.store.app.petstore.Controllers.Admin.Statistic.OverViewController;
import com.store.app.petstore.Controllers.Admin.Statistic.RevenueController;
import com.store.app.petstore.Controllers.Staff.CustomerInforController;
import com.store.app.petstore.Models.Entities.Customer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

public class ViewFactory {
    private static final String STAFF_FXML = "";
    private static ViewFactory instance;
    private final String LOGIN_FXML = "/FXML/Login.fxml";
    private final String DASHBOAR_FXML = "/FXML/Staff/StaffDashboard.fxml";
    private final String FORGOT_PASSWORD_FXML = "/FXML/ForgotPassword.fxml";
    private final String ORDER_FXML = "/FXML/Staff/Order.fxml";
    private final String PERSONAL_INFOR_FXML = "/FXML/Staff/PersonalInfo.fxml";
    private final String BILL_HISTORY_FXML = "/FXML/Staff/BillHistory.fxml";
    private final String PAYMENT_FXML = "/FXML/Staff/Payment.fxml";

    //popup
    private final String CUSTOMER_POPUP = "/FXML/Staff/CustomerInfor.fxml";

    private final Map<String, AnchorPane> views;
    private final Map<String, Stage> stages;

    ViewFactory() {
        this.views = new HashMap<>();
        this.stages = new HashMap<>();
    }

    public static synchronized ViewFactory getInstance() {
        if (instance == null) {
            instance = new ViewFactory();
        }
        return instance;
    }

    public void showWindow(String fxmlName) {
        Stage stage = new Stage();
        Parent root = null;
        try {
            switch (fxmlName.toLowerCase()) {
                case "login":
                    root = loadFXML(LOGIN_FXML);
                    stage.setTitle("Login");
                    break;
                case "dashboard":
                    root = loadFXML(DASHBOAR_FXML);
                    stage.setTitle("Staff Dashboard");
                    break;
                case "forgotpassword":
                    root = loadFXML(FORGOT_PASSWORD_FXML);
                    stage.setTitle("Forgot Password");
                    break;
                case "order":
                    root = loadFXML(ORDER_FXML);
                    stage.setTitle("Order Management");
                    break;
                case "profile":
                    root = loadFXML(PERSONAL_INFOR_FXML);
                    stage.setTitle("Personal Information");
                    break;
                case "billhistory":
                    root = loadFXML(BILL_HISTORY_FXML);
                    stage.setTitle("Bill History");
                    break;
                case "payment":
                    root = loadFXML(PAYMENT_FXML);
                    stage.setTitle("Payment");
                    break;
                case "overview":
                    OverViewController overViewController = new OverViewController();
                    overViewController.show(stage);
                    return;
                case "bestseller":
                    BestSellerController bestSellerController = new BestSellerController();
                    bestSellerController.show(stage);
                    return;
                case "revenue":
                    RevenueController revenueController = new RevenueController();
                default:
                    System.err.println("Unknown FXML file: " + fxmlName);
                    return;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void clearCache() {
        views.clear();
        stages.clear();
    }

    private Parent loadFXML(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        return loader.load();
    }

    public Stage showPopup(String popupName, Stage parentStage, boolean isModal) {
        String fxmlPath;

        switch (popupName.toLowerCase()) {
            case "customer":
                fxmlPath = CUSTOMER_POPUP;
                break;
            // Thêm các popup khác nếu có
            default:
                System.err.println("Unknown popup: " + popupName);
                return null;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.setTitle("Popup - " + popupName);
            popupStage.setScene(new Scene(root));
            popupStage.setResizable(false);
            popupStage.setAlwaysOnTop(true);

            if (parentStage != null) {
                popupStage.initOwner(parentStage);
                if (isModal) {
                    popupStage.initModality(Modality.WINDOW_MODAL);
                }
            }

            popupStage.show();
            stages.put(popupName, popupStage);
            return popupStage;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> Stage showPopup(String popupName, Stage parentStage, boolean isModal, T data) {
        String fxmlPath;
        switch (popupName.toLowerCase()) {
            case "customer":
                fxmlPath = CUSTOMER_POPUP;
                break;
            // Thêm các popup khác ở đây
            default:
                System.err.println("Unknown popup: " + popupName);
                return null;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Get the controller and set the data
            Object controller = loader.getController();
            if (controller != null) {
                if (data instanceof Customer && controller instanceof CustomerInforController) {
                    ((CustomerInforController) controller).setCustomer((Customer) data);
                }
                // Thêm các trường hợp khác ở đây
            }

            Stage popupStage = new Stage();
            popupStage.setTitle("Popup - " + popupName);
            popupStage.setScene(new Scene(root));
            popupStage.setResizable(false);
            popupStage.setAlwaysOnTop(true);

            if (parentStage != null) {
                popupStage.initOwner(parentStage);
                if (isModal) {
                    popupStage.initModality(Modality.WINDOW_MODAL);
                }
            }

            popupStage.show();
            stages.put(popupName, popupStage);
            return popupStage;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void switchContent(String fxmlName, Stage currentStage) {
        Parent root = null;
        try {
            switch (fxmlName.toLowerCase()) {
                case "login":
                    root = loadFXML(LOGIN_FXML);
                    currentStage.setTitle("Login");
                    break;
                case "dashboard":
                    root = loadFXML(DASHBOAR_FXML);
                    currentStage.setTitle("Staff Dashboard");
                    break;
                case "forgotpassword":
                    root = loadFXML(FORGOT_PASSWORD_FXML);
                    currentStage.setTitle("Forgot Password");
                    break;
                case "order":
                    root = loadFXML(ORDER_FXML);
                    currentStage.setTitle("Order Management");
                    break;
                case "profile":
                    root = loadFXML(PERSONAL_INFOR_FXML);
                    currentStage.setTitle("Personal Information");
                    break;
                case "billhistory":
                    root = loadFXML(BILL_HISTORY_FXML);
                    currentStage.setTitle("Bill History");
                    break;
                case "payment":
                    root = loadFXML(PAYMENT_FXML);
                    currentStage.setTitle("Payment");
                    break;
                default:
                    System.err.println("Unknown FXML file: " + fxmlName);
                    return;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Scene scene = new Scene(root);
        currentStage.setScene(scene);
    }
}

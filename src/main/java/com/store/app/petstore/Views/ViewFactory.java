package com.store.app.petstore.Views;

import com.store.app.petstore.Controllers.LoginController;
import com.store.app.petstore.Controllers.Staff.StaffController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

public class ViewFactory {
    private static ViewFactory instance;
    private final String LOGIN_FXML = "/FXML/Login.fxml";
    private final String DASHBOAR_FXML = "/FXML/Staff/StaffDashboard.fxml";
    private final String FORGOT_PASSWORD_FXML = "/FXML/ForgotPassword.fxml";
    private final String ORDER_FXML = "/FXML/Staff/Order.fxml";
    private final String PERSONAL_INFOR_FXML = "/FXML/Staff/PersonalInfo.fxml";
    private final String BILL_HISTORY_FXML = "/FXML/Staff/BillHistory.fxml";
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
    
    public AnchorPane getView(String viewName) {
        if (!views.containsKey(viewName)) {
            try {
                String fxmlPath = getFxmlPath(viewName);
                AnchorPane view = new FXMLLoader(getClass().getResource(fxmlPath)).load();
                views.put(viewName, view);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return views.get(viewName);
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
    
    public void closeWindow(String windowName) {
        Stage stage = stages.get(windowName);
        if (stage != null) {
            stage.close();
            stages.remove(windowName);
        }
    }
    
    private String getFxmlPath(String viewName) {
        switch (viewName.toLowerCase()) {
            case "dashboard":
                return DASHBOAR_FXML;
            case "login":
                return LOGIN_FXML;
            case "profile":
                return PERSONAL_INFOR_FXML;
            case "order":
                return ORDER_FXML;
            case "billhistory":
                return BILL_HISTORY_FXML;
            case "forgotpassword":
                return FORGOT_PASSWORD_FXML;
            case "addcustomer":
                return CUSTOMER_POPUP;
            default:
                throw new IllegalArgumentException("Unknown view: " + viewName);
        }
    }
    
    private String getWindowTitle(String windowName) {
        switch (windowName.toLowerCase()) {
            case "dashboard":
                return "Staff Dashboard";
            case "login":
                return "Login";
            case "profile":
                return "Personal Information";
            case "order":
                return "Order Management";
            case "forgotpassword":
                return "Forgot Password";
            case "billhistory":
                return "Bill History";
            default:
                return windowName;
        }
    }
    
    public void clearCache() {
        views.clear();
        stages.clear();
    }

    private Parent loadFXML(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        return loader.load();
    }

    public void showPopup(String fxmlName, Stage popupStage) {
        Parent root = null;
        try {
            switch (fxmlName.toLowerCase()) {
                case "customerpopup":
                    root = loadFXML(CUSTOMER_POPUP);
                    popupStage.setTitle("Thêm khách hàng");
                    break;
                default:
                    System.err.println("Unknown popup FXML file: " + fxmlName);
                    return;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Scene scene = new Scene(root);
        popupStage.setScene(scene);
        popupStage.initStyle(StageStyle.DECORATED);
        popupStage.show();
    }

    public void hidePopup() {
        for (Stage stage : stages.values()) {
            if (stage.isShowing()) {
                stage.hide();
            }
        }
    }
}

package com.store.app.petstore.Views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

public class AdminView {
    private static StaffView instance;
    private final String LOGIN_FXML = "/FXML/Login.fxml";
    private final String ADMIN_FXML = "/FXML/Admin/AdminDashboard.fxml";
    private final String FORGOT_PASSWORD_FXML = "/FXML/ForgotPassword.fxml";
    private final String PET_MANAGEMENT_FXML = "/FXML/Admin/PetManagement.fxml";
    private final String CUSTOMER_MANAGEMENT_FXML = "/FXML/Admin/CustomerManagement.fxml";
    private final String DISCOUNT_MANAGEMENT_FXML = "/FXML/Admin/DiscountManagement.fxml";
    private final String INVOICE_MANAGEMENT_FXML = "/FXML/Admin/InvoiceManagement.fxml";
    private final String PRODUCT_MANAGEMENT_FXML = "/FXML/Admin/ProductManagement.fxml";
    private final String STAFF_MANAGEMENT_FXML = "/FXML/Admin/StaffManagement.fxml";

    private final Map<String, AnchorPane> views;
    private final Map<String, Stage> stages;

    AdminView() {
        this.views = new HashMap<>();
        this.stages = new HashMap<>();
    }

    public static synchronized StaffView getInstance() {
        if (instance == null) {
            instance = new StaffView();
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
                case "admin":
                    root = loadFXML(ADMIN_FXML);
                    stage.setTitle("Admin Dashboard");
                    break;
                case "petmanagement":
                    root = loadFXML(PET_MANAGEMENT_FXML);
                    stage.setTitle("Pet Management");
                    break;
                case "customermanagement":
                    root = loadFXML(CUSTOMER_MANAGEMENT_FXML);
                    stage.setTitle("Customer Management");
                    break;
                case "discountmanagement":
                    root = loadFXML(DISCOUNT_MANAGEMENT_FXML);
                    stage.setTitle("Discount Management");
                    break;
                case "invoicemanagement":
                    root = loadFXML(INVOICE_MANAGEMENT_FXML);
                    stage.setTitle("Invoice Management");
                    break;
                case "productmanagement":
                    root = loadFXML(PRODUCT_MANAGEMENT_FXML);
                    stage.setTitle("Product Management");
                    break;
                case "staffmanagement":
                    root = loadFXML(STAFF_MANAGEMENT_FXML);
                    stage.setTitle("Staff Management");
                    break;
                case "forgotpassword":
                    root = loadFXML(FORGOT_PASSWORD_FXML);
                    stage.setTitle("Forgot Password");
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
            case "login":
                return LOGIN_FXML;
            case "admin":
                return ADMIN_FXML;
            case "petmanagement":
                return PET_MANAGEMENT_FXML;
            case "customermanagement":
                return CUSTOMER_MANAGEMENT_FXML;
            case "discountmanagement":
                return DISCOUNT_MANAGEMENT_FXML;
            case "invoicemanagement":
                return INVOICE_MANAGEMENT_FXML;
            case "productmanagement":
                return PRODUCT_MANAGEMENT_FXML;
            case "staffmanagement":
                return STAFF_MANAGEMENT_FXML;
            case "forgotpassword":
                return FORGOT_PASSWORD_FXML;
            // Add more cases as needed
            default:
                throw new IllegalArgumentException("Unknown view: " + viewName);
        }
    }

    private String getWindowTitle(String windowName) {
        switch (windowName.toLowerCase()) {
            case "login":
                return "Login";
            case "admin":
                return "Admin Dashboard";
            case "petmanagement":
                return "Pet Management";
            case "customermanagement":
                return "Customer Management";
            case "discountmanagement":
                return "Discount Management";
            case "invoicemanagement":
                return "Invoice Management";
            case "productmanagement":
                return "Product Management";
            case "staffmanagement":
                return "Staff Management";
            case "forgotpassword":
                return "Forgot Password";
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
}

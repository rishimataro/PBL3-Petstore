package com.store.app.petstore.Views;

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

public class AdminFactory {
    private static final String ADMIN_FXML = "";
    private static AdminFactory instance;
    private final String DASHBOARD_FXML = "/FXML/Admin/Dashboard.fxml";
    private final String USERMANAGEMENT_FXML = "/FXML/Admin/UserManagement.fxml";
    private final String CUSTOMERMANAGEMENT_FXML = "/FXML/Admin/CustomerManagement.fxml";
    private final String DISCOUNTMANAGEMENT_FXML = "/FXML/Admin/DiscountManagement.fxml";
    private final String PETMANAGEMENT_FXML = "/FXML/Admin/PetManagement.fxml";
    private final String PRODUCTMANAGEMENT_FXML = "/FXML/Admin/ProductManagement.fxml";
    private final String STAFFMANAGEMENT_FXML = "/FXML/Admin/StaffManagement.fxml";
    private final String INVOICEMANAGEMENT_FXML = "/FXML/Admin/InvoiceManagement.fxml";
    private final String INVENTORY_FXML = "/FXML/Admin/Inventory.fxml";
    private final String CUSTOMERREPORT_FXML = "/FXML/Admin/CustomerReport.fxml";

    private final String USERTINFOR_FXML = "/FXML/Admin/UserInfor.fxml";
    private final String CUSTOMERINFOR_FXML = "/FXML/Admin/CustomerInfor.fxml";
    private final String DISCOUNTINFOR_FXML = "/FXML/Admin/DiscountInfor.fxml";
    private final String PETINFOR_FXML = "/FXML/Admin/PetInfor.fxml";
    private final String PRODUCTINFOR_FXML = "/FXML/Admin/ProductInfor.fxml";
    private final String STAFFINFOR_FXML = "/FXML/Admin/StaffInfor.fxml";

    private final String STATISTIC_BESTSELLER_FXML = "/FXML/Admin/Statistics/BestSeller.fxml";
    private final String STATISTIC_OVERVIEW_FXML = "/FXML/Admin/Statistics/Overview.fxml";
    private final String STATISTIC_REVENUE_FXML = "/FXML/Admin/Statistics/Revenue.fxml";

    private final Map<String, AnchorPane> views;
    private final Map<String, Stage> stages;

    AdminFactory() {
        this.views = new HashMap<>();
        this.stages = new HashMap<>();
    }

    public static synchronized AdminFactory getInstance() {
        if (instance == null) {
            instance = new AdminFactory();
        }
        return instance;
    }

    public void showWindow(String fxmlName) {
        Stage stage = new Stage();
        Parent root = null;
        try {
            switch (fxmlName.toLowerCase()) {
                case "dashboard":
                    root = loadFXML(DASHBOARD_FXML);
                    stage.setTitle("Bảng điều khiển Admin");
                    break;
                case "usermanagement":
                    root = loadFXML(USERMANAGEMENT_FXML);
                    stage.setTitle("Quản lý tài khoản");
                    break;
                case "customermanagement":
                    root = loadFXML(CUSTOMERMANAGEMENT_FXML);
                    stage.setTitle("Quản lý khách hàng");
                    break;
                case "discountmanagement":
                    root = loadFXML(DISCOUNTMANAGEMENT_FXML);
                    stage.setTitle("Quản lý khuyến mãi");
                    break;
                case "petmanagement":
                    root = loadFXML(PETMANAGEMENT_FXML);
                    stage.setTitle("Quản lý thú cưng");
                    break;
                case "productmanagement":
                    root = loadFXML(PRODUCTMANAGEMENT_FXML);
                    stage.setTitle("Quản lý sản phẩm");
                    break;
                case "staffmanagement":
                    root = loadFXML(STAFFMANAGEMENT_FXML);
                    stage.setTitle("Quản lý nhân viên");
                    break;
                case "invoicemanagement":
                    root = loadFXML(INVOICEMANAGEMENT_FXML);
                    stage.setTitle("Quản lý hóa đơn");
                    break;
                case "bestselling":
                    root = loadFXML(STATISTIC_BESTSELLER_FXML);
                    stage.setTitle("Thống kê bán chạy");
                    break;
                case "overview":
                    root = loadFXML(STATISTIC_OVERVIEW_FXML);
                    stage.setTitle("Thống kê tổng quan");
                    break;
                case "revenue":
                    root = loadFXML(STATISTIC_REVENUE_FXML);
                    stage.setTitle("Thống kê doanh thu");
                    break;
                case "inventory":
                    root = loadFXML(INVENTORY_FXML);
                    stage.setTitle("Báo cáo tồn kho");
                    break;
                case "customerreport":
                    root = loadFXML(CUSTOMERREPORT_FXML);
                    stage.setTitle("Báo cáo khách hàng");
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
            case "account":
                fxmlPath = USERTINFOR_FXML;
                break;
            case "customer":
                fxmlPath = CUSTOMERINFOR_FXML;
                break;
            case "discount":
                fxmlPath = DISCOUNTINFOR_FXML;
                break;
            case "pet":
                fxmlPath = PETINFOR_FXML;
                break;
            case "product":
                fxmlPath = PRODUCTINFOR_FXML;
                break;
            case "staff":
                fxmlPath = STAFFINFOR_FXML;
                break;
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
            case "account":
                fxmlPath = USERTINFOR_FXML;
                break;
            case "customer":
                fxmlPath = CUSTOMERINFOR_FXML;
                break;
            case "discount":
                fxmlPath = DISCOUNTINFOR_FXML;
                break;
            case "pet":
                fxmlPath = PETINFOR_FXML;
                break;
            case "product":
                fxmlPath = PRODUCTINFOR_FXML;
                break;
            case "staff":
                fxmlPath = STAFFINFOR_FXML;
                break;
            default:
                System.err.println("Unknown popup: " + popupName);
                return null;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

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
                case "dashboard":
                    root = loadFXML(DASHBOARD_FXML);
                    currentStage.setTitle("Bảng điều khiển Admin");
                    break;
                case "usermanagement":
                    root = loadFXML(USERMANAGEMENT_FXML);
                    currentStage.setTitle("Quản lý tài khoản");
                    break;
                case "customermanagement":
                    root = loadFXML(CUSTOMERMANAGEMENT_FXML);
                    currentStage.setTitle("Quản lý khách hàng");
                    break;
                case "discountmanagement":
                    root = loadFXML(DISCOUNTMANAGEMENT_FXML);
                    currentStage.setTitle("Quản lý khuyến mãi");
                    break;
                case "petmanagement":
                    root = loadFXML(PETMANAGEMENT_FXML);
                    currentStage.setTitle("Quản lý thú cưng");
                    break;
                case "productmanagement":
                    root = loadFXML(PRODUCTMANAGEMENT_FXML);
                    currentStage.setTitle("Quản lý sản phẩm");
                    break;
                case "staffmanagement":
                    root = loadFXML(STAFFMANAGEMENT_FXML);
                    currentStage.setTitle("Quản lý nhân viên");
                    break;
                case "bestselling":
                    root = loadFXML(STATISTIC_BESTSELLER_FXML);
                    currentStage.setTitle("Thống kê bán chạy");
                    break;
                case "overview":
                    root = loadFXML(STATISTIC_OVERVIEW_FXML);
                    currentStage.setTitle("Thống kê tổng quan");
                    break;
                case "revenue":
                    root = loadFXML(STATISTIC_REVENUE_FXML);
                    currentStage.setTitle("Thống kê doanh thu");
                    break;
                case "invoicemanagement":
                    root = loadFXML(INVOICEMANAGEMENT_FXML);
                    currentStage.setTitle("Quản lý hóa đơn");
                    break;
                case "inventory":
                    root = loadFXML(INVENTORY_FXML);
                    currentStage.setTitle("Báo cáo tồn kho");
                    break;
                case "customerreport":
                    root = loadFXML(CUSTOMERREPORT_FXML);
                    currentStage.setTitle("Báo cáo khách hàng");
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

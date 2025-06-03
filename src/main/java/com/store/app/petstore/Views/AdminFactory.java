package com.store.app.petstore.Views;

import com.store.app.petstore.Controllers.Admin.*;
import com.store.app.petstore.Controllers.Admin.Statistic.OverViewController;
import com.store.app.petstore.Models.Entities.*;
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
    private static AdminFactory instance;
    private final String USERMANAGEMENT_FXML = "/FXML/Admin/UserManagement.fxml";
    private final String CUSTOMERMANAGEMENT_FXML = "/FXML/Admin/CustomerManagement.fxml";
    private final String DISCOUNTMANAGEMENT_FXML = "/FXML/Admin/DiscountManagement.fxml";
    private final String PETMANAGEMENT_FXML = "/FXML/Admin/PetManagement.fxml";
    private final String PRODUCTMANAGEMENT_FXML = "/FXML/Admin/ProductManagement.fxml";
    private final String STAFFMANAGEMENT_FXML = "/FXML/Admin/StaffManagement.fxml";

    private final String USERTINFOR_FXML = "/FXML/Admin/UserInfor.fxml";
    private final String CUSTOMERINFOR_FXML = "/FXML/Admin/CustomerInfor.fxml";
    private final String DISCOUNTINFOR_FXML = "/FXML/Admin/DiscountInfor.fxml";
    private final String PETINFOR_FXML = "/FXML/Admin/PetInfor.fxml";
    private final String PRODUCTINFOR_FXML = "/FXML/Admin/ProductInfor.fxml";
    private final String STAFFINFOR_FXML = "/FXML/Admin/StaffInfor.fxml";

    private final String OVERVIEW_FXML = "/FXML/Admin/Statistics/Overview.fxml";
    private final String REVENUE_FXML = "/FXML/Admin/Statistics/Revenue.fxml";

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
                case "usermanagement":
                    root = loadFXML(USERMANAGEMENT_FXML);
                    stage.setTitle("Account Management");
                    break;
                case "customermanagement":
                    root = loadFXML(CUSTOMERMANAGEMENT_FXML);
                    stage.setTitle("Customer Management");
                    break;
                case "discountmanagement":
                    root = loadFXML(DISCOUNTMANAGEMENT_FXML);
                    stage.setTitle("Discount Management");
                    break;
                case "petmanagement":
                    root = loadFXML(PETMANAGEMENT_FXML);
                    stage.setTitle("Pet Management");
                    break;
                case "productmanagement":
                    root = loadFXML(PRODUCTMANAGEMENT_FXML);
                    stage.setTitle("Product Management");
                    break;
                case "staffmanagement":
                    root = loadFXML(STAFFMANAGEMENT_FXML);
                    stage.setTitle("Staff Management");
                    break;
                case "overview":
                    root = loadFXML(OVERVIEW_FXML);
                    stage.setTitle("Best Seller Statistics");
                    break;
                case "revenue":
                    root = loadFXML(REVENUE_FXML);
                    stage.setTitle("Revenue Statistics");
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
                } else if (data instanceof Staff && controller instanceof StaffInforController) {
                    ((StaffInforController) controller).setStaff((Staff) data);
                } else if (data instanceof Discount && controller instanceof DiscountInforController) {
                    ((DiscountInforController) controller).setDiscount((Discount) data);
                } else if (data instanceof Pet && controller instanceof PetInforController) {
                    ((PetInforController) controller).setPet((Pet) data);
                } else if (data instanceof Product && controller instanceof ProductInforController) {
                    ((ProductInforController) controller).setProduct((Product) data);
                } else if (data instanceof User && controller instanceof UserInforController) {
                    ((UserInforController) controller).setUser((User) data);
                }
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
                case "usermanagement":
                    root = loadFXML(USERMANAGEMENT_FXML);
                    currentStage.setTitle("Account Management");
                    break;
                case "customermanagement":
                    root = loadFXML(CUSTOMERMANAGEMENT_FXML);
                    currentStage.setTitle("Customer Management");
                    break;
                case "discountmanagement":
                    root = loadFXML(DISCOUNTMANAGEMENT_FXML);
                    currentStage.setTitle("Discount Management");
                    break;
                case "petmanagement":
                    root = loadFXML(PETMANAGEMENT_FXML);
                    currentStage.setTitle("Pet Management");
                    break;
                case "productmanagement":
                    root = loadFXML(PRODUCTMANAGEMENT_FXML);
                    currentStage.setTitle("Product Management");
                    break;
                case "staffmanagement":
                    root = loadFXML(STAFFMANAGEMENT_FXML);
                    currentStage.setTitle("Staff Management");
                    break;
                case "overview":
                    root = loadFXML(OVERVIEW_FXML);
                    currentStage.setTitle("Staff Management");
                    break;
                case "revenue":
                    root = loadFXML(REVENUE_FXML);
                    currentStage.setTitle("Revenue Statistics");
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

package com.store.app.petstore.Views;

import com.store.app.petstore.Controllers.Staff.CustomerInforController;
import com.store.app.petstore.Models.Entities.Customer;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

public class StaffFactory {
    private static StaffFactory instance;

    private final String ORDER_FXML = "/FXML/Staff/Order.fxml";
    private final String PERSONAL_INFOR_FXML = "/FXML/Staff/PersonalInfo.fxml";
    private final String BILL_HISTORY_FXML = "/FXML/Staff/BillHistory.fxml";
    private final String PAYMENT_FXML = "/FXML/Staff/Payment.fxml";
    // popup
    private final String CUSTOMER_POPUP = "/FXML/Staff/CustomerInfor.fxml";

    private final Map<String, AnchorPane> views;
    private final Map<String, Stage> stages;

    StaffFactory() {
        this.views = new HashMap<>();
        this.stages = new HashMap<>();
    }

    public static synchronized StaffFactory getInstance() {
        if (instance == null) {
            instance = new StaffFactory();
        }
        return instance;
    }

    public void showWindow(String fxmlName) {
        Stage stage = new Stage();
        Parent root = null;
        try {
            switch (fxmlName.toLowerCase()) {
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
        stage.setMaximized(true);
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

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        currentStage.setX(screenBounds.getMinX());
        currentStage.setY(screenBounds.getMinY());
        currentStage.setWidth(screenBounds.getWidth());
        currentStage.setHeight(screenBounds.getHeight());

        currentStage.show();
    }
}

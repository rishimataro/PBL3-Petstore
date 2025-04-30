package com.store.app.petstore.Views;

import com.store.app.petstore.Controllers.Staff.OrderController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ViewFactory {

    private static ViewFactory instance;

    // Đường dẫn đến các tệp FXML
    private final String LOGIN_FXML = "/FXML/Login.fxml";
    private final String STAFF_FXML = "/FXML/Staff/StaffDashboard.fxml";
    private final String FORGOT_PASSWORD_FXML = "/FXML/ForgotPassword.fxml";
    private final String ORDER_FXML = "/FXML/Staff/Order.fxml";
    private final String PERSONAL_INFOR_FXML = "/FXML/Staff/PersonalInformation.fxml";

    // Lưu trữ các giao diện đã tải
    private final Map<String, AnchorPane> views;
    // Lưu trữ các cửa sổ đã mở
    private final Map<String, Stage> stages;

    // Singleton instance
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

    // Lấy giao diện theo tên
    public AnchorPane getView(String viewName) {
        if (viewName == null || viewName.isEmpty()) {
            throw new IllegalArgumentException("View name cannot be null or empty");
        }

        if (!views.containsKey(viewName)) {
            try {
                String fxmlPath = getFxmlPath(viewName);
                AnchorPane view = new FXMLLoader(getClass().getResource(fxmlPath)).load();
                views.put(viewName, view);
            } catch (IOException e) {
                System.err.println("Error loading view: " + viewName);
                e.printStackTrace();
                return null;
            }
        }
        return views.get(viewName);
    }

    // Hiển thị cửa sổ với tên FXML
    public void showWindow(String fxmlName) {
        Stage stage = new Stage();
        Parent root = null;

        try {
            switch (fxmlName.toLowerCase()) {
                case "login":
                    root = loadFXML(LOGIN_FXML);
                    stage.setTitle("Login");
                    break;
                case "staff":
                    root = loadFXML(STAFF_FXML);
                    stage.setTitle("Staff Dashboard");
                    break;
                case "forgotpassword":
                    root = loadFXML(FORGOT_PASSWORD_FXML);
                    stage.setTitle("Forgot Password");
                    break;
                case "order":
                    OrderController orderController = new OrderController();
                    orderController.show(stage);
                    stage.setTitle("Order Management");
                    return;
                case "personalinfo":
                    root = loadFXML(PERSONAL_INFOR_FXML);
                    stage.setTitle("Personal Information");
                    break;
                default:
                    System.err.println("Unknown FXML file: " + fxmlName);
                    return;
            }
        } catch (IOException e) {
            System.err.println("Error loading FXML for: " + fxmlName);
            e.printStackTrace();
            return;
        }

        if (root != null) {
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            stages.put(fxmlName, stage);  // Lưu cửa sổ vào map để có thể đóng sau này
        } else {
            System.err.println("Root is null for FXML: " + fxmlName);
        }
    }

    // Đóng cửa sổ theo tên
    public void closeWindow(String windowName) {
        Stage stage = stages.get(windowName);
        if (stage != null) {
            stage.close();
            stages.remove(windowName);
        } else {
            System.err.println("No window found for: " + windowName);
        }
    }

    // Lấy đường dẫn FXML từ tên view
    private String getFxmlPath(String viewName) {
        switch (viewName.toLowerCase()) {
            case "login":
                return LOGIN_FXML;
            case "staff":
                return STAFF_FXML;
            case "personalinfo":
                return PERSONAL_INFOR_FXML;
            case "order":
                return ORDER_FXML;
            case "forgotpassword":
                return FORGOT_PASSWORD_FXML;
            default:
                throw new IllegalArgumentException("Unknown view: " + viewName);
        }
    }

    // Xóa cache các view và stage đã lưu
    public void clearCache() {
        views.clear();
        stages.clear();
    }

    // Tải tệp FXML
    private Parent loadFXML(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        return loader.load();
    }
}

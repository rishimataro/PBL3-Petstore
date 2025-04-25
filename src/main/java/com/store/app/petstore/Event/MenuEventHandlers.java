package com.store.app.petstore.Event;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.util.Optional;

public class MenuEventHandlers implements MenuActions {
    @Override
    public void handleOrder(ActionEvent event) {
        try {
            // Load the order view
            FXMLLoader loader = new FXMLLoader(MenuEventHandlers.class.getResource("/FXML/Staff/Order.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Lỗi", "Không thể mở trang đặt hàng");
        }
    }

    @Override
    public void handleBillHistory(ActionEvent event) {
        try {
            // Load the bill history view
            FXMLLoader loader = new FXMLLoader(MenuEventHandlers.class.getResource("/FXML/Staff/BillHistory.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Lỗi", "Không thể mở trang lịch sử hóa đơn");
        }
    }

    @Override
    public void handleAccountInfo(ActionEvent event) {
        try {
            // Load the account info view
            FXMLLoader loader = new FXMLLoader(MenuEventHandlers.class.getResource("/FXML/Staff/AccountInfo.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Lỗi", "Không thể mở trang thông tin tài khoản");
        }
    }

    @Override
    public void handleLogout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận đăng xuất");
        alert.setHeaderText(null);
        alert.setContentText("Bạn có chắc chắn muốn đăng xuất?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Load the login view
                FXMLLoader loader = new FXMLLoader(MenuEventHandlers.class.getResource("/FXML/Login.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showErrorAlert("Lỗi", "Không thể đăng xuất");
            }
        }
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 
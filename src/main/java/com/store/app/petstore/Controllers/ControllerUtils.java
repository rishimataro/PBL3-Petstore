package com.store.app.petstore.Controllers;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.util.Optional;

public class ControllerUtils {
    public static void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        // Lấy window hiện tại
        Window window = alert.getDialogPane().getScene().getWindow();
        if (window instanceof Stage) {
            Stage stage = (Stage) window;
            // Đảm bảo alert luôn hiển thị trên cùng
            stage.setAlwaysOnTop(true);
            // Đặt modality để chặn tương tác với các window khác
            stage.initModality(Modality.APPLICATION_MODAL);
            // Đặt style cho stage
            stage.initStyle(StageStyle.DECORATED);
        }

        alert.showAndWait();
    }

    public static String formatCurrency(double amount) {
        return String.format("%,.0f", amount).replace(",", " ");
    }

    public static double parseCurrency(String currencyString) {
        return Double.parseDouble(currencyString.replaceAll("[^\\d.]", ""));
    }

    public static boolean showConfirmationAndWait(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        // Lấy window hiện tại
        Window window = alert.getDialogPane().getScene().getWindow();
        if (window instanceof Stage) {
            Stage stage = (Stage) window;
            // Đảm bảo alert luôn hiển thị trên cùng
            stage.setAlwaysOnTop(true);
            // Đặt modality để chặn tương tác với các window khác
            stage.initModality(Modality.APPLICATION_MODAL);
            // Đặt style cho stage
            stage.initStyle(StageStyle.DECORATED);
        }

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public static String getCurrentDateTime() {
        java.util.Date date = new java.util.Date();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    public static void showErrorAndWait(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        // Get the current window
        Window window = alert.getDialogPane().getScene().getWindow();
        if (window instanceof Stage) {
            Stage stage = (Stage) window;
            // Ensure the alert is always on top
            stage.setAlwaysOnTop(true);
            // Set modality to block interaction with other windows
            stage.initModality(Modality.APPLICATION_MODAL);
            // Set style for the stage
            stage.initStyle(StageStyle.DECORATED);
        }

        alert.showAndWait();
    }

    public static void showInformationAndWait(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        // Get the current window
        Window window = alert.getDialogPane().getScene().getWindow();
        if (window instanceof Stage) {
            Stage stage = (Stage) window;
            // Ensure the alert is always on top
            stage.setAlwaysOnTop(true);
            // Set modality to block interaction with other windows
            stage.initModality(Modality.APPLICATION_MODAL);
            // Set style for the stage
            stage.initStyle(StageStyle.DECORATED);
        }

        alert.showAndWait();
    }
}
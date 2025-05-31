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

        Window window = alert.getDialogPane().getScene().getWindow();
        if (window instanceof Stage stage) {
            stage.setAlwaysOnTop(true);
            stage.initModality(Modality.APPLICATION_MODAL);
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

        Window window = alert.getDialogPane().getScene().getWindow();
        if (window instanceof Stage stage) {
            stage.setAlwaysOnTop(true);
            stage.initModality(Modality.APPLICATION_MODAL);
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

    public static void showInformationAndWait(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        Window window = alert.getDialogPane().getScene().getWindow();
        if (window instanceof Stage stage) {
            stage.setAlwaysOnTop(true);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.DECORATED);
        }

        alert.showAndWait();
    }
}

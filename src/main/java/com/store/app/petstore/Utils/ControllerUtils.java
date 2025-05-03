package com.store.app.petstore.Utils;

import javafx.scene.control.Alert;

public class ControllerUtils {
    public static void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static String formatCurrency(double amount) {
        return String.format("%,.0f", amount).replace(",", " ");
    }

    public static double parseCurrency(String currencyString) {
        return Double.parseDouble(currencyString.replace(" ", ""));
    }
} 
package com.store.app.petstore.Controllers.Admin.Statistic;

import com.store.app.petstore.Models.Entities.OrderDetail;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class RevenueController {
    @FXML private Label totalRevenueLabel;
    @FXML private Label totalRevenueValueLabel;
    @FXML private ChoiceBox<String> revenueChartFilter1;
    @FXML private ChoiceBox<String> revenueChartFilter2;
    @FXML private ChoiceBox<String> staffRevenueFilter;
    @FXML private ChoiceBox<String> staffRevenueSort;

    // Mock database service
    private final MockDatabaseService mockDb = new MockDatabaseService();

    public void show(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Thống kê doanh thu");
        primaryStage.setScene(new Scene(createUI(), 900, 900));
        primaryStage.show();
    }

    @FXML
    public void initialize() {
        loadRevenueStatistics();
    }

    private void loadRevenueStatistics() {
        Map<String, String> revenueStats = mockDb.getRevenueStatistics(LocalDate.now());
        totalRevenueValueLabel.setText(revenueStats.get("totalRevenue"));
    }

    public AnchorPane createAdminMenu() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Admin/AdminMenu.fxml"));
        return loader.load();
    }

    public AnchorPane createStatistic() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Admin/Statistics/Revenue.fxml"));
        return loader.load();
    }

    public VBox createUI() throws IOException {
        VBox vBox = new VBox(10);
        vBox.getChildren().addAll(createAdminMenu(), createStatistic());
        return vBox;
    }

    /**
     * Mock database service for demonstration purposes
     */
    private static class MockDatabaseService {
        public Map<String, String> getRevenueStatistics(LocalDate date) {
            Map<String, String> stats = new HashMap<>();
            stats.put("totalRevenue", "12,450,000.00");
            return stats;
        }
    }
}

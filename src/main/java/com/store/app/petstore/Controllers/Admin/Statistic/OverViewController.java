package com.store.app.petstore.Controllers.Admin.Statistic;

import com.store.app.petstore.Models.Entities.OrderDetail;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OverViewController {
    @FXML private Label totalRevenueLabel;
    @FXML private BarChart<String, Number> summaryChart;
    @FXML private Label totalPetsSoldLabel;
    @FXML private Label totalProductsSoldLabel;
    @FXML private Label totalInvoicesLabel;
    @FXML private TableView<Map<String, String>> recentOrdersTable;

    // Mock database service
    private final MockDatabaseService mockDb = new MockDatabaseService();

    public void show(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Thống kê tổng quan");
        primaryStage.setScene(new Scene(createUI(), 900, 900));
        primaryStage.show();
    }

    @FXML
    public void initialize() {
        loadDailyStatistics();
        loadMonthlyRevenueChart();
    }

    private void loadDailyStatistics() {
        Map<String, String> dailyStats = mockDb.getDailyStatistics(LocalDate.now());
        totalRevenueLabel.setText(dailyStats.get("revenue"));
        totalPetsSoldLabel.setText(dailyStats.get("petsSold"));
        totalProductsSoldLabel.setText(dailyStats.get("productsSold"));
        totalInvoicesLabel.setText(dailyStats.get("invoices"));
        
        // Load and display recent orders
        List<Map<String, String>> recentOrders = mockDb.getRecentOrders();
        recentOrdersTable.getItems().setAll(recentOrders);
        
        // Add details for each order
        for (Map<String, String> order : recentOrders) {
            int orderId = Integer.parseInt(order.get("id").split("-")[1]);
            List<OrderDetail> details = mockDb.getRecentOrderDetails(orderId);
            // TODO: Display order details when row is selected
        }
    }

    private void loadMonthlyRevenueChart() {
        Map<String, Number> monthlyRevenue = mockDb.getMonthlyRevenue();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        
        monthlyRevenue.forEach((month, revenue) -> 
            series.getData().add(new XYChart.Data<>(month, revenue))
        );
        
        summaryChart.getData().add(series);
    }

    public AnchorPane createAdminMenu() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Admin/AdminMenu.fxml"));
        return loader.load();
    }

    public AnchorPane createStatistic() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Admin/Statistics/Overview.fxml"));
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
        public Map<String, String> getDailyStatistics(LocalDate date) {
            Map<String, String> stats = new HashMap<>();
            stats.put("revenue", "12,450,000.00");
            stats.put("petsSold", "15");
            stats.put("productsSold", "42");
            stats.put("invoices", "8");
            return stats;
        }

        public Map<String, Number> getMonthlyRevenue() {
            Map<String, Number> revenue = new HashMap<>();
            revenue.put("Tháng 1", 12);
            revenue.put("Tháng 2", 15);
            revenue.put("Tháng 3", 18);
            revenue.put("Tháng 4", 20);
            revenue.put("Tháng 5", 25);
            revenue.put("Tháng 6", 30);
            return revenue;
        }

        public List<OrderDetail> getRecentOrderDetails(int orderId) {
            List<OrderDetail> details = new ArrayList<>();
            // Mock data for order details
            details.add(new OrderDetail(orderId, "pet", 101, 1, 1500000));
            details.add(new OrderDetail(orderId, "product", 201, 2, 250000));
            details.add(new OrderDetail(orderId, "product", 202, 1, 350000));
            return details;
        }

        public List<Map<String, String>> getRecentOrders() {
            List<Map<String, String>> orders = new ArrayList<>();
            
            Map<String, String> order1 = new HashMap<>();
            order1.put("id", "ORD-001");
            order1.put("customer", "Nguyễn Văn A");
            order1.put("date", "2023-05-01");
            order1.put("total", "1,250,000");
            order1.put("status", "Hoàn thành");
            orders.add(order1);

            Map<String, String> order2 = new HashMap<>();
            order2.put("id", "ORD-002");
            order2.put("customer", "Trần Thị B");
            order2.put("date", "2023-05-02");
            order2.put("total", "2,500,000");
            order2.put("status", "Đang xử lý");
            orders.add(order2);

            Map<String, String> order3 = new HashMap<>();
            order3.put("id", "ORD-003");
            order3.put("customer", "Lê Văn C");
            order3.put("date", "2023-05-03");
            order3.put("total", "1,800,000");
            order3.put("status", "Hoàn thành");
            orders.add(order3);

            Map<String, String> order4 = new HashMap<>();
            order4.put("id", "ORD-004");
            order4.put("customer", "Phạm Thị D");
            order4.put("date", "2023-05-04");
            order4.put("total", "3,200,000");
            order4.put("status", "Đã hủy");
            orders.add(order4);

            return orders;
        }
    }
}

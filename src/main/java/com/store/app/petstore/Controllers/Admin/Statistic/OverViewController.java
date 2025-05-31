package com.store.app.petstore.Controllers.Admin.Statistic;

import com.store.app.petstore.DAO.StatisticDAO.OverviewDAO;
import javafx.fxml.FXML;
import java.sql.SQLException;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.concurrent.Task;
import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.cell.MapValueFactory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class OverViewController {
    @FXML
    private Label totalRevenueLabel;
    @FXML
    private BarChart<String, Number> summaryChart;
    @FXML
    private Label totalPetsSoldLabel;
    @FXML
    private Label totalProductsSoldLabel;
    @FXML
    private Label totalInvoicesLabel;
    @FXML
    private TableView<Map<String, String>> recentOrdersTable;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private ProgressBar chartProgressBar;
    @FXML
    private ProgressBar tableProgressBar;


    @FXML
    public void initialize() {
        recentOrdersTable.getColumns().get(0).setCellValueFactory(new MapValueFactory("id"));
        recentOrdersTable.getColumns().get(1).setCellValueFactory(new MapValueFactory("customer"));
        recentOrdersTable.getColumns().get(2).setCellValueFactory(new MapValueFactory("date"));
        recentOrdersTable.getColumns().get(3).setCellValueFactory(new MapValueFactory("total"));

        loadDailyStatistics();
    }

    private void loadDailyStatistics() {
        Task<Void> dailyStatsTask = new Task<Void>() {
            @Override
            protected Void call() throws SQLException {
                updateProgress(-1, 1); // Indeterminate progress
                Map<String, String> dailyStats = OverviewDAO.getDailyStatistics(LocalDate.now());
                List<Map<String, String>> recentOrders = OverviewDAO.getRecentOrders();

                Platform.runLater(() -> {
                    totalRevenueLabel.setText(dailyStats.get("revenue"));
                    totalPetsSoldLabel.setText(dailyStats.get("petsSold"));
                    totalProductsSoldLabel.setText(dailyStats.get("productsSold"));
                    totalInvoicesLabel.setText(dailyStats.get("invoices"));
                    recentOrdersTable.getItems().setAll(recentOrders);
                });
                return null;
            }
        };

        dailyStatsTask.setOnSucceeded(event -> {
        });

        dailyStatsTask.setOnFailed(event -> {
            Throwable e = dailyStatsTask.getException();
            e.printStackTrace();
            Platform.runLater(() -> {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Database Error");
                alert.setContentText("Failed to load daily statistics.");
                alert.showAndWait();
            });
        });

        new Thread(dailyStatsTask).start();
    }

    public Map<String, LocalDate> getRevenueDateRange() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (startDate != null && endDate != null) {
            Map<String, LocalDate> dateRange = new HashMap<>();
            dateRange.put("startDate", startDate);
            dateRange.put("endDate", endDate);
            return dateRange;
        } else {
            return null; // Or handle as needed, e.g., return empty map or throw exception
        }
    }

    @FXML
    private void onViewRevenueButtonClicked() {
        Map<String, LocalDate> dateRange = getRevenueDateRange();
        if (dateRange != null) {
            Task<Map<String, Number>> filteredRevenueTask = new Task<Map<String, Number>>() {
                @Override
                protected Map<String, Number> call() throws SQLException {
                     updateProgress(-1, 1);
                    return OverviewDAO.getRevenueByDateRange(dateRange.get("startDate"), dateRange.get("endDate"));
                }
            };

            chartProgressBar.progressProperty().bind(filteredRevenueTask.progressProperty());
            chartProgressBar.setVisible(true);

            filteredRevenueTask.setOnSucceeded(event -> {
                chartProgressBar.progressProperty().unbind();
                chartProgressBar.setVisible(false);
                Map<String, Number> filteredRevenue = filteredRevenueTask.getValue();
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                filteredRevenue.forEach((month, revenue) -> series.getData().add(new XYChart.Data<>(month, revenue)));
                summaryChart.getData().clear();
                summaryChart.getData().add(series);
            });

            filteredRevenueTask.setOnFailed(event -> {
                chartProgressBar.progressProperty().unbind();
                chartProgressBar.setVisible(false);
                Throwable e = filteredRevenueTask.getException();
                e.printStackTrace();
                Platform.runLater(() -> {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Database Error");
                    alert.setContentText("Failed to load revenue data for the selected date range.");
                    alert.showAndWait();
                });
            });

            new Thread(filteredRevenueTask).start();

        } else {
            System.out.println("View Revenue button clicked. Date range not selected.");
             Alert alert = new Alert(AlertType.WARNING);
             alert.setTitle("Warning");
             alert.setHeaderText(null);
             alert.setContentText("Please select both start and end dates.");
             alert.showAndWait();
        }
    }

    @FXML
    private void onViewRecentOrdersButtonClicked() {
        System.out.println("View Recent Orders button clicked.");

        Task<List<Map<String, String>>> recentOrdersTask = new Task<List<Map<String, String>>>() {
            @Override
            protected List<Map<String, String>> call() throws SQLException {
                 updateProgress(-1, 1);
                return OverviewDAO.getRecentOrders();
            }
        };

        tableProgressBar.progressProperty().bind(recentOrdersTask.progressProperty());
        tableProgressBar.setVisible(true);

        recentOrdersTask.setOnSucceeded(event -> {
            tableProgressBar.progressProperty().unbind();
            tableProgressBar.setVisible(false);
            List<Map<String, String>> recentOrders = recentOrdersTask.getValue();
            recentOrdersTable.getItems().setAll(recentOrders);
        });

        recentOrdersTask.setOnFailed(event -> {
            tableProgressBar.progressProperty().unbind();
            tableProgressBar.setVisible(false);
            Throwable e = recentOrdersTask.getException();
            e.printStackTrace();
            Platform.runLater(() -> {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Database Error");
                alert.setContentText("Failed to load recent orders.");
                alert.showAndWait();
            });
        });

        new Thread(recentOrdersTask).start();
    }

}

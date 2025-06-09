package com.store.app.petstore.Controllers.Admin.Statistic;

import com.store.app.petstore.DAO.StatisticDAO.OverviewDAO;
import javafx.fxml.FXML;
import java.sql.SQLException;

import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.concurrent.Task;
import javafx.application.Platform;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.Pane;
import javafx.collections.FXCollections;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private ChoiceBox<String> monthChoiceBox;

    @FXML
    public void initialize() {
        setupTableColumns();
        setupChoiceBoxes();
        initializeDatePickers();
        loadDailyStatistics();
        updateLastUpdateTime();
    }

    private void setupTableColumns() {
        try {
            if (recentOrdersTable != null && recentOrdersTable.getColumns().size() >= 4) {
                recentOrdersTable.getColumns().get(0).setCellValueFactory(new MapValueFactory("id"));
                recentOrdersTable.getColumns().get(1).setCellValueFactory(new MapValueFactory("customer"));
                recentOrdersTable.getColumns().get(2).setCellValueFactory(new MapValueFactory("date"));
                recentOrdersTable.getColumns().get(3).setCellValueFactory(new MapValueFactory("total"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error setting up table columns: " + e.getMessage());
        }
    }

    private void setupChoiceBoxes() {
        if (monthChoiceBox != null) {
            monthChoiceBox.setItems(FXCollections.observableArrayList(
                "Tất cả", "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6",
                "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"
            ));
            monthChoiceBox.setValue("Tất cả");

            monthChoiceBox.setVisible(true);
            monthChoiceBox.setManaged(true);

            monthChoiceBox.setOnAction(e -> onMonthFilterChanged());
        }
    }

    private void onMonthFilterChanged() {
        String selectedMonth = monthChoiceBox.getValue();
        onViewRecentOrdersButtonClicked();
    }

    private void initializeDatePickers() {
        LocalDate now = LocalDate.now();
        LocalDate oneYearAgo = now.minusYears(1);

        startDatePicker.setValue(oneYearAgo);
        endDatePicker.setValue(now);

        startDatePicker.setOnAction(e -> onDateRangeChanged());
        endDatePicker.setOnAction(e -> onDateRangeChanged());
    }

    private void updateLastUpdateTime() {
        Platform.runLater(() -> {
            String currentTime = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            System.out.println("Statistics last updated: " + currentTime);
        });
    }

    private void onDateRangeChanged() {
        if (startDatePicker.getValue() != null && endDatePicker.getValue() != null) {
            if (startDatePicker.getValue().isAfter(endDatePicker.getValue())) {
                showAlert("Lỗi", "Ngày bắt đầu không thể sau ngày kết thúc!");
                return;
            }
            onViewRevenueButtonClicked();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadDailyStatistics() {
        Task<Void> dailyStatsTask = new Task<Void>() {
            @Override
            protected Void call() {
                updateProgress(-1, 1);
                Map<String, String> overallStats = null;
                List<Map<String, String>> recentOrders = null;
                try {
                    overallStats = OverviewDAO.getOverallStatistics();
                    recentOrders = OverviewDAO.getRecentOrders();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final Map<String, String> finalOverallStats = overallStats;
                final List<Map<String, String>> finalRecentOrders = recentOrders;
                Platform.runLater(() -> {
                    updateStatisticsDisplay(finalOverallStats != null ? finalOverallStats : new HashMap<>());
                    if (finalRecentOrders != null && !finalRecentOrders.isEmpty()) {
                        recentOrdersTable.getItems().setAll(finalRecentOrders);
                    } else {
                        recentOrdersTable.getItems().clear();
                    }
                    onViewRevenueButtonClicked();
                });
                return null;
            }
        };

        dailyStatsTask.setOnFailed(event -> {
            Throwable e = dailyStatsTask.getException();
            if (e != null) e.printStackTrace();
            Platform.runLater(() -> {
                showAlert("Lỗi", "Không thể tải dữ liệu thống kê.");
                setDefaultStatistics();
                recentOrdersTable.getItems().clear();
            });
        });

        new Thread(dailyStatsTask).start();
    }

    private void updateStatisticsDisplay(Map<String, String> stats) {
        // Update revenue
        String revenue = stats.get("revenue");
        if (revenue != null && !revenue.equals("Error") && !revenue.equals("0.00")) {
            totalRevenueLabel.setText(formatCurrency(revenue) + " VND");
        } else {
            totalRevenueLabel.setText("0 VND");
        }

        // Update pets sold
        String petsSold = stats.get("petsSold");
        if (petsSold != null && !petsSold.equals("Error")) {
            totalPetsSoldLabel.setText(formatNumber(petsSold));
        } else {
            totalPetsSoldLabel.setText("0");
        }

        // Update products sold
        String productsSold = stats.get("productsSold");
        if (productsSold != null && !productsSold.equals("Error")) {
            totalProductsSoldLabel.setText(formatNumber(productsSold));
        } else {
            totalProductsSoldLabel.setText("0");
        }

        // Update invoices
        String invoices = stats.get("invoices");
        if (invoices != null && !invoices.equals("Error")) {
            totalInvoicesLabel.setText(formatNumber(invoices));
        } else {
            totalInvoicesLabel.setText("0");
        }
    }

    private void setDefaultStatistics() {
        totalRevenueLabel.setText("0");
        totalPetsSoldLabel.setText("0");
        totalProductsSoldLabel.setText("0");
        totalInvoicesLabel.setText("0");
    }

    private String formatCurrency(String value) {
        try {
            double amount = Double.parseDouble(value.replace(",", ""));
            return String.format("%,.0f", amount);
        } catch (NumberFormatException e) {
            return value;
        }
    }

    private String formatNumber(String value) {
        try {
            int number = Integer.parseInt(value);
            return String.format("%,d", number);
        } catch (NumberFormatException e) {
            return value != null ? value : "0";
        }
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
            return null;
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
                updateChartWithSortedData(filteredRevenue);
            });

            filteredRevenueTask.setOnFailed(event -> {
                chartProgressBar.progressProperty().unbind();
                chartProgressBar.setVisible(false);
                Throwable e = filteredRevenueTask.getException();
                e.printStackTrace();
                Platform.runLater(() -> {
                    showAlert("Lỗi", "Không thể tải dữ liệu doanh thu cho khoảng thời gian đã chọn.");
                });
            });

            new Thread(filteredRevenueTask).start();

        } else {
            showAlert("Cảnh báo", "Vui lòng chọn cả ngày bắt đầu và ngày kết thúc.");
        }
    }

    private void updateChartWithSortedData(Map<String, Number> revenueData) {
        try {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Doanh thu");

            if (revenueData == null || revenueData.isEmpty()) {
                summaryChart.getData().clear();
                return;
            }

            revenueData.entrySet().stream()
                .sorted((e1, e2) -> {
                    try {
                        int month1 = Integer.parseInt(e1.getKey().replace("Tháng ", ""));
                        int month2 = Integer.parseInt(e2.getKey().replace("Tháng ", ""));
                        return Integer.compare(month1, month2);
                    } catch (NumberFormatException ex) {
                        return e1.getKey().compareTo(e2.getKey());
                    }
                })
                .forEach(entry -> {
                    XYChart.Data<String, Number> data = new XYChart.Data<>(entry.getKey(), entry.getValue());
                    series.getData().add(data);
                });

            summaryChart.getData().clear();
            summaryChart.getData().add(series);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể cập nhật biểu đồ: " + e.getMessage());
        }
    }

    @FXML
    private void onViewRecentOrdersButtonClicked() {
        Map<String, LocalDate> dateRange = getRevenueDateRange();
        String selectedMonth = monthChoiceBox != null ? monthChoiceBox.getValue() : "Tất cả";

        Task<List<Map<String, String>>> recentOrdersTask = new Task<List<Map<String, String>>>() {
            @Override
            protected List<Map<String, String>> call() throws SQLException {
                 updateProgress(-1, 1);

                 if (selectedMonth != null && !selectedMonth.equals("Tất cả")) {
                     try {
                         int monthNumber = Integer.parseInt(selectedMonth.replace("Tháng ", ""));
                         return OverviewDAO.getRecentOrders(monthNumber);
                     } catch (NumberFormatException e) {
                         return OverviewDAO.getRecentOrders();
                     }
                 } else if (dateRange != null) {
                     return OverviewDAO.getOrdersByDateRange(dateRange.get("startDate"), dateRange.get("endDate"));
                 } else {
                     return OverviewDAO.getRecentOrders();
                 }
            }
        };

        tableProgressBar.progressProperty().bind(recentOrdersTask.progressProperty());
        tableProgressBar.setVisible(true);

        recentOrdersTask.setOnSucceeded(event -> {
            tableProgressBar.progressProperty().unbind();
            tableProgressBar.setVisible(false);
            List<Map<String, String>> orders = recentOrdersTask.getValue();
            recentOrdersTable.getItems().setAll(orders);
        });

        recentOrdersTask.setOnFailed(event -> {
            tableProgressBar.progressProperty().unbind();
            tableProgressBar.setVisible(false);
            Throwable e = recentOrdersTask.getException();
            e.printStackTrace();
            Platform.runLater(() -> {
                showAlert("Lỗi", "Không thể tải dữ liệu đơn hàng.");
            });
        });

        new Thread(recentOrdersTask).start();
    }

}

package com.store.app.petstore.Controllers.Admin.Statistic;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.chart.BarChart;

import com.store.app.petstore.DAO.StatisticDAO.RevenueDAO;
import com.store.app.petstore.Models.Records.StaffRevenueStats;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

public class RevenueController {

    @FXML private Label totalRevenueValueLabel;
    @FXML private DatePicker revenueChartFilter1;
    @FXML private DatePicker revenueChartFilter2;
    @FXML private Label monthlyRevenueLabel;
    @FXML private Label lastMonthRevenueLabel;
    @FXML private Label revenueRatioLabel;
    @FXML private TableView<StaffRevenueStats> staffRevenueTable;
    @FXML private BarChart<String, Number> summaryChart;
    @FXML private Button viewChartButton;
    @FXML private ChoiceBox<String> staffRevenueFilter;
    @FXML private ProgressBar chartProgressBar;
    @FXML private ProgressBar staffTableProgressBar;

    // === Initialize ===
    @FXML
    public void initialize() {
        initializeStaffRevenueFilters();
        loadDefaultRevenueStatistics();
    }

    private void initializeStaffRevenueFilters() {
        ObservableList<String> months = FXCollections.observableArrayList(
                "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6",
                "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"
        );
        staffRevenueFilter.setItems(months);
        staffRevenueFilter.getSelectionModel().selectFirst();
    }

    private void loadDefaultRevenueStatistics() {
        LocalDate startDate = LocalDate.now().withDayOfMonth(1);
        LocalDate endDate = LocalDate.now();

        revenueChartFilter1.setValue(startDate);
        revenueChartFilter2.setValue(endDate);

        fetchAndDisplayRevenueStatistics(startDate, endDate);
    }

    // === Event Handlers ===

    @FXML
    private void handleViewChartButtonAction() {
        LocalDate startDate = revenueChartFilter1.getValue();
        LocalDate endDate = revenueChartFilter2.getValue();

        if (startDate != null && endDate != null) {
            fetchAndDisplayRevenueStatistics(startDate, endDate);
        } else {
            System.out.println("Vui lòng chọn cả ngày bắt đầu và ngày kết thúc.");
        }
    }

    @FXML
    private void handleViewStaffButtonAction() {
        String selectedMonth = staffRevenueFilter.getValue();

        if (selectedMonth == null) return;

        int month = staffRevenueFilter.getItems().indexOf(selectedMonth) + 1;
        int year = LocalDate.now().getYear();

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        loadStaffRevenueData(startDate, endDate);
    }

    // === Logic ===

    private void fetchAndDisplayRevenueStatistics(LocalDate startDate, LocalDate endDate) {
        chartProgressBar.setVisible(true);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Map<String, String> revenueStats = RevenueDAO.fetchRevenueDataFromDatabase(startDate, endDate);
                ObservableList<XYChart.Data<String, Number>> monthlyData =
                        RevenueDAO.fetchMonthlyRevenueDataFromDatabase(startDate, endDate);
                ArrayList<StaffRevenueStats> staffData =
                        RevenueDAO.fetchStaffRevenueDataFromDatabase(startDate, endDate);

                Platform.runLater(() -> {
                    updateRevenueLabels(revenueStats);
                    updateBarChart(monthlyData);
                    updateStaffTable(staffData);
                });
                return null;
            }

            @Override
            protected void succeeded() {
                chartProgressBar.setVisible(false);
            }

            @Override
            protected void failed() {
                chartProgressBar.setVisible(false);
                getException().printStackTrace();
            }
        };

        new Thread(task).start();
    }

    private void loadStaffRevenueData(LocalDate startDate, LocalDate endDate) {
        staffTableProgressBar.setVisible(true);

        Task<ObservableList<StaffRevenueStats>> task = new Task<>() {
            @Override
            protected ObservableList<StaffRevenueStats> call() throws Exception {
                ArrayList<StaffRevenueStats> staffData =
                        RevenueDAO.fetchStaffRevenueDataFromDatabase(startDate, endDate);
                staffData.forEach(System.out::println);
                return FXCollections.observableArrayList(staffData);
            }

            @Override
            protected void succeeded() {
                staffRevenueTable.setItems(getValue());
                staffTableProgressBar.setVisible(false);
            }

            @Override
            protected void failed() {
                staffTableProgressBar.setVisible(false);
                getException().printStackTrace();
            }
        };

        new Thread(task).start();
    }

    // === UI Update Helpers ===

    private void updateRevenueLabels(Map<String, String> stats) {
        totalRevenueValueLabel.setText(stats.get("totalRevenue"));
        monthlyRevenueLabel.setText(stats.get("monthlyRevenue"));
        lastMonthRevenueLabel.setText(stats.get("lastMonthRevenue"));
        revenueRatioLabel.setText(stats.get("revenueRatio"));
    }

    private void updateBarChart(ObservableList<XYChart.Data<String, Number>> data) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().addAll(data);

        summaryChart.getData().clear();
        summaryChart.getData().add(series);
    }

    private void updateStaffTable(ArrayList<StaffRevenueStats> data) {
        staffRevenueTable.setItems(FXCollections.observableArrayList(data));
    }
}

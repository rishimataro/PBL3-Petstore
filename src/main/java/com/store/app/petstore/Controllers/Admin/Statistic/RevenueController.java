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

    @FXML private TableView<StaffRevenueStats> staffRevenueTable;
    @FXML private BarChart<String, Number> summaryChart;
    @FXML private ProgressBar chartProgressBar;
    @FXML private ProgressBar staffTableProgressBar;
    @FXML private ChoiceBox<String> monthChoiceBox;

    @FXML
    public void initialize() {
        setupChoiceBoxes();
        loadDefaultRevenueStatistics();
        setupChartStyling();
    }

    private void setupChoiceBoxes() {
        if (monthChoiceBox != null) {
            monthChoiceBox.setItems(FXCollections.observableArrayList(
                    "Tất cả", "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6",
                    "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"
            ));
            monthChoiceBox.setValue("Tất cả");

            monthChoiceBox.setOnAction(e -> onMonthFilterChanged());
        }
    }

    private void setupChartStyling() {
        // Configure chart appearance
        summaryChart.setAnimated(true);
        summaryChart.setLegendVisible(false);
        summaryChart.setTitle("Doanh thu theo nhân viên");
    }

    private void onMonthFilterChanged() {
        String selectedMonth = monthChoiceBox.getValue();

        if (selectedMonth != null && !selectedMonth.equals("Tất cả")) {
            try {
                int monthNumber = Integer.parseInt(selectedMonth.replace("Tháng ", ""));
                int year = LocalDate.now().getYear();
                loadStaffRevenueChartByMonth(monthNumber, year);

                LocalDate startDate = LocalDate.of(year, monthNumber, 1);
                LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
                loadStaffRevenueData(startDate, endDate);
            } catch (NumberFormatException e) {
                System.err.println("Error parsing month number from: " + selectedMonth);
            }
        } else {
            loadDefaultRevenueStatistics();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadDefaultRevenueStatistics() {
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfYear(1);
        LocalDate endDate = now;

        fetchAndDisplayRevenueStatistics(startDate, endDate);
    }

    private void fetchAndDisplayRevenueStatistics(LocalDate startDate, LocalDate endDate) {
        chartProgressBar.setVisible(true);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                ObservableList<XYChart.Data<String, Number>> staffChartData =
                        RevenueDAO.fetchStaffRevenueChartData(startDate, endDate);
                ArrayList<StaffRevenueStats> staffData =
                        RevenueDAO.fetchStaffRevenueDataFromDatabase(startDate, endDate);

                Platform.runLater(() -> {
                    updateStaffRevenueChart(staffChartData);
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

    private void loadStaffRevenueChartByMonth(int month, int year) {
        chartProgressBar.setVisible(true);

        Task<ObservableList<XYChart.Data<String, Number>>> task = new Task<>() {
            @Override
            protected ObservableList<XYChart.Data<String, Number>> call() throws Exception {
                return RevenueDAO.fetchStaffRevenueChartDataByMonth(month, year);
            }

            @Override
            protected void succeeded() {
                updateStaffRevenueChart(getValue());
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

    private void updateStaffRevenueChart(ObservableList<XYChart.Data<String, Number>> data) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Doanh thu nhân viên");
        series.getData().addAll(data);

        summaryChart.getData().clear();
        summaryChart.getData().add(series);
    }

    private void updateStaffTable(ArrayList<StaffRevenueStats> data) {
        staffRevenueTable.setItems(FXCollections.observableArrayList(data));
    }
}

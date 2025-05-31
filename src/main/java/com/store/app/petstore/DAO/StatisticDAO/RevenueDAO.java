package com.store.app.petstore.DAO.StatisticDAO;

import com.store.app.petstore.DAO.DatabaseUtil;
import com.store.app.petstore.Models.Records.StaffRevenueStats;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RevenueDAO {

    public static Map<String, String> fetchRevenueDataFromDatabase(LocalDate startDate, LocalDate endDate) throws SQLException {
        Map<String, String> stats = new HashMap<>();
        Timestamp startTimestamp = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp endTimestamp = Timestamp.valueOf(endDate.atTime(23, 59, 59));

        Connection conn = null;

        try {
            conn = DatabaseUtil.getConnection();

            double totalRevenue = calculateTotalRevenue(conn, startTimestamp, endTimestamp);
            double monthlyRevenue = calculateMonthlyRevenue(conn, endDate);
            double lastMonthRevenue = calculateLastMonthRevenue(conn, endDate);
            double revenueRatio = calculateRevenueRatio(monthlyRevenue, lastMonthRevenue);

            stats.put("totalRevenue", String.format("%,.2f", totalRevenue));
            stats.put("monthlyRevenue", String.format("%,.2f", monthlyRevenue));
            stats.put("lastMonthRevenue", String.format("%,.2f", lastMonthRevenue));
            stats.put("revenueRatio", String.format("%,.2f", revenueRatio));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stats;
    }

    private static double calculateTotalRevenue(Connection conn, Timestamp startTimestamp, Timestamp endTimestamp)
            throws SQLException {
        double totalRevenue = 0.0;
        String sql = "SELECT SUM(total_price) FROM Orders WHERE order_date BETWEEN ? AND ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setTimestamp(1, startTimestamp);
            pstmt.setTimestamp(2, endTimestamp);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    totalRevenue = rs.getDouble(1);
                }
            }
        }
        return totalRevenue;
    }

    private static double calculateMonthlyRevenue(Connection conn, LocalDate endDate) throws SQLException {
        LocalDate currentMonthStart = endDate.withDayOfMonth(1);
        LocalDate currentMonthEnd = endDate.withDayOfMonth(endDate.lengthOfMonth());
        Timestamp currentMonthStartTimestamp = Timestamp.valueOf(currentMonthStart.atStartOfDay());
        Timestamp currentMonthEndTimestamp = Timestamp.valueOf(currentMonthEnd.atTime(23, 59, 59));

        double monthlyRevenue = 0.0;
        String sql = "SELECT SUM(total_price) FROM Orders WHERE order_date BETWEEN ? AND ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setTimestamp(1, currentMonthStartTimestamp);
            pstmt.setTimestamp(2, currentMonthEndTimestamp);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    monthlyRevenue = rs.getDouble(1);
                }
            }
        }
        return monthlyRevenue;
    }

    private static double calculateLastMonthRevenue(Connection conn, LocalDate endDate) throws SQLException {
        LocalDate currentMonthStart = endDate.withDayOfMonth(1);
        LocalDate lastMonthEnd = currentMonthStart.minusDays(1);
        LocalDate lastMonthStart = lastMonthEnd.withDayOfMonth(1);
        Timestamp lastMonthStartTimestamp = Timestamp.valueOf(lastMonthStart.atStartOfDay());
        Timestamp lastMonthEndTimestamp = Timestamp.valueOf(lastMonthEnd.atTime(23, 59, 59));

        double lastMonthRevenue = 0.0;
        String sql = "SELECT SUM(total_price) FROM Orders WHERE order_date BETWEEN ? AND ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setTimestamp(1, lastMonthStartTimestamp);
            pstmt.setTimestamp(2, lastMonthEndTimestamp);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    lastMonthRevenue = rs.getDouble(1);
                }
            }
        }
        return lastMonthRevenue;
    }

    private static double calculateRevenueRatio(double monthlyRevenue, double lastMonthRevenue) {
        double revenueRatio = 0.0;
        if (lastMonthRevenue > 0) {
            revenueRatio = ((monthlyRevenue - lastMonthRevenue) / lastMonthRevenue) * 100;
        }
        return revenueRatio;
    }

    public static ObservableList<XYChart.Data<String, Number>> fetchMonthlyRevenueDataFromDatabase(LocalDate startDate,
                                                                                                   LocalDate endDate) {
        ObservableList<XYChart.Data<String, Number>> data = FXCollections.observableArrayList();
        Timestamp startTimestamp = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp endTimestamp = Timestamp.valueOf(endDate.atTime(23, 59, 59));

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT DATE_FORMAT(order_date, '%Y-%m') as month, SUM(total_price) as monthly_revenue " +
                    "FROM Orders WHERE order_date BETWEEN ? AND ? GROUP BY month ORDER BY month";
            pstmt = conn.prepareStatement(sql);
            pstmt.setTimestamp(1, startTimestamp);
            pstmt.setTimestamp(2, endTimestamp);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                data.add(new XYChart.Data<>(rs.getString("month"), rs.getDouble("monthly_revenue")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions
        }

        return data;
    }

    public static ArrayList<StaffRevenueStats> fetchStaffRevenueDataFromDatabase(LocalDate startDate, LocalDate endDate)
            throws SQLException {
        ArrayList<StaffRevenueStats> data = new ArrayList<>();
        Timestamp startTimestamp = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp endTimestamp = Timestamp.valueOf(endDate.atTime(23, 59, 59));

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT s.full_name, SUM(o.total_price) as staff_revenue " +
                    "FROM Orders o JOIN Staffs s ON o.staff_id = s.staff_id " +
                    "WHERE o.order_date BETWEEN ? AND ? GROUP BY s.full_name";
            pstmt = conn.prepareStatement(sql);
            pstmt.setTimestamp(1, startTimestamp);
            pstmt.setTimestamp(2, endTimestamp);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                String staffName = rs.getString("full_name");
                double staffRevenue = rs.getDouble("staff_revenue");
                data.add(new StaffRevenueStats(staffName, staffRevenue));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }
}

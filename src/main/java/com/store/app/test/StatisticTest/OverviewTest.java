package com.store.app.test.StatisticTest;

import com.store.app.petstore.DAO.StatisticDAO.OverviewDAO;
import com.store.app.petstore.Models.Records.OrderDetailRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class OverviewTest {
    public static void main(String[] args) {
        testGetDailyStatistics();
        testGetMonthlyRevenue();
        testGetRecentOrders();
        testGetRecentOrderDetails(1);
        testGetRevenueByDateRange();
    }

    public static void testGetDailyStatistics() {
        System.out.println("----- Test getDailyStatistics -----");
        try {
            Map<String, String> stats = OverviewDAO.getDailyStatistics(LocalDate.now());
            stats.forEach((key, value) -> System.out.println(key + ": " + value));
        } catch (Exception e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    public static void testGetMonthlyRevenue() {
        System.out.println("----- Test getMonthlyRevenue -----");
        try {
            Map<String, Number> revenue = OverviewDAO.getMonthlyRevenue();
            revenue.forEach((month, amount) -> System.out.println(month + ": " + amount));
        } catch (Exception e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    public static void testGetRecentOrders() {
        System.out.println("----- Test getRecentOrders -----");
        try {
            List<Map<String, String>> orders = OverviewDAO.getRecentOrders();
            for (Map<String, String> order : orders) {
                System.out.println(order);
            }
        } catch (Exception e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    public static void testGetRecentOrderDetails(int orderId) {
        System.out.println("----- Test getRecentOrderDetails (orderId = " + orderId + ") -----");
        try {
            List<OrderDetailRecord> details = OverviewDAO.getRecentOrderDetails(orderId);
            for (OrderDetailRecord detail : details) {
                System.out.println(detail);
            }
        } catch (Exception e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    public static void testGetRevenueByDateRange() {
        System.out.println("----- Test getRevenueByDateRange -----");
        try {
            LocalDate start = LocalDate.now().minusMonths(6);
            LocalDate end = LocalDate.now();
            Map<String, Number> revenue = OverviewDAO.getRevenueByDateRange(start, end);
            revenue.forEach((month, amount) -> System.out.println(month + ": " + amount));
        } catch (Exception e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

}

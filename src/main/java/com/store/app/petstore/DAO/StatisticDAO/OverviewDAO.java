package com.store.app.petstore.DAO.StatisticDAO;

import com.store.app.petstore.DAO.DatabaseUtil;
import com.store.app.petstore.Models.Records.OrderDetailRecord;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class OverviewDAO {

    public static Map<String, String> getOverallStatistics() throws SQLException {
        Map<String, String> stats = new HashMap<>();
        Connection connection = DatabaseUtil.getConnection();

        String sql = "SELECT " +
                "SUM(CASE WHEN od.item_type = 'pet' THEN od.quantity ELSE 0 END) AS total_pets_sold, " +
                "SUM(CASE WHEN od.item_type = 'product' THEN od.quantity ELSE 0 END) AS total_products_sold, " +
                "SUM(o.total_price) AS total_revenue, " +
                "COUNT(DISTINCT o.order_id) AS total_invoices " +
                "FROM Orders o " +
                "JOIN OrderDetails od ON o.order_id = od.order_id";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                stats.put("petsSold", String.valueOf(rs.getInt("total_pets_sold")));
                stats.put("productsSold", String.valueOf(rs.getInt("total_products_sold")));
                stats.put("revenue", String.format("%.2f", rs.getDouble("total_revenue")));
                stats.put("invoices", String.valueOf(rs.getInt("total_invoices")));
            } else {
                stats.put("petsSold", "0");
                stats.put("productsSold", "0");
                stats.put("revenue", "0.00");
                stats.put("invoices", "0");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            stats.put("petsSold", "Error");
            stats.put("productsSold", "Error");
            stats.put("revenue", "Error");
            stats.put("invoices", "Error");
        }

        return stats;
    }

    public static Map<String, String> getDailyStatistics(LocalDate date) throws SQLException {
        Map<String, String> stats = new HashMap<>();
        Connection connection = DatabaseUtil.getConnection();

        String sql = "SELECT " +
                     "SUM(CASE WHEN od.item_type = 'pet' THEN od.quantity ELSE 0 END) AS total_pets_sold, " +
                     "SUM(CASE WHEN od.item_type = 'product' THEN od.quantity ELSE 0 END) AS total_products_sold, " +
                     "SUM(o.total_price) AS total_revenue, " +
                     "COUNT(DISTINCT o.order_id) AS total_invoices " +
                     "FROM Orders o " +
                     "JOIN OrderDetails od ON o.order_id = od.order_id " +
                     "WHERE DATE(o.order_date) = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(date));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.put("petsSold", String.valueOf(rs.getInt("total_pets_sold")));
                    stats.put("productsSold", String.valueOf(rs.getInt("total_products_sold")));
                    stats.put("revenue", String.format("%,.2f", rs.getDouble("total_revenue")));
                    stats.put("invoices", String.valueOf(rs.getInt("total_invoices")));
                } else {
                    stats.put("petsSold", "0");
                    stats.put("productsSold", "0");
                    stats.put("revenue", "0.00");
                    stats.put("invoices", "0");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            stats.put("petsSold", "Error");
            stats.put("productsSold", "Error");
            stats.put("revenue", "Error");
            stats.put("invoices", "Error");
        }

        return stats;
    }

    public static Map<String, Number> getMonthlyRevenue() throws SQLException {
        Map<String, Number> revenue = new HashMap<>();
        Connection connection = DatabaseUtil.getConnection();

        String sql = "SELECT DATE_FORMAT(order_date, '%Y-%m') AS month, SUM(total_price) AS monthly_revenue " +
                     "FROM Orders " +
                     "GROUP BY month " +
                     "ORDER BY month ASC";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String month = rs.getString("month");
                double monthlyRevenue = rs.getDouble("monthly_revenue");
                // Convert month format from YYYY-MM to "Tháng X"
                String[] parts = month.split("-");
                int monthNumber = Integer.parseInt(parts[1]);
                String monthName = "Tháng " + monthNumber;
                revenue.put(monthName, monthlyRevenue);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return revenue;
    }

    public static List<Map<String, String>> getRecentOrders() throws SQLException {
        List<Map<String, String>> orders = new ArrayList<>();
        Connection connection = DatabaseUtil.getConnection();

        String sql = "SELECT o.order_id, c.full_name AS customer_name, o.order_date, o.total_price " +
                     "FROM Orders o " +
                     "JOIN Customers c ON o.customer_id = c.customer_id " +
                     "ORDER BY o.order_date DESC " +
                     "LIMIT 10";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, String> order = new HashMap<>();
                order.put("id", "ORD-" + rs.getInt("order_id"));
                order.put("customer", rs.getString("customer_name"));
                order.put("date", rs.getDate("order_date").toString());
                order.put("total", String.format("%,.2f", rs.getDouble("total_price")));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public static List<Map<String, String>> getRecentOrders(int month) throws SQLException {
        List<Map<String, String>> orders = new ArrayList<>();
        Connection connection = DatabaseUtil.getConnection();

        String sql = "SELECT o.order_id, c.full_name AS customer_name, o.order_date, o.total_price " +
                     "FROM Orders o " +
                     "JOIN Customers c ON o.customer_id = c.customer_id " +
                     "WHERE MONTH(o.order_date) = ? " +
                     "ORDER BY o.order_date DESC " +
                     "LIMIT 10";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, month);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> order = new HashMap<>();
                    order.put("id", "ORD-" + rs.getInt("order_id"));
                    order.put("customer", rs.getString("customer_name"));
                    order.put("date", rs.getDate("order_date").toString());
                    order.put("total", String.format("%,.2f", rs.getDouble("total_price")));
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public static List<OrderDetailRecord> getRecentOrderDetails(int orderId) throws SQLException {
        List<OrderDetailRecord> details = new ArrayList<>();
        Connection connection = DatabaseUtil.getConnection();

        String sql = "SELECT item_type, item_id, quantity, unit_price FROM OrderDetails WHERE order_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String itemType = rs.getString("item_type");
                    int itemId = rs.getInt("item_id");
                    int quantity = rs.getInt("quantity");
                    double price = rs.getDouble("price");
                    details.add(new OrderDetailRecord(orderId, itemType, itemId, quantity, price));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }

    public static Map<String, Number> getRevenueByDateRange(LocalDate startDate, LocalDate endDate) throws SQLException {
        Map<String, Number> revenue = new HashMap<>();
        Connection connection = DatabaseUtil.getConnection();

        String sql = "SELECT DATE_FORMAT(order_date, '%Y-%m') AS month, SUM(total_price) AS monthly_revenue " +
                     "FROM Orders " +
                     "WHERE order_date BETWEEN ? AND ? " +
                     "GROUP BY month " +
                     "ORDER BY month ASC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String month = rs.getString("month");
                    double monthlyRevenue = rs.getDouble("monthly_revenue");
                    // Convert month format from YYYY-MM to "Tháng X"
                    String[] parts = month.split("-");
                    int monthNumber = Integer.parseInt(parts[1]);
                    String monthName = "Tháng " + monthNumber;
                    revenue.put(monthName, monthlyRevenue);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return revenue;
    }

    public static List<Map<String, String>> getOrdersByDateRange(LocalDate startDate, LocalDate endDate) throws SQLException {
        List<Map<String, String>> orders = new ArrayList<>();
        Connection connection = DatabaseUtil.getConnection();

        String sql = "SELECT o.order_id, c.full_name AS customer_name, o.order_date, o.total_price " +
                     "FROM Orders o " +
                     "JOIN Customers c ON o.customer_id = c.customer_id " +
                     "WHERE o.order_date BETWEEN ? AND ? " +
                     "ORDER BY o.order_date DESC " +
                     "LIMIT 50"; // Limit to 50 orders to avoid performance issues

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> order = new HashMap<>();
                    order.put("id", "ORD-" + rs.getInt("order_id"));
                    order.put("customer", rs.getString("customer_name"));
                    order.put("date", rs.getDate("order_date").toString());
                    order.put("total", String.format("%,.2f", rs.getDouble("total_price")));
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
}


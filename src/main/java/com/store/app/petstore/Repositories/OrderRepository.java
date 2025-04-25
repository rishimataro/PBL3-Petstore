package com.store.app.petstore.Repositories;
import com.store.app.petstore.Models.DatabaseManager;
import com.store.app.petstore.Models.Entities.Order;
import com.store.app.petstore.Models.Records.OrderSummaryRecord;
import com.store.app.petstore.Utils.Mappers.OrderMapper;
import com.store.app.petstore.Utils.ParserModel;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository {

    public boolean addOrder(Order order) {
        String sql = "INSERT INTO Orders (customer_id, total_price, order_date, staff_id, discount_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            OrderMapper.bindOrderParams(stmt, order);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateOrder(Order order) {
        String sql = "UPDATE Orders SET customer_id = ?, total_price = ?, order_date = ?, staff_id = ?, discount_id = ? WHERE order_id = ?";
        try (Connection conn = DatabaseManager.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            OrderMapper.bindOrderParams(stmt, order);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Order getOrderById(int orderId) {
        String sql = "SELECT * FROM Orders WHERE order_id = ?";
        try (Connection conn = DatabaseManager.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return ParserModel.getOrder(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Order> getAllOrders() {
        String sql = "SELECT * FROM Orders";
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DatabaseManager.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                orders.add(ParserModel.getOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public boolean deleteOrder(int orderId) {
        String sql = "DELETE FROM Orders WHERE order_id = ?";
        try (Connection conn = DatabaseManager.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }return false;}


    public List<OrderSummaryRecord> getOrderHistory(
            String phoneFilter,
            String nameFilter,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        // Bắt đầu xây dựng câu SQL
        StringBuilder sql = new StringBuilder("""
        SELECT o.order_id, 
               c.full_name AS customer_name,
               s.full_name AS staff_name, 
               o.order_date, 
               o.total_price, 
               d.value AS discount_value, 
               d.discount_type
        FROM Orders o
        JOIN Staffs s ON o.staff_id = s.staff_id
        LEFT JOIN Discounts d ON o.discount_id = d.discount_id
        LEFT JOIN Customers c ON o.customer_id = c.customer_id
        WHERE 1 = 1
    """);

        List<Object> params = new ArrayList<>();

        if (!isBlank(phoneFilter)) {
            sql.append(" AND c.phone LIKE ?");
            params.add("%" + phoneFilter + "%");
            System.out.println("Added phone filter: " + "%" + phoneFilter + "%");
        }

        if (!isBlank(nameFilter)) {
            sql.append(" AND c.full_name LIKE ?");
            params.add("%" + nameFilter + "%");
            System.out.println("Added name filter: " + "%" + nameFilter + "%");
        }

        if (startDate != null) {
            sql.append(" AND o.order_date >= ?");
            params.add(startDate);
            System.out.println("Added startDate filter: " + startDate);
        }

        if (endDate != null) {
            sql.append(" AND o.order_date <= ?");
            params.add(endDate);
            System.out.println("Added endDate filter: " + endDate);
        }

        sql.append(" ORDER BY o.order_id");

        System.out.println("Generated SQL: " + sql.toString());
        System.out.println("Parameters: " + params);

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            System.out.println("Executing query...");
            try (ResultSet rs = stmt.executeQuery()) {
                return mapToOrderSummaryList(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error executing SQL query: " + e.getMessage());
            throw new RuntimeException("Failed to fetch order history", e);
        }
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    // Helper function để chuyển ResultSet thành danh sách OrderSummaryRecord
    private List<OrderSummaryRecord> mapToOrderSummaryList(ResultSet rs) throws SQLException {
        List<OrderSummaryRecord> list = new ArrayList<>();
        System.out.println("Mapping result set to OrderSummaryRecord...");
        while (rs.next()) {
            OrderSummaryRecord record = new OrderSummaryRecord(
                    rs.getInt("order_id"),
                    rs.getString("customer_name"),
                    rs.getString("staff_name"),
                    rs.getTimestamp("order_date").toLocalDateTime(),
                    rs.getLong("total_price"),
                    rs.getDouble("discount_value"),
                    rs.getString("discount_type")
            );
            list.add(record);
            System.out.println(record.toString());
        }
        System.out.println("Mapped " + list.size() + " records.");
        return list;
    }

}

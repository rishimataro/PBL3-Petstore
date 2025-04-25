package com.store.app.petstore.Repositories;

import com.store.app.petstore.Models.DatabaseManager;
import com.store.app.petstore.Models.Entities.OrderDetail;
import com.store.app.petstore.Models.Records.TotalTodayRecord;
import com.store.app.petstore.Utils.Mappers.OrderDetailMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailRepository {

    public boolean addOrderDetail(OrderDetail orderDetail) {
        String sql = "INSERT INTO OrderDetails (order_id, item_type, item_id, quantity, unit_price) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            OrderDetailMapper.bindOrderDetailParams(stmt, orderDetail);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateOrderDetail(OrderDetail orderDetail) {
        String sql = "UPDATE OrderDetails SET order_id = ?, item_type = ?, item_id = ?, quantity = ?, unit_price = ? WHERE order_detail_id = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            OrderDetailMapper.bindOrderDetailParams(stmt, orderDetail);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public OrderDetail getOrderDetailById(int orderDetailId) {
        String sql = "SELECT * FROM OrderDetails WHERE order_detail_id = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderDetailId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new OrderDetail(
                        rs.getInt("order_detail_id"),
                        rs.getInt("order_id"),
                        rs.getString("item_type"),
                        rs.getInt("item_id"),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<OrderDetail> getOrderDetailsByOrderId(int orderId) {
        String sql = "SELECT * FROM OrderDetails WHERE order_id = ?";
        List<OrderDetail> orderDetails = new ArrayList<>();
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                OrderDetail orderDetail = new OrderDetail(
                        rs.getInt("order_detail_id"),
                        rs.getInt("order_id"),
                        rs.getString("item_type"),
                        rs.getInt("item_id"),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price")
                );
                orderDetails.add(orderDetail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderDetails;
    }

    public boolean deleteOrderDetail(int orderDetailId) {
        String sql = "DELETE FROM OrderDetails WHERE order_detail_id = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderDetailId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public TotalTodayRecord getTotalTodayRecord() {
        String sql = """
            SELECT
                SUM(OrderDetails.unit_price * OrderDetails.quantity) AS total_price,
                SUM(CASE WHEN OrderDetails.item_type = 'pet' THEN OrderDetails.quantity ELSE 0 END) AS total_pet,
                SUM(CASE WHEN OrderDetails.item_type = 'product' THEN OrderDetails.quantity ELSE 0 END) AS total_product
            FROM
                OrderDetails
                    INNER JOIN
                Orders O ON OrderDetails.order_id = O.order_id
            WHERE
                DATE(O.order_date) = CURDATE();
            """;
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            return new TotalTodayRecord(rs.getLong("total_price"), rs.getInt("total_pet"), rs.getString("total_product"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

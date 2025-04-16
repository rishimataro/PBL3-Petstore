package com.store.app.petstore.Repositories;

import com.store.app.petstore.Models.DatabaseManager;
import com.store.app.petstore.Models.Entities.Order;
import com.store.app.petstore.Utils.Mappers.OrderMapper;
import com.store.app.petstore.Utils.ParserModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository {

    public boolean addOrder(Order order) {
        String sql = "INSERT INTO Orders (customer_id, total_price, order_date, staff_id, discount_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

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
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

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
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

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
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

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
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

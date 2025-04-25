package com.store.app.petstore.DAO.Impl;

import com.store.app.petstore.DAO.OrderDAO;
import com.store.app.petstore.Models.Entities.Order;
import com.store.app.petstore.Models.DatabaseManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDAOImpl implements OrderDAO {
    private final Connection connection;

    public OrderDAOImpl() {
        this.connection = DatabaseManager.connect();
    }

    @Override
    public List<Order> getAll() {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM orders";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                orders.add(extractOrderFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    @Override
    public Order getById(int id) {
        String query = "SELECT * FROM orders WHERE orderId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractOrderFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean create(Order order) {
        String query = "INSERT INTO orders (customerId, staffId, totalPrice, orderDate, discountId) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            setOrderParameters(pstmt, order);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        order.setOrderId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(Order order) {
        String query = "UPDATE orders SET customerId = ?, staffId = ?, totalPrice = ?, orderDate = ?, discountId = ? WHERE orderId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            setOrderParameters(pstmt, order);
            pstmt.setInt(6, order.getOrderId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String query = "DELETE FROM orders WHERE orderId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Order> getOrdersByUserId(int userId) {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM orders WHERE customerId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(extractOrderFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    @Override
    public List<Order> getOrdersByStatus(String status) {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM orders WHERE status = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, status);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(extractOrderFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    @Override
    public List<Order> getOrdersByDateRange(String startDate, String endDate) {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM orders WHERE orderDate BETWEEN ? AND ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(extractOrderFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    private Order extractOrderFromResultSet(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setOrderId(rs.getInt("orderId"));
        order.setCustomerId(rs.getInt("customerId"));
        order.setStaffId(rs.getInt("staffId"));
        order.setTotalPrice(rs.getDouble("totalPrice"));
        order.setOrderDate(rs.getTimestamp("orderDate").toLocalDateTime());
        order.setDiscountId(rs.getInt("discountId"));
        return order;
    }

    private void setOrderParameters(PreparedStatement pstmt, Order order) throws SQLException {
        pstmt.setInt(1, order.getCustomerId());
        pstmt.setInt(2, order.getStaffId());
        pstmt.setDouble(3, order.getTotalPrice());
        pstmt.setTimestamp(4, Timestamp.valueOf(order.getOrderDate()));
        pstmt.setInt(5, order.getDiscountId());
    }
} 
package com.store.app.petstore.DAO.Impl;

import com.store.app.petstore.DAO.OrderDetailDAO;
import com.store.app.petstore.Models.Entities.OrderDetail;
import com.store.app.petstore.Models.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailDAOImpl implements OrderDetailDAO {
    private final Connection connection;

    public OrderDetailDAOImpl() {
        this.connection = DatabaseManager.connect();
    }

    @Override
    public List<OrderDetail> getAll() {
        List<OrderDetail> orderDetails = new ArrayList<>();
        String query = "SELECT * FROM order_details";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                orderDetails.add(extractOrderDetailFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderDetails;
    }

    @Override
    public OrderDetail getById(int id) {
        String query = "SELECT * FROM order_details WHERE orderDetailId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractOrderDetailFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean create(OrderDetail orderDetail) {
        String query = "INSERT INTO order_details (orderId, itemType, itemId, quantity, unitPrice) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            setOrderDetailParameters(pstmt, orderDetail);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        orderDetail.setOrderDetailId(generatedKeys.getInt(1));
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
    public boolean update(OrderDetail orderDetail) {
        String query = "UPDATE order_details SET orderId = ?, itemType = ?, itemId = ?, quantity = ?, unitPrice = ? WHERE orderDetailId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            setOrderDetailParameters(pstmt, orderDetail);
            pstmt.setInt(6, orderDetail.getOrderDetailId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String query = "DELETE FROM order_details WHERE orderDetailId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<OrderDetail> getOrderDetailsByOrderId(int orderId) {
        List<OrderDetail> orderDetails = new ArrayList<>();
        String query = "SELECT * FROM order_details WHERE orderId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, orderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    orderDetails.add(extractOrderDetailFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderDetails;
    }

    @Override
    public List<OrderDetail> getOrderDetailsByProductId(int productId) {
        List<OrderDetail> orderDetails = new ArrayList<>();
        String query = "SELECT * FROM order_details WHERE itemId = ? AND itemType = 'PRODUCT'";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    orderDetails.add(extractOrderDetailFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderDetails;
    }

    private OrderDetail extractOrderDetailFromResultSet(ResultSet rs) throws SQLException {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderDetailId(rs.getInt("orderDetailId"));
        orderDetail.setOrderId(rs.getInt("orderId"));
        orderDetail.setItemType(rs.getString("itemType"));
        orderDetail.setItemId(rs.getInt("itemId"));
        orderDetail.setQuantity(rs.getInt("quantity"));
        orderDetail.setUnitPrice(rs.getDouble("unitPrice"));
        return orderDetail;
    }

    private void setOrderDetailParameters(PreparedStatement pstmt, OrderDetail orderDetail) throws SQLException {
        pstmt.setInt(1, orderDetail.getOrderId());
        pstmt.setString(2, orderDetail.getItemType());
        pstmt.setInt(3, orderDetail.getItemId());
        pstmt.setInt(4, orderDetail.getQuantity());
        pstmt.setDouble(5, orderDetail.getUnitPrice());
    }
} 
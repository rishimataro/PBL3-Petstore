package com.store.app.petstore.DAO;

import com.store.app.petstore.Models.Entities.OrderDetail;

import java.sql.*;
import java.util.ArrayList;

public class OrderDetailDAO {
    public static OrderDetailDAO getInstance() {
        return new OrderDetailDAO();
    }

    public static int insert(OrderDetail entity) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "INSERT INTO OrderDetails (order_id, item_type, item_id, quantity, unit_price) VALUES (?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setInt(1, entity.getOrderId());
            stmt.setString(2, entity.getItemType());
            stmt.setInt(3, entity.getItemId());
            stmt.setInt(4, entity.getQuantity());
            stmt.setDouble(5, entity.getUnitPrice());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return 0;
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
    }

    public static int update(OrderDetail entity) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "UPDATE OrderDetails SET order_id = ?, item_type = ?, item_id = ?, quantity = ?, unit_price = ? WHERE order_detail_id = ?";
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, entity.getOrderId());
            stmt.setString(2, entity.getItemType());
            stmt.setInt(3, entity.getItemId());
            stmt.setInt(4, entity.getQuantity());
            stmt.setDouble(5, entity.getUnitPrice());
            stmt.setInt(6, entity.getOrderDetailId());

            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DatabaseUtil.closeResources(stmt, conn);
        }
    }

    public static int delete(OrderDetail entity) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "DELETE FROM OrderDetails WHERE order_detail_id = ?";
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, entity.getOrderDetailId());

            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DatabaseUtil.closeResources(stmt, conn);
        }
    }

    public static ArrayList<OrderDetail> findAll() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<OrderDetail> orderDetails = new ArrayList<>();

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM OrderDetails";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrderDetailId(rs.getInt("order_detail_id"));
                orderDetail.setOrderId(rs.getInt("order_id"));
                orderDetail.setItemType(rs.getString("item_type"));
                orderDetail.setItemId(rs.getInt("item_id"));
                orderDetail.setQuantity(rs.getInt("quantity"));
                orderDetail.setUnitPrice(rs.getDouble("unit_price"));
                orderDetails.add(orderDetail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
        return orderDetails;
    }

    public static OrderDetail findById(Integer id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM OrderDetails WHERE order_detail_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrderDetailId(rs.getInt("order_detail_id"));
                orderDetail.setOrderId(rs.getInt("order_id"));
                orderDetail.setItemType(rs.getString("item_type"));
                orderDetail.setItemId(rs.getInt("item_id"));
                orderDetail.setQuantity(rs.getInt("quantity"));
                orderDetail.setUnitPrice(rs.getDouble("unit_price"));
                return orderDetail;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
        return null;
    }

    public static ArrayList<OrderDetail> findByCondition(String condition) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<OrderDetail> orderDetails = new ArrayList<>();

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM OrderDetails WHERE " + condition;
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrderDetailId(rs.getInt("order_detail_id"));
                orderDetail.setOrderId(rs.getInt("order_id"));
                orderDetail.setItemType(rs.getString("item_type"));
                orderDetail.setItemId(rs.getInt("item_id"));
                orderDetail.setQuantity(rs.getInt("quantity"));
                orderDetail.setUnitPrice(rs.getDouble("unit_price"));
                orderDetails.add(orderDetail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
        return orderDetails;
    }

    public static ArrayList<OrderDetail> findByOrderId(int orderId) {
        return findByCondition("order_id = " + orderId);
    }

    public static int getTotalItemsSold() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT SUM(quantity) as total_items FROM OrderDetails";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total_items");
            }
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
    }

    public static double getTotalRevenueByItemType(String itemType) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT SUM(quantity * unit_price) as total_revenue FROM OrderDetails WHERE item_type = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, itemType);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total_revenue");
            }
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
    }

    public static ArrayList<OrderDetail> getMostPopularItems(int limit) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<OrderDetail> popularItems = new ArrayList<>();

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT item_id, item_type, SUM(quantity) as total_quantity " +
                    "FROM OrderDetails " +
                    "GROUP BY item_id, item_type " +
                    "ORDER BY total_quantity DESC " +
                    "LIMIT ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, limit);
            rs = stmt.executeQuery();

            while (rs.next()) {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setItemId(rs.getInt("item_id"));
                orderDetail.setItemType(rs.getString("item_type"));
                orderDetail.setQuantity(rs.getInt("total_quantity"));
                popularItems.add(orderDetail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
        return popularItems;
    }

    public static ArrayList<OrderDetail> getOrderDetailsWithTotalPrice() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<OrderDetail> orderDetails = new ArrayList<>();

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT *, (quantity * unit_price) as total_price FROM OrderDetails";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrderDetailId(rs.getInt("order_detail_id"));
                orderDetail.setOrderId(rs.getInt("order_id"));
                orderDetail.setItemType(rs.getString("item_type"));
                orderDetail.setItemId(rs.getInt("item_id"));
                orderDetail.setQuantity(rs.getInt("quantity"));
                orderDetail.setUnitPrice(rs.getDouble("unit_price"));
                // Note: You might want to add a setTotalPrice method to OrderDetail class
                // orderDetail.setTotalPrice(rs.getDouble("total_price"));
                orderDetails.add(orderDetail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
        return orderDetails;
    }
}

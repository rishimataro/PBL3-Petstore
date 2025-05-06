package com.store.app.petstore.DAO;

import com.store.app.petstore.Models.Entities.Order;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class OrderDAO implements BaseDAO<Order, Integer> {
    public static OrderDAO getInstance() { 
        return new OrderDAO(); 
    }

    @Override
    public int insert(Order entity) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "INSERT INTO Orders (customer_id, staff_id, total_price, order_date, discount_id, isDelete) VALUES (?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setInt(1, entity.getCustomerId());
            stmt.setInt(2, entity.getStaffId());
            stmt.setDouble(3, entity.getTotalPrice());
            stmt.setTimestamp(4, Timestamp.valueOf(entity.getOrderDate()));
            if (entity.getDiscountId() > 0) {
                stmt.setInt(5, entity.getDiscountId());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }
            stmt.setBoolean(6, entity.isDeleted());
            
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

    @Override
    public int update(Order entity) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "UPDATE Orders SET customer_id = ?, staff_id = ?, total_price = ?, order_date = ?, discount_id = ?, isDelete = ? WHERE order_id = ?";
            stmt = conn.prepareStatement(sql);
            
            stmt.setInt(1, entity.getCustomerId());
            stmt.setInt(2, entity.getStaffId());
            stmt.setDouble(3, entity.getTotalPrice());
            stmt.setTimestamp(4, Timestamp.valueOf(entity.getOrderDate()));
            if (entity.getDiscountId() > 0) {
                stmt.setInt(5, entity.getDiscountId());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }
            stmt.setBoolean(6, entity.isDeleted());
            stmt.setInt(7, entity.getOrderId());

            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DatabaseUtil.closeResources(stmt, conn);
        }
    }

    @Override
    public int delete(Order entity) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "DELETE FROM Orders WHERE order_id = ?";
            stmt = conn.prepareStatement(sql);
            
            stmt.setInt(1, entity.getOrderId());
            
            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DatabaseUtil.closeResources(stmt, conn);
        }
    }

    @Override
    public ArrayList<Order> findAll() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<Order> orders = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM Orders";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("order_id"));
                order.setCustomerId(rs.getInt("customer_id"));
                order.setStaffId(rs.getInt("staff_id"));
                order.setTotalPrice(rs.getDouble("total_price"));
                order.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());
                order.setDiscountId(rs.getInt("discount_id"));
                order.setDeleted(rs.getBoolean("isDelete"));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
        return orders;
    }

    @Override
    public Order findById(Integer id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM Orders WHERE order_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("order_id"));
                order.setCustomerId(rs.getInt("customer_id"));
                order.setStaffId(rs.getInt("staff_id"));
                order.setTotalPrice(rs.getDouble("total_price"));
                order.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());
                order.setDiscountId(rs.getInt("discount_id"));
                order.setDeleted(rs.getBoolean("isDelete"));
                return order;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
        return null;
    }

    @Override
    public ArrayList<Order> findByCondition(String condition) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<Order> orders = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM Orders WHERE " + condition;
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("order_id"));
                order.setCustomerId(rs.getInt("customer_id"));
                order.setStaffId(rs.getInt("staff_id"));
                order.setTotalPrice(rs.getDouble("total_price"));
                order.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());
                order.setDiscountId(rs.getInt("discount_id"));
                order.setDeleted(rs.getBoolean("isDelete"));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
        return orders;
    }

    public ArrayList<Order> findByCustomerId(int customerId) {
        return findByCondition("customer_id = " + customerId);
    }

    public ArrayList<Order> findByStaffId(int staffId) {
        return findByCondition("staff_id = " + staffId);
    }

    public ArrayList<Order> findByDateRange(Timestamp startDate, Timestamp endDate) {
        return findByCondition("order_date BETWEEN '" + startDate + "' AND '" + endDate + "'");
    }

    public int getOrderCount() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT COUNT(*) as count FROM Orders";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count");
            }
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
    }

    public double getTotalRevenue() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT SUM(total_price) as total_revenue FROM Orders";
            stmt = conn.prepareStatement(sql);
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

    public double getAverageOrderValue() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT AVG(total_price) as average_value FROM Orders";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("average_value");
            }
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
    }
}

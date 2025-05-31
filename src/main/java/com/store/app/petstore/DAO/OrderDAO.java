package com.store.app.petstore.DAO;

import com.store.app.petstore.Models.Entities.Order;
import com.store.app.petstore.Models.Records.StaffRevenue;
import java.sql.*;
import java.util.ArrayList;

public class OrderDAO {
    public static OrderDAO getInstance() {
        return new OrderDAO();
    }

    public static int insert(Order entity) {
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

    public static int update(Order entity) {
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

    public static int delete(Order entity) {
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

    public static ArrayList<Order> findAll() {
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

    public static Order findById(Integer id) {
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

    public static ArrayList<Order> findByCondition(String condition) {
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

    public static ArrayList<Order> findByCustomerId(int customerId) {
        return findByCondition("customer_id = " + customerId);
    }

    public static ArrayList<Order> findByStaffId(int staffId) {
        return findByCondition("staff_id = " + staffId);
    }

    public static ArrayList<Order> findByDateRange(Timestamp startDate, Timestamp endDate) {
        return findByCondition("order_date BETWEEN '" + startDate + "' AND '" + endDate + "'");
    }

    public static int getOrderCount() {
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

    public static double getTotalRevenue() {
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

    public static double getAverageOrderValue() {
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

    /**
     * Calculates the total revenue for a given date range.
     *
     * @param startDate The start date (inclusive).
     * @param endDate   The end date (inclusive).
     * @return The total revenue within the date range.
     */
    public static double getTotalRevenueByDateRange(Timestamp startDate, Timestamp endDate) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        double totalRevenue = 0;

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT SUM(total_price) as total_revenue FROM Orders WHERE order_date BETWEEN ? AND ?";
            stmt = conn.prepareStatement(sql);
            stmt.setTimestamp(1, startDate);
            stmt.setTimestamp(2, endDate);
            rs = stmt.executeQuery();

            if (rs.next()) {
                totalRevenue = rs.getDouble("total_revenue");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
        return totalRevenue;
    }

    /**
     * Fetches monthly revenue data for a given date range.
     *
     * @param startDate The start date (inclusive).
     * @param endDate   The end date (inclusive).
     * @return An ArrayList of Object arrays, where each array contains the month (String) and total revenue for that month (Double).
     */
    public static ArrayList<Object[]> getMonthlyRevenueDataByDateRange(Timestamp startDate, Timestamp endDate) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<Object[]> monthlyRevenueData = new ArrayList<>();

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT DATE_FORMAT(order_date, '%Y-%m') as month, SUM(total_price) as monthly_revenue " +
                         "FROM Orders WHERE order_date BETWEEN ? AND ? " +
                         "GROUP BY month ORDER BY month";
            stmt = conn.prepareStatement(sql);
            stmt.setTimestamp(1, startDate);
            stmt.setTimestamp(2, endDate);
            rs = stmt.executeQuery();

            while (rs.next()) {
                monthlyRevenueData.add(new Object[]{rs.getString("month"), rs.getDouble("monthly_revenue")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
        return monthlyRevenueData;
    }

    /**
     * Fetches staff revenue data for a given date range.
     *
     * @param startDate The start date (inclusive).
     * @param endDate   The end date (inclusive).
     * @return An ArrayList of StaffRevenue objects.
     */
    public static ArrayList<StaffRevenue> getStaffRevenueDataByDateRange(Timestamp startDate, Timestamp endDate) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<StaffRevenue> staffRevenueData = new ArrayList<>();

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT o.order_id, s.full_name, SUM(o.total_price) as staff_total_revenue " +
                         "FROM Orders o JOIN Staffs s ON o.staff_id = s.staff_id " +
                         "WHERE o.order_date BETWEEN ? AND ? " +
                         "GROUP BY o.staff_id, o.order_id, s.full_name"; // Group by order_id to get individual orders per staff
            stmt = conn.prepareStatement(sql);
            stmt.setTimestamp(1, startDate);
            stmt.setTimestamp(2, endDate);
            rs = stmt.executeQuery();

            while (rs.next()) {
                // Assuming StaffRevenue class exists and has a constructor like StaffRevenue(String orderId, String staffName, String total)
                staffRevenueData.add(new StaffRevenue(rs.getString("order_id"), rs.getString("full_name"), String.valueOf(rs.getDouble("staff_total_revenue"))));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
        return staffRevenueData;
    }
}

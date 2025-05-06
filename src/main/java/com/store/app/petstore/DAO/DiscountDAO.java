package com.store.app.petstore.DAO;

import com.store.app.petstore.Models.Entities.Discount;
import com.store.app.petstore.Controllers.ControllerUtils;
import java.sql.*;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DiscountDAO implements BaseDAO<Discount, Integer> {
    public static DiscountDAO getInstance() { 
        return new DiscountDAO(); 
    }

    @Override
    public int insert(Discount entity) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "INSERT INTO Discounts (code, discount_type, value, start_date, end_date, min_order_value, max_discount_value) VALUES (?, ?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, entity.getCode());
            stmt.setString(2, entity.getDiscountType());
            stmt.setDouble(3, entity.getValue());
            stmt.setDate(4, Date.valueOf(entity.getStartDate()));
            stmt.setDate(5, Date.valueOf(entity.getEndDate()));
            stmt.setDouble(6, entity.getMinOrderValue());
            stmt.setDouble(7, entity.getMaxDiscountValue());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return 0;
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int discountId = generatedKeys.getInt(1);
                    entity.setDiscountId(discountId);
                    return discountId;
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
    public int update(Discount entity) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "UPDATE Discounts SET code = ?, discount_type = ?, value = ?, start_date = ?, end_date = ?, min_order_value = ?, max_discount_value = ? WHERE discount_id = ?";
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, entity.getCode());
            stmt.setString(2, entity.getDiscountType());
            stmt.setDouble(3, entity.getValue());
            stmt.setDate(4, Date.valueOf(entity.getStartDate()));
            stmt.setDate(5, Date.valueOf(entity.getEndDate()));
            stmt.setDouble(6, entity.getMinOrderValue());
            stmt.setDouble(7, entity.getMaxDiscountValue());
            stmt.setInt(8, entity.getDiscountId());
            
            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DatabaseUtil.closeResources(stmt, conn);
        }
    }

    @Override
    public int delete(Discount entity) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "DELETE FROM Discounts WHERE discount_id = ?";
            stmt = conn.prepareStatement(sql);
            
            stmt.setInt(1, entity.getDiscountId());
            
            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DatabaseUtil.closeResources(stmt, conn);
        }
    }

    @Override
    public ArrayList<Discount> findAll() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<Discount> discountList = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM Discounts";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Discount discount = new Discount(
                    rs.getInt("discount_id"),
                    rs.getString("code"),
                    rs.getString("discount_type"),
                    rs.getDouble("value"),
                    rs.getDate("start_date").toLocalDate(),
                    rs.getDate("end_date").toLocalDate(),
                    rs.getDouble("min_order_value"),
                    rs.getDouble("max_discount_value")
                );
                discountList.add(discount);
            }
            return discountList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
    }

    @Override
    public Discount findById(Integer id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM Discounts WHERE discount_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                Discount discount = new Discount();
                discount.setDiscountId(rs.getInt("discount_id"));
                discount.setCode(rs.getString("code"));
                discount.setDiscountType(rs.getString("discount_type"));
                discount.setValue(rs.getDouble("value"));
                discount.setStartDate(rs.getDate("start_date").toLocalDate());
                discount.setEndDate(rs.getDate("end_date").toLocalDate());
                discount.setMinOrderValue(rs.getDouble("min_order_value"));
                discount.setMaxDiscountValue(rs.getDouble("max_discount_value"));
                return discount;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
        return null;
    }

    @Override
    public ArrayList<Discount> findByCondition(String condition) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<Discount> discountList = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM Discounts WHERE " + condition;
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Discount discount = new Discount(
                    rs.getInt("discount_id"),
                    rs.getString("code"),
                    rs.getString("discount_type"),
                    rs.getDouble("value"),
                    rs.getDate("start_date").toLocalDate(),
                    rs.getDate("end_date").toLocalDate(),
                    rs.getDouble("min_order_value"),
                    rs.getDouble("max_discount_value")
                );
                discountList.add(discount);
            }
            return discountList.isEmpty() ? null : discountList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
    }

    public Discount findByCode(String code) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM Discounts WHERE code = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, code);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                String discountType = rs.getString("discount_type");
                // Chuyển đổi từ tiếng Việt sang tiếng Anh
                if (discountType.equals("phần trăm")) {
                    discountType = "percent";
                } else if (discountType.equals("cố định")) {
                    discountType = "fixed";
                }
                
                return new Discount(
                    rs.getInt("discount_id"),
                    rs.getString("code"),
                    discountType,
                    rs.getDouble("value"),
                    rs.getDate("start_date").toLocalDate(),
                    rs.getDate("end_date").toLocalDate(),
                    rs.getDouble("min_order_value"),
                    rs.getDouble("max_discount_value")
                );
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
    }

    public ArrayList<Discount> findActiveDiscounts() {
        return findByCondition("CURRENT_DATE BETWEEN start_date AND end_date");
    }

    public boolean isValidDiscount(Discount discount) {
        if (discount == null) return false;
        
        LocalDate today = LocalDate.now();
        return !today.isBefore(discount.getStartDate()) && 
               !today.isAfter(discount.getEndDate());
    }

    public String validateDiscount(Discount discount, double orderTotal) {
        if (discount == null) {
            return "Mã giảm giá không tồn tại!";
        }

        LocalDate today = LocalDate.now();
        
        if (today.isBefore(discount.getStartDate())) {
            return String.format("Mã giảm giá này sẽ có hiệu lực từ ngày %s", 
                discount.getStartDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }
        
        if (today.isAfter(discount.getEndDate())) {
            return String.format("Mã giảm giá này đã hết hạn từ ngày %s", 
                discount.getEndDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }

        if (orderTotal < discount.getMinOrderValue()) {
            return String.format("Đơn hàng phải có giá trị tối thiểu %s để áp dụng mã giảm giá!", 
                ControllerUtils.formatCurrency(discount.getMinOrderValue()));
        }

        return null; // null means valid
    }
}

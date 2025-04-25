package com.store.app.petstore.DAO.Impl;

import com.store.app.petstore.DAO.DiscountDAO;
import com.store.app.petstore.Models.Entities.Discount;
import com.store.app.petstore.Models.DatabaseManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DiscountDAOImpl implements DiscountDAO {
    private final Connection connection;

    public DiscountDAOImpl() {
        this.connection = DatabaseManager.connect();
    }

    @Override
    public List<Discount> getAll() {
        List<Discount> discounts = new ArrayList<>();
        String query = "SELECT * FROM discounts";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                discounts.add(extractDiscountFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return discounts;
    }

    @Override
    public Discount getById(int id) {
        String query = "SELECT * FROM discounts WHERE discountId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractDiscountFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean create(Discount discount) {
        String query = "INSERT INTO discounts (code, discountType, value, startDate, endDate, minOrderValue, maxDiscountValue) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            setDiscountParameters(pstmt, discount);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        discount.setDiscountId(generatedKeys.getInt(1));
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
    public boolean update(Discount discount) {
        String query = "UPDATE discounts SET code = ?, discountType = ?, value = ?, startDate = ?, endDate = ?, minOrderValue = ?, maxDiscountValue = ? WHERE discountId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            setDiscountParameters(pstmt, discount);
            pstmt.setInt(8, discount.getDiscountId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String query = "DELETE FROM discounts WHERE discountId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Discount> getActiveDiscounts() {
        List<Discount> discounts = new ArrayList<>();
        String query = "SELECT * FROM discounts WHERE startDate <= CURRENT_DATE AND endDate >= CURRENT_DATE";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                discounts.add(extractDiscountFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return discounts;
    }

    @Override
    public List<Discount> getDiscountsByType(String type) {
        List<Discount> discounts = new ArrayList<>();
        String query = "SELECT * FROM discounts WHERE discountType = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, type);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    discounts.add(extractDiscountFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return discounts;
    }

    @Override
    public List<Discount> getDiscountsByDateRange(String startDate, String endDate) {
        List<Discount> discounts = new ArrayList<>();
        String query = "SELECT * FROM discounts WHERE startDate >= ? AND endDate <= ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    discounts.add(extractDiscountFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return discounts;
    }

    private Discount extractDiscountFromResultSet(ResultSet rs) throws SQLException {
        Discount discount = new Discount();
        discount.setDiscountId(rs.getInt("discountId"));
        discount.setCode(rs.getString("code"));
        discount.setDiscountType(rs.getString("discountType"));
        discount.setValue(rs.getDouble("value"));
        discount.setStartDate(rs.getDate("startDate").toLocalDate());
        discount.setEndDate(rs.getDate("endDate").toLocalDate());
        discount.setMinOrderValue(rs.getDouble("minOrderValue"));
        discount.setMaxDiscountValue(rs.getDouble("maxDiscountValue"));
        return discount;
    }

    private void setDiscountParameters(PreparedStatement pstmt, Discount discount) throws SQLException {
        pstmt.setString(1, discount.getCode());
        pstmt.setString(2, discount.getDiscountType());
        pstmt.setDouble(3, discount.getValue());
        pstmt.setDate(4, Date.valueOf(discount.getStartDate()));
        pstmt.setDate(5, Date.valueOf(discount.getEndDate()));
        pstmt.setDouble(6, discount.getMinOrderValue());
        pstmt.setDouble(7, discount.getMaxDiscountValue());
    }
} 
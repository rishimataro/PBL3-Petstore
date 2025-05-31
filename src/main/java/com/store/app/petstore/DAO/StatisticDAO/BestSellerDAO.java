package com.store.app.petstore.DAO.StatisticDAO;

import com.store.app.petstore.DAO.DatabaseUtil;
import com.store.app.petstore.Models.Records.BestSellingItem;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BestSellerDAO {

    public static List<BestSellingItem> getBestSellingPets(LocalDate startDate, LocalDate endDate, String category) throws SQLException {
        List<BestSellingItem> result = new ArrayList<>();
        String sql = "SELECT p.name, SUM(od.quantity) AS total_quantity, DATE(o.order_date) AS sale_date " +
                "FROM Orders o " +
                "JOIN OrderDetails od ON o.order_id = od.order_id " +
                "JOIN Pets p ON od.item_id = p.pet_id " +
                "WHERE od.item_type = 'pet' " +
                "AND o.order_date BETWEEN ? AND ? " +
                "AND (? = 'Tất cả' OR p.type LIKE ?) " +
                "GROUP BY p.name, DATE(o.order_date) " +
                "ORDER BY sale_date ASC";

        Connection connection = DatabaseUtil.getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            stmt.setString(3, category);
            stmt.setString(4, category.equals("Tất cả") ? "%" : category + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("name");
                    int totalQuantity = rs.getInt("total_quantity");
                    LocalDate saleDate = rs.getDate("sale_date").toLocalDate();

                    BestSellingItem pet = new BestSellingItem(name, totalQuantity, saleDate);
                    result.add(pet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return result;
    }

    public static List<BestSellingItem> getBestSellingProducts(LocalDate startDate, LocalDate endDate, String category) throws SQLException {
        List<BestSellingItem> result = new ArrayList<>();
        String sql = "SELECT p.name, SUM(od.quantity) AS total_quantity, DATE(o.order_date) AS sale_date " +
                "FROM Orders o " +
                "JOIN OrderDetails od ON o.order_id = od.order_id " +
                "JOIN Pets p ON od.item_id = p.pet_id " +
                "WHERE od.item_type = 'product' " +
                "AND o.order_date BETWEEN ? AND ? " +
                "AND (? = 'Tất cả' OR p.type LIKE ?) " +
                "GROUP BY p.name, DATE(o.order_date) " +
                "ORDER BY sale_date ASC";

        Connection connection = DatabaseUtil.getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            stmt.setString(3, category);
            stmt.setString(4, category.equals("Tất cả") ? "%" : category + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("name");
                    int totalQuantity = rs.getInt("total_quantity");
                    LocalDate saleDate = rs.getDate("sale_date").toLocalDate();

                    BestSellingItem pet = new BestSellingItem(name, totalQuantity, saleDate);
                    result.add(pet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return result;
    }
}
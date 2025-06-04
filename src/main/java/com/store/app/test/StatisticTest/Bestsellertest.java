package com.store.app.test.StatisticTest;

import com.store.app.petstore.DAO.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;

public class Bestsellertest {


        // Hàm test lấy best selling pets với tham số startDate, endDate, category
        public static void testGetBestSellingPets(Connection connection, LocalDate startDate, LocalDate endDate, String category) {
            String sql = "SELECT p.name, SUM(od.quantity) AS total_quantity, DATE(o.order_date) AS sale_date " +
                    "FROM Orders o " +
                    "JOIN OrderDetails od ON o.order_id = od.order_id " +
                    "JOIN Pets p ON od.item_id = p.pet_id " +
                    "WHERE od.item_type = 'pet' " +
                    "AND o.order_date BETWEEN ? AND ? " +
                    "AND (? = 'Tất cả' OR p.type LIKE ?) " +
                    "GROUP BY p.name, DATE(o.order_date) " +
                    "ORDER BY sale_date ASC";

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setDate(1, java.sql.Date.valueOf(startDate));
                stmt.setDate(2, java.sql.Date.valueOf(endDate));
                stmt.setString(3, category);
                stmt.setString(4, category.equals("Tất cả") ? "%" : category + "%");

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String name = rs.getString("name");
                        int totalQuantity = rs.getInt("total_quantity");
                        LocalDate saleDate = rs.getDate("sale_date").toLocalDate();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Ví dụ chạy test
        public static void main(String[] args) {

            try (Connection connection = DatabaseUtil.getConnection()) {
                LocalDate startDate = LocalDate.of(2025, 5, 1);
                LocalDate endDate = LocalDate.of(2025, 5, 31);

                // Test với category = "Tất cả"
                testGetBestSellingPets(connection, startDate, endDate, "Tất cả");

                // Test với category = "Chó"
                testGetBestSellingPets(connection, startDate, endDate, "Chó");

            } catch (SQLException e) {
                e.printStackTrace();
            }
    }
}

package com.store.app.petstore.Models.Seeder;

import com.store.app.petstore.Models.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailTableSeeder {
    public static void seedOrderDetails(int count) {
        Random rand = new Random();

        try (Connection conn = DatabaseManager.connect()) {
            // Lấy danh sách order_id có thật trong bảng Orders
            List<Integer> orderIds = new ArrayList<>();
            Statement orderStmt = conn.createStatement();
            ResultSet rs = orderStmt.executeQuery("SELECT order_id FROM Orders");

            while (rs.next()) {
                orderIds.add(rs.getInt("order_id"));
            }

            if (orderIds.isEmpty()) {
                System.out.println("Không có đơn hàng nào để chèn chi tiết.");
                return;
            }

            String sql = "INSERT INTO OrderDetails (order_id, item_type, item_id, quantity, unit_price) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);

            for (int i = 0; i < count; i++) {
                int orderId = orderIds.get(rand.nextInt(orderIds.size())); // order_id tồn tại
                String itemType = rand.nextBoolean() ? "pet" : "product";
                int itemId = itemType.equals("pet") ? rand.nextInt(50) + 1 : rand.nextInt(20) + 1;
                int quantity = rand.nextInt(5) + 1;
                double unitPrice = 10 + rand.nextInt(91);

                ps.setInt(1, orderId);
                ps.setString(2, itemType);
                ps.setInt(3, itemId);
                ps.setInt(4, quantity);
                ps.setDouble(5, unitPrice);

                ps.executeUpdate();
            }

            System.out.println(count + " order details seeded successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
package com.store.app.petstore.Models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class OrderDetailSeeder {
    public static void seedOrderDetails(int count) {
        Random rand = new Random();

        try (Connection conn = ConnectJDBC.connect()) {
            String sql = "INSERT INTO Orderdetails (order_id, item_type, item_id, quantity, unit_price) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);

            for (int i = 0; i < count; i++) {
                int orderId = rand.nextInt(50) + 1; // Order ID from 1 to 50
                String itemType = rand.nextBoolean() ? "pet" : "product"; // Randomly pick item type
                int itemId = itemType.equals("pet") ? rand.nextInt(50) + 1 : rand.nextInt(20) + 1; // Pet ID from 1–50, Product ID from 1–20
                int quantity = rand.nextInt(5) + 1; // Quantity from 1 to 5
                double unitPrice = 10 + rand.nextInt(91); // Unit price from 10 to 100

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

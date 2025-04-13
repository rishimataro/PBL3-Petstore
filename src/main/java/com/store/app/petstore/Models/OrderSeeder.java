package com.store.app.petstore.Models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;
import java.util.Random;

public class OrderSeeder {
    public static void seedOrders(int count) {
        Random rand = new Random();

        try (Connection conn = ConnectJDBC.connect()) {
            String sql = "INSERT INTO Orders (customer_id, total_price, order_date, staff_id, discount_id) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);

            for (int i = 0; i < count; i++) {
                int customerId = rand.nextInt(20) + 1; // Assuming 20 customers
                double totalPrice = 50 + rand.nextInt(451); // From 50 to 500
                Date orderDate = new Date(System.currentTimeMillis() - rand.nextInt(100) * 24L * 60 * 60 * 1000); // Within last 100 days
                int staffId = rand.nextInt(10) + 1; // Assuming 10 staff members
                Integer discountId = rand.nextInt(100) < 30 ? rand.nextInt(20) + 1 : null; // 30% chance to have a discount (1-20)

                ps.setInt(1, customerId);
                ps.setDouble(2, totalPrice);
                ps.setDate(3, orderDate);
                ps.setInt(4, staffId);
                if (discountId != null) {
                    ps.setInt(5, discountId);
                } else {
                    ps.setNull(5, java.sql.Types.INTEGER);
                }

                ps.executeUpdate();
            }

            System.out.println(count + " orders seeded successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

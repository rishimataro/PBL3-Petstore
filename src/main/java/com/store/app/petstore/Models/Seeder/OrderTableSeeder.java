package com.store.app.petstore.Models.Seeder;

import com.store.app.petstore.Models.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OrderTableSeeder {
    public static void seedOrders(int count) {
        Random rand = new Random();

        try (Connection conn = DatabaseManager.connect()) {
            // Lấy danh sách discount_id có thật từ bảng Discounts
            List<Integer> discountIds = new ArrayList<>();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT discount_id FROM Discounts");
            while (rs.next()) {
                discountIds.add(rs.getInt("discount_id"));
            }

            String sql = "INSERT INTO Orders (customer_id, total_price, order_date, staff_id, discount_id, isDelete) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);

            for (int i = 0; i < count; i++) {
                int customerId = rand.nextInt(9) + 2; // 2 đến 10
                double totalPrice = 50 + rand.nextInt(451); // 50 đến 500
                Date orderDate = new Date(System.currentTimeMillis() - rand.nextInt(100) * 24L * 60 * 60 * 1000); // 100 ngày gần đây
                int staffId = rand.nextInt(4) + 125; // 125 đến 128

                // Chọn discount ngẫu nhiên trong danh sách (30% cơ hội)
                Integer discountId = null;
                if (!discountIds.isEmpty() && rand.nextInt(100) < 30) {
                    discountId = discountIds.get(rand.nextInt(discountIds.size()));
                }

                ps.setInt(1, customerId);
                ps.setDouble(2, totalPrice);
                ps.setDate(3, orderDate);
                ps.setInt(4, staffId);
                if (discountId != null) {
                    ps.setInt(5, discountId);
                } else {
                    ps.setNull(5, Types.INTEGER);
                }
                ps.setBoolean(6, false);

                ps.executeUpdate();
            }

            System.out.println(count + " orders seeded successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

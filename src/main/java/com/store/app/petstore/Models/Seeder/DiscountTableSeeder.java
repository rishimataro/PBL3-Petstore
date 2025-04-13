package com.store.app.petstore.Models.Seeder;

import com.store.app.petstore.Models.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;
import java.util.Calendar;
import java.util.Random;

public class DiscountTableSeeder {
    public DiscountTableSeeder() {
        int count = 50;
        String[] codes = {"SUMMER", "WINTER", "WELCOME", "VIP", "SALE", "LOVE", "PETDAY", "XMAS", "NEWYEAR", "LUCKY"};
        String[] types = {"phần trăm", "cố định"};
        String[] statuses = {"sắp diễn ra", "đang hoạt động", "kết thúc"};

        try (Connection conn = DatabaseManager.connect()) {
            String sql = "INSERT INTO Discounts (code, discount_type, value, status, start_date, end_date, min_order_value) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);

            Random rand = new Random();

            for (int i = 0; i < count; i++) {
                String code = codes[rand.nextInt(codes.length)] + i;
                String type = types[rand.nextInt(types.length)];
                String status = statuses[rand.nextInt(statuses.length)];

                double value;
                if (type.equals("phần trăm")) {
                    value = 5 + rand.nextInt(26); // 5% - 30%
                } else {
                    value = 10 + rand.nextInt(91); // 10 - 100
                }

                // Dates: today and +X days
                Calendar calendar = Calendar.getInstance();
                Date startDate = new Date(calendar.getTimeInMillis());
                calendar.add(Calendar.DAY_OF_MONTH, rand.nextInt(30) + 1);
                Date endDate = new Date(calendar.getTimeInMillis());

                double minOrderValue = rand.nextBoolean() ? 0.0 : 20.0 + rand.nextInt(81); // 0.0 or 20-100

                ps.setString(1, code);
                ps.setString(2, type);
                ps.setDouble(3, value);
                ps.setString(4, status);
                ps.setDate(5, startDate);
                ps.setDate(6, endDate);
                ps.setDouble(7, minOrderValue);

                ps.executeUpdate();

                System.out.println("Inserted: " + code + " | Type: " + type + " | Value: " + value +
                        " | Status: " + status + " | Min Order: $" + minOrderValue);
            }

            System.out.println("Successfully inserted " + count + " discounts into the database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

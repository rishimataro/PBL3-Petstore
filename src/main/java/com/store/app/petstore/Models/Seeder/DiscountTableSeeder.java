package com.store.app.petstore.Models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;
import java.util.Calendar;
import java.util.Random;

public class DiscountSeeder {
    public static void seedDiscounts(int count) {
        String[] codes = {"SUMMER", "WINTER", "WELCOME", "VIP", "SALE", "LOVE", "PETDAY", "XMAS", "NEWYEAR", "LUCKY"};
        String[] types = {"phần trăm", "cố định"};

        try (Connection conn = ConnectJDBC.connect()) {
            String sql = "INSERT INTO Discounts (code, discount_type, value, max_discount_value, start_date, end_date, min_order_value) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);

            Random rand = new Random();

            for (int i = 0; i < count; i++) {
                String code = codes[rand.nextInt(codes.length)] + i;
                String type = types[rand.nextInt(types.length)];

                double value;
                double maxDiscountValue = 0;

                if (type.equals("phần trăm")) {
                    value = 5 + rand.nextInt(26); // 5% - 30%
                    maxDiscountValue = (rand.nextInt(10) + 1) * 10; // 10, 20, ..., 100
                } else {
                    value = 10 + rand.nextInt(91); // 10 - 100
                }

                Calendar calendar = Calendar.getInstance();
                Date startDate = new Date(calendar.getTimeInMillis());
                calendar.add(Calendar.DAY_OF_MONTH, rand.nextInt(30) + 1);
                Date endDate = new Date(calendar.getTimeInMillis());

                double minOrderValue = rand.nextBoolean() ? 0.0 : 20.0 + rand.nextInt(81); // 0.0 hoặc 20-100

                ps.setString(1, code);
                ps.setString(2, type);
                ps.setDouble(3, value);
                ps.setDouble(4, maxDiscountValue);
                ps.setDate(5, startDate);
                ps.setDate(6, endDate);
                ps.setDouble(7, minOrderValue);

                ps.executeUpdate();

                System.out.println("Đã thêm: " + code + " | Loại: " + type + " | Giá trị: " + value +
                        " | Giảm tối đa: " + maxDiscountValue + " | Giá trị đơn hàng tối thiểu: đ" + minOrderValue);
            }

            System.out.println("Đã thêm thành công " + count + " mã giảm giá vào cơ sở dữ liệu.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

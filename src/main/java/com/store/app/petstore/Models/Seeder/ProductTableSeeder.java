package com.store.app.petstore.Models.Seeder;

import com.github.javafaker.Faker;
import com.store.app.petstore.Models.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Random;

public class ProductTableSeeder {
    public ProductTableSeeder() {
        Faker faker = new Faker(new Locale("vi"));
        Random random = new Random();

        String[] categories = {"phụ kiện", "thức ăn", "đồ chơi"};

        try (Connection conn = DatabaseManager.connect()) {
            String sql = "INSERT INTO Products (name, category, stock, description, image_url) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            for (int i = 1; i <= 50; i++) {
                String name = "Sản phẩm cho " + faker.animal().name();
                String category = "Miêu tả sản phẩm " + i;
                int stock = random.nextInt(50);
                String description = faker.lorem().sentence(10);
                String imageUrl = "https://example.com/images/product-" + i + ".jpg";

                stmt.setString(1, name);
                stmt.setString(2, category);
                stmt.setInt(3, stock);
                stmt.setString(4, description);
                stmt.setString(5, imageUrl);
                System.out.println(stmt.toString());
                stmt.executeUpdate();
            }

            System.out.println("Seeder thành công: đã thêm 50 sản phẩm hợp lệ.");
            DatabaseManager.closeConnection(conn);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Lỗi khi chèn dữ liệu: " + e.getMessage());
        }

    }
}

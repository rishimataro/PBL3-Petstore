package com.store.app.petstore.Models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class PetSeeder {
    public static void seedPets(int count) {
        String[] petNames = {"Milo", "Luna", "Coco", "Buddy", "Charlie", "Bella", "Max", "Lucy", "Rocky", "Daisy"};
        String[] types = {"chó", "mèo"};
        String[] breeds = {"Golden Retriever", "Siamese", "Ba Tư", "Bulldog", "Chihuahua"};
        String[] sex = {"đực", "cái"};
        int[] price = {500000, 1500000, 200000, 750000, 1000000};

        String[] descriptions = {
                "thân thiện và hiếu động", "thích được ôm ấp", "rất năng động",
                "hiền lành và điềm tĩnh", "phù hợp với không gian nhỏ"
        };
        String[] imageUrls = {
                "https://example.com/dog1.jpg", "https://example.com/cat1.jpg",
                "https://example.com/dog2.jpg", "https://example.com/cat2.jpg"
        };

        try (Connection conn = ConnectJDBC.connect()) {
            String sql = "INSERT INTO Pets (name, type, breed, sex, age, price, description, image_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);

            Random rand = new Random();
            for (int i = 0; i < count; i++) {
                String name = petNames[rand.nextInt(petNames.length)] + i;
                String type = types[rand.nextInt(types.length)];
                String breed = breeds[rand.nextInt(breeds.length)];
                String selectedSex = sex[rand.nextInt(sex.length)];
                int age = rand.nextInt(10) + 1;
                int selectedPrice = price[rand.nextInt(price.length)];
                String description = descriptions[rand.nextInt(descriptions.length)];
                String imageUrl = imageUrls[rand.nextInt(imageUrls.length)];

                ps.setString(1, name);
                ps.setString(2, type);
                ps.setString(3, breed);
                ps.setString(4, selectedSex);
                ps.setInt(5, age);
                ps.setInt(6, selectedPrice);
                ps.setString(7, description);
                ps.setString(8, imageUrl);

                ps.executeUpdate();

                System.out.println("Đã thêm: " + name + " | " + type + " | " + breed + " | Giới tính: " + selectedSex + " | Tuổi: " + age + " | Giá: " + selectedPrice + "đ");
            }

            System.out.println("Đã thêm thành công " + count + " thú cưng vào cơ sở dữ liệu.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

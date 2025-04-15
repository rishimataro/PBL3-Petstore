package com.store.app.petstore.Models.Seeder;

import com.store.app.petstore.Models.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

public class PetTableSeeder {
    public PetTableSeeder() {
        String[] petNames = {"Milo", "Luna", "Coco", "Buddy", "Charlie", "Bella", "Max", "Lucy", "Rocky", "Daisy"};
        String[] types = {"Dog", "Cat"};
        String[] breeds = {"Golden Retriever", "Siamese", "Persian", "Bulldog", "Chihuahua"};
        String[] descriptions = {
                "Friendly and playful", "Loves to cuddle", "Very active",
                "Gentle and calm", "Perfect for small spaces"
        };
        String[] imageUrls = {
                "https://example.com/dog1.jpg", "https://example.com/cat1.jpg",
                "https://example.com/dog2.jpg", "https://example.com/cat2.jpg"
        };

        try (Connection conn = DatabaseManager.connect()) {
            String sql = "INSERT INTO Pets (pet_id ,name, type, breed, age, description, image_url) VALUES (?, ?, ?, ?, ?, ?, ?)";
            String delete = "DELETE FROM Pets";
            PreparedStatement ps = conn.prepareStatement(sql);
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);

            Random rand = new Random();
            for (int i = 0; i < 50; i++) {
                String name = petNames[rand.nextInt(petNames.length)] + i;
                String type = types[rand.nextInt(types.length)];
                String breed = breeds[rand.nextInt(breeds.length)];
                int age = rand.nextInt(10) + 1;
                String description = descriptions[rand.nextInt(descriptions.length)];
                String imageUrl = imageUrls[rand.nextInt(imageUrls.length)];

                ps.setInt(1, i + 1);
                ps.setString(2, name);
                ps.setString(3, type);
                ps.setString(4, breed);
                ps.setInt(5, age);
                ps.setString(6, description);
                ps.setString(7, imageUrl);

                System.out.println(ps.toString());
                ps.executeUpdate();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

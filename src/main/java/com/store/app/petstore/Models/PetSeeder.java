package com.store.app.petstore.Models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class PetSeeder {
    public static void seedPets(int count) {
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

        try (Connection conn = conn.connect()) {
            String sql = "INSERT INTO Pets (name, type, breed, age, description, image_url) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);

            Random rand = new Random();
            for (int i = 0; i < count; i++) {
                String name = petNames[rand.nextInt(petNames.length)] + i;
                String type = types[rand.nextInt(types.length)];
                String breed = breeds[rand.nextInt(breeds.length)];
                int age = rand.nextInt(10) + 1;
                String description = descriptions[rand.nextInt(descriptions.length)];
                String imageUrl = imageUrls[rand.nextInt(imageUrls.length)];

                ps.setString(1, name);
                ps.setString(2, type);
                ps.setString(3, breed);
                ps.setInt(4, age);
                ps.setString(5, description);
                ps.setString(6, imageUrl);

                ps.executeUpdate();

                System.out.println("Inserted: " + name + " | " + type + " | " + breed + " | Age: " + age);
            }

            System.out.println("Successfully inserted " + count + " pets into the database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

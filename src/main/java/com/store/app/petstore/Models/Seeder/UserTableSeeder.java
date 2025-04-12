package com.store.app.petstore.Models.Seeder;

import com.github.javafaker.Faker;
import com.store.app.petstore.Models.DatabaseManager;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.HashSet;
import java.util.Set;

public class UserTableSeeder {
    public UserTableSeeder() {
        try (
            Connection conn = DatabaseManager.connect();) {
            String sql = "INSERT INTO Customers (customer_id, full_name, phone) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            Faker faker = new Faker();
            Set<String> generatedPhones = new HashSet<>();
            int customerId = 1;
            while (customerId <= 10) {
                String fullName = faker.name().fullName();

                String phone;
                do {
                    phone = "0" + faker.number().digits(10);
                } while (!generatedPhones.add(phone));

                stmt.setInt(1, customerId);
                stmt.setString(2, fullName);
                stmt.setString(3, phone);
                stmt.executeUpdate();

                customerId++;
            }

            System.out.println("✅ Created customers successfully.");
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }
}

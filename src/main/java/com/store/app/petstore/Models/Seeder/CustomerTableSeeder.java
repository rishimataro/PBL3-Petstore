package com.store.app.petstore.Models.Seeder;

import com.github.javafaker.Faker;
import com.store.app.petstore.Models.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Locale;

public class CustomerTableSeeder {
    public CustomerTableSeeder() {
        try (Connection conn = DatabaseManager.connect();) {
            String sqlDelete = "DELETE FROM Customers";
            Statement stmt2 = conn.createStatement();
            stmt2.execute(sqlDelete);
            String sql = "INSERT INTO Customers (customer_id, full_name, phone) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            Faker faker = new Faker(Locale.forLanguageTag("vi"));
            int customerId = 1;
            while (customerId <= 10) {
                String fullName = faker.name().fullName();
                String phone = faker.phoneNumber().cellPhone().replaceAll("[^0-9]", "").replaceAll(" ", "");

                stmt.setInt(1, customerId);
                stmt.setString(2, fullName);
                stmt.setString(3, phone);
                stmt.executeUpdate();

                customerId++;
            }

            System.out.println("✅ Created customers successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

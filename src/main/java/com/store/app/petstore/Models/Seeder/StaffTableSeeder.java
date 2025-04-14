package com.store.app.petstore.Models.Seeder;

import com.github.javafaker.Faker;
import com.store.app.petstore.Models.DatabaseManager;

import java.sql.*;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class StaffTableSeeder {
    public StaffTableSeeder() {
        Faker faker = new Faker(new Locale("vi"));
        String[] roles = {"thu ngân", "bán hàng", "chăm sóc thú cưng", "tư vấn"};
        String sql = "INSERT INTO Staffs (user_id, full_name, phone, email, salary, hire_date, role, isActive) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            Set<String> usedPhones = new HashSet<>();
            for (int i = 11; i < 50; i++) {
                int userId = faker.random().nextInt(10);
                String fullName = faker.name().fullName();
                String phone;
                do {
                    phone = "0" + faker.number().digits(10);
                } while (!usedPhones.add(phone));

                String email = faker.internet().emailAddress();
                double salary = faker.number().randomDouble(2, 50000, 1000000);
                Date hireDate = new Date(faker.date().past(2000, TimeUnit.DAYS).getTime());
                String role = roles[faker.random().nextInt(roles.length)];
                int isActive = faker.bool().bool() ? 1 : 0;

                stmt.setInt(1, userId);
                stmt.setString(2, fullName);
                stmt.setString(3, phone);
                stmt.setString(4, email);
                stmt.setDouble(5, salary);
                stmt.setDate(6, hireDate);
                stmt.setString(7, role);
                stmt.setInt(8, isActive);

                stmt.executeUpdate();
            }

            System.out.println("✅ Inserted 10 fake staff records successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

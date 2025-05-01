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
        String sql = "INSERT INTO Staffs (staff_id, user_id, full_name, phone, email, salary, hire_date, role, isActive) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String deleteSql = "DELETE FROM Staffs";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             Statement delete = conn.createStatement();) {
            delete.executeUpdate(deleteSql);
            int userId = 2;
            for (int i = 0; i < roles.length; i++) {
                String fullName = faker.name().fullName();
                String phone = faker.phoneNumber().phoneNumber().trim();
                String email = faker.internet().emailAddress();
                double salary = faker.number().randomDouble(2, 50000, 1000000);
                Date hireDate = new Date(faker.date().past(2000, TimeUnit.DAYS).getTime());
                String role = roles[i];
                int isActive = 1;

                stmt.setInt(1, i);
                stmt.setInt(2, userId++);
                stmt.setString(3, fullName);
                stmt.setString(4, phone);
                stmt.setString(5, email);
                stmt.setDouble(6, salary);
                stmt.setDate(7, hireDate);
                stmt.setString(8, role);
                stmt.setInt(9, isActive);

                stmt.executeUpdate();
            }

            System.out.println("Inserted fake staff records successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}

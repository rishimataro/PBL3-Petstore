package com.store.app.petstore.Models.Seeder;

import com.github.javafaker.Faker;
import com.store.app.petstore.Models.DatabaseManager;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class UserTableSeeder {
    public UserTableSeeder() {
        String sql = "INSERT INTO Users ( username, password, role, created_at, isActive) VALUES ( ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            Faker faker = new Faker(new Locale("vi"));
            {
                String username = faker.name().firstName().toLowerCase() + faker.number().numberBetween(100, 999);
                String password = BCrypt.hashpw("o3Trjrndn8Fqb", BCrypt.gensalt());
                Date date = new Date(System.currentTimeMillis());
                String role = "quản trị viên";

                stmt.setString(1, username);
                stmt.setString(2, password);
                stmt.setString(3, role);
                stmt.setDate(4, date);
                stmt.setInt(5, 1);
                stmt.executeUpdate();
            }

            {
                String username = faker.name().firstName().toLowerCase() + faker.number().numberBetween(100, 999);
                String password = BCrypt.hashpw("FKBw5saUi5q6M", BCrypt.gensalt());
                Date date = new Date(System.currentTimeMillis());
                String role = "nhân viên";

                stmt.setString(1, username);
                stmt.setString(2, password);
                stmt.setString(3, role);
                stmt.setDate(4, date);
                stmt.setInt(5, 1);
                stmt.executeUpdate();
            }

            System.out.println("✅ Created customers successfully.");
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }
}

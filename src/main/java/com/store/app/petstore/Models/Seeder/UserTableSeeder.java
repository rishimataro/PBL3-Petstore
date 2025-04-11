package com.store.app.petstore.Models.Seeder;

import com.store.app.petstore.Models.DatabaseManager;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class UserTableSeeder {
    public UserTableSeeder() {
        try (
                Connection conn = DatabaseManager.connect();) {
            String sql = "INSERT INTO Users (username, password, role, isActive) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            // Admin account
            String adminUsername = "admin";
            String adminPassword = BCrypt.hashpw("admin123", BCrypt.gensalt());
            stmt.setString(1, adminUsername);
            stmt.setString(2, adminPassword);
            stmt.setString(3, "admin");
            stmt.setBoolean(4, true);
            stmt.executeUpdate();

            // Staff account
            String staffUsername = "staff";
            String staffPassword = BCrypt.hashpw("staff123", BCrypt.gensalt());
            stmt.setString(1, staffUsername);
            stmt.setString(2, staffPassword);
            stmt.setString(3, "staff");
            stmt.setBoolean(4, true);
            stmt.executeUpdate();

            System.out.println("✅ Created admin and staff users successfully.");
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }
}

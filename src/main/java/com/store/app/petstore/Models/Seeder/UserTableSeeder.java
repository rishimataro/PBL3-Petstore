package com.store.app.petstore.Models.Seeder;

import com.store.app.petstore.Models.DatabaseManager;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class UserTableSeeder {
    public UserTableSeeder() {
        String deleteSql = "DELETE FROM Users";
        String insertSql = "INSERT INTO Users (user_id, username, password, role, created_at, isActive, image_url) VALUES (?, ?, ?, ?, ?, ?, ?)";
        int userId = 1;
        try (Connection conn = DatabaseManager.connect();
                Statement deleteStmt = conn.createStatement();
                PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
            deleteStmt.executeUpdate(deleteSql);
            {
                String username = "admin";
                String password = BCrypt.hashpw("o3Trjrndn8Fqb", BCrypt.gensalt());
                Date date = new Date(System.currentTimeMillis());
                String role = "quản trị viên";

                insertStmt.setInt(1, userId++);
                insertStmt.setString(2, username);
                insertStmt.setString(3, password);
                insertStmt.setString(4, role);
                insertStmt.setDate(5, date);
                insertStmt.setInt(6, 1);
                insertStmt.executeUpdate();
            }
            {
                String username = "user1";
                String password = BCrypt.hashpw("FKBw5saUi5q6M", BCrypt.gensalt());
                Date date = new Date(System.currentTimeMillis());
                String role = "nhân viên";
                String imageUrl = "/Images/User/user1.jpg";

                insertStmt.setInt(1, userId++);
                insertStmt.setString(2, username);
                insertStmt.setString(3, password);
                insertStmt.setString(4, role);
                insertStmt.setDate(5, date);
                insertStmt.setInt(6, 1);
                insertStmt.setString(7, imageUrl);
                insertStmt.executeUpdate();
            }

            {
                String username = "user2";
                String password = BCrypt.hashpw("FKBw5saUi5q6M", BCrypt.gensalt());
                Date date = new Date(System.currentTimeMillis());
                String role = "nhân viên";
                String imageUrl = "/Images/User/user2.jpg";

                insertStmt.setInt(1, userId++);
                insertStmt.setString(2, username);
                insertStmt.setString(3, password);
                insertStmt.setString(4, role);
                insertStmt.setDate(5, date);
                insertStmt.setInt(6, 1);
                insertStmt.setString(7, imageUrl);
                insertStmt.executeUpdate();
            }
            {
                String username = "user3";
                String password = BCrypt.hashpw("FKBw5saUi5q6M", BCrypt.gensalt());
                Date date = new Date(System.currentTimeMillis());
                String role = "nhân viên";
                String imageUrl = "/Images/User/user3.jpg";

                insertStmt.setInt(1, userId++);
                insertStmt.setString(2, username);
                insertStmt.setString(3, password);
                insertStmt.setString(4, role);
                insertStmt.setDate(5, date);
                insertStmt.setInt(6, 1);
                insertStmt.setString(7, imageUrl);
                insertStmt.executeUpdate();
            }
            {
                String username = "user4";
                String password = BCrypt.hashpw("FKBw5saUi5q6M", BCrypt.gensalt());
                Date date = new Date(System.currentTimeMillis());
                String role = "nhân viên";
                String imageUrl = "/Images/User/user4.jpg";

                insertStmt.setInt(1, userId++);
                insertStmt.setString(2, username);
                insertStmt.setString(3, password);
                insertStmt.setString(4, role);
                insertStmt.setDate(5, date);
                insertStmt.setInt(6, 1);
                insertStmt.setString(7, imageUrl);
                insertStmt.executeUpdate();
            }
            System.out.println("✅ Recreated Users table successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

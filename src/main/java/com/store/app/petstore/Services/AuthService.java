package com.store.app.petstore.Services;

import com.store.app.petstore.Models.DatabaseManager;
import com.store.app.petstore.Models.Entities.User;
import com.store.app.petstore.Sessions.SessionManager;
import com.store.app.petstore.Utils.Mappers.UserMapper;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class AuthService {
    public boolean login(String username, String password) {
        String sql = "SELECT * FROM Users WHERE username = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("Login Successful!");
                String hashedPassword = rs.getString("password");
                System.out.println(hashedPassword);
                System.out.println(password);
                if (BCrypt.checkpw(password, hashedPassword)) {
                    User user = UserMapper.fromResultSet(rs);
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Login Failed!");
        return false;
    }

    public void logout() {
        SessionManager.clear();
    }

    public String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    public boolean resetPassword(String email, String newPassword) {
        String sql = "SELECT s.*, u.user_id FROM Staffs s " +
                    "JOIN Users u ON s.user_id = u.user_id " +
                    "WHERE s.email = ? AND s.isActive = 1";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                
                // Update the user's password using user_id
                String updateSql = "UPDATE Users SET password = ? WHERE user_id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setString(1, hashPassword(newPassword));
                    updateStmt.setInt(2, userId);
                    updateStmt.executeUpdate();
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

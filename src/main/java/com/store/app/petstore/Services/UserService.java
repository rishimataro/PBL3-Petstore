package com.store.app.petstore.Services;

import com.store.app.petstore.Models.DatabaseManager;
import com.store.app.petstore.Models.Entities.User;
import javafx.application.Platform;

import java.sql.*;
import java.util.concurrent.CompletableFuture;

public class UserService {
    public User getUserById(int userId) {
        String sql = "SELECT * FROM Users WHERE user_id = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getInt("isActive") > 0
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateUser(User user) {
        String sql = "UPDATE Users SET username = ?, password = ?, role = ?, isActive = ? WHERE user_id = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole());
            stmt.setBoolean(4, user.isActive());
            stmt.setInt(5, user.getUserId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteUser(int userId) {
        String sql = "DELETE FROM Users WHERE user_id = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public CompletableFuture<User> getUserByIdAsync(int userId) {
        return CompletableFuture.supplyAsync(() -> getUserById(userId));
    }

    public CompletableFuture<Void> updateUserAsync(User user) {
        return CompletableFuture.runAsync(() -> updateUser(user));
    }

    public CompletableFuture<Void> deleteUserAsync(int userId) {
        return CompletableFuture.runAsync(() -> deleteUser(userId));
    }
}


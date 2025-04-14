package com.store.app.petstore.Services;

import com.store.app.petstore.Models.DatabaseManager;
import com.store.app.petstore.Models.Entities.User;
import com.store.app.petstore.Sessions.SessionManager;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class AuthService {
    public boolean login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                if (BCrypt.checkpw(password, hashedPassword)) {
                    User user = new User(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            hashedPassword,
                            rs.getString("role"),
                            rs.getTimestamp("created_at").toLocalDateTime(),
                            rs.getInt("isActive") > 0
                    );
                    SessionManager.setCurrentUser(user);
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void logout() {
        SessionManager.clear();
    }

    public String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }
}

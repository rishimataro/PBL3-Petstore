package com.store.app.petstore.Services;

import com.store.app.petstore.Models.DatabaseManager;
import com.store.app.petstore.Models.Entities.User;
import com.store.app.petstore.Sessions.SessionManager;
import com.store.app.petstore.Utils.Mappers.UserMapper;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

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
}

package com.store.app.petstore.DAO;

import com.store.app.petstore.Models.Entities.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;

public class UserDAO implements BaseDAO<User, Integer> {
    public static final int isDuplicate = -1;

    public static UserDAO getInstance() { 
        return new UserDAO(); 
    }

    public static String encode(String password) {
//        return Base64.getEncoder().encodeToString(password.getBytes());
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

//    public static String decode(String password) {
//        return new String(Base64.getDecoder().decode(password));
//    }

    public static boolean verify(String password, String hashed) {
        return BCrypt.checkpw(password, hashed);
    }

    @Override
    public int insert(User entity) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "INSERT INTO Users (username, password, role, image_url) VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, entity.getUsername());
            stmt.setString(2, encode(entity.getPassword()));
            stmt.setString(3, entity.getRole());
            stmt.setString(4, entity.getImageUrl());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return 0;
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
    }

    @Override
    public int update(User entity) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "UPDATE Users SET username = ?, password = ?, role = ?, image_url = ? WHERE user_id = ?";
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, entity.getUsername());
            stmt.setString(2, encode(entity.getPassword()));
            stmt.setString(3, entity.getRole());
            stmt.setString(4, entity.getImageUrl());
            stmt.setInt(5, entity.getUserId());
            
            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DatabaseUtil.closeResources(stmt, conn);
        }
    }

    public int update(User entity, boolean isUsernameChanged) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "UPDATE Users SET username = ?, password = ?, role = ?, image_url = ? WHERE user_id = ?";
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, entity.getUsername());
            stmt.setString(2, encode(entity.getPassword()));
            stmt.setString(3, entity.getRole());
            stmt.setString(4, entity.getImageUrl());
            stmt.setInt(5, entity.getUserId());
            
            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DatabaseUtil.closeResources(stmt, conn);
        }
    }

    @Override
    public int delete(User entity) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "DELETE FROM Users WHERE user_id = ?";
            stmt = conn.prepareStatement(sql);
            
            stmt.setInt(1, entity.getUserId());
            
            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DatabaseUtil.closeResources(stmt, conn);
        }
    }

    @Override
    public ArrayList<User> findAll() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<User> users = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM Users";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                user.setImageUrl(rs.getString("image_url"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
        return users;
    }

    @Override
    public User findById(Integer id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM Users WHERE user_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                user.setImageUrl(rs.getString("image_url"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
        return null;
    }

    @Override
    public ArrayList<User> findByCondition(String condition) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<User> users = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM Users WHERE " + condition;
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                user.setImageUrl(rs.getString("image_url"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
        return users;
    }

    public User findByUsername(String username) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM Users WHERE username = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                user.setImageUrl(rs.getString("image_url"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
        return null;
    }

    public User findByEmail(String email) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            // Kiểm tra email có tồn tại trong bảng Staffs không
            conn = DatabaseUtil.getConnection();
            String checkSql = "SELECT * FROM Staffs WHERE email = ?";
            stmt = conn.prepareStatement(checkSql);
            stmt.setString(1, email);
            rs = stmt.executeQuery();
            
            // Nếu email tồn tại, lấy thông tin user
            String sql = "SELECT u.* FROM Users u " +
                        "JOIN Staffs s ON u.user_id = s.user_id " +
                        "WHERE s.email = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                user.setImageUrl(rs.getString("image_url"));
                return user;
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
        return null;
    }

    public boolean checkDuplicate(String username, String role) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT COUNT(*) FROM Users WHERE username = ? AND role = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, role);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
        return false;
    }

    public boolean checkUserID(User entity) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT COUNT(*) FROM Users WHERE user_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, entity.getUserId());
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
        return false;
    }
}

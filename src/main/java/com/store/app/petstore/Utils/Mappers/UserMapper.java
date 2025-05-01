package com.store.app.petstore.Utils.Mappers;

import com.store.app.petstore.Models.Entities.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper {
    public static User fromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        user.setActive(rs.getBoolean("isActive"));
        user.setImageUrl(rs.getString("image_url"));
        return user;
    }
}

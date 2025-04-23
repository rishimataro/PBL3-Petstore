package com.store.app.petstore.Repositories;

import com.store.app.petstore.Models.DatabaseManager;
import com.store.app.petstore.Models.Entities.User;
import com.store.app.petstore.Utils.ParserModel;
import javafx.concurrent.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository implements BaseRepository<User> {
    @Override
    public Task<List<User>> findAll() throws SQLException {
        return new Task<>() {
            @Override
            protected List<User> call() throws Exception {
                List<User> users = new ArrayList<>();
                String sql = "SELECT * FROM Users";
                try (Connection conn = DatabaseManager.connect();
                     PreparedStatement stmt = conn.prepareStatement(sql);) {
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        users.add(ParserModel.getUser(rs));
                    }
                    return users;
                }
            }
        };
    }

    @Override
    public Task<Optional<User>> findById(int id) {
        return new Task<>() {
            @Override
            protected Optional<User> call() throws Exception {
                String sql = "SELECT * FROM Users WHERE id = ?";
                try (Connection conn = DatabaseManager.connect();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, id);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        return Optional.of(ParserModel.getUser(rs));
                    }
                    return Optional.empty();
                }
            }
        };
    }

    @Override
    public Task<Boolean> save(User entity) {
        return new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                String sql = "INSERT INTO Users (username, password, role, created_at, isActive) VALUES (?, ?, ?, ?)";
                try (Connection conn = DatabaseManager.connect();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, entity.getUsername());
                    stmt.setString(2, entity.getPassword());
                    stmt.setString(3, entity.getRole());
                    stmt.setTimestamp(4, Timestamp.valueOf(entity.getCreatedAt()));
                    stmt.setBoolean(5, entity.isActive());
                    return stmt.executeUpdate() > 0;
                }
            }
        };
    }

    @Override
    public Task<Boolean> update(User entity) {
        return new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                String sql = "UPDATE Users SET password = ?, role = ?, is_active = ? WHERE id = ?";
                try (Connection conn = DatabaseManager.connect();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, entity.getPassword());
                    stmt.setString(2, entity.getRole());
                    stmt.setBoolean(3, entity.isActive());
                    stmt.setInt(4, entity.getUserId());
                    return stmt.executeUpdate() > 0;
                }
            }
        };
    }

    @Override
    public Task<Boolean> deleteById(int id) {
        return new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                String sql = "DELETE FROM Users WHERE id = ?";
                try (Connection conn = DatabaseManager.connect();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, id);
                    return stmt.executeUpdate() > 0;
                }
            }
        };
    }
}

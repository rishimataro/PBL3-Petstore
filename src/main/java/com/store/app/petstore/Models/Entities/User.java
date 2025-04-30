package com.store.app.petstore.Models.Entities;

import com.store.app.petstore.Models.BaseModel;

import java.time.LocalDateTime;

public class User extends BaseModel {
    private int userId;
    private String username;
    private String password;
    private String role;
    private LocalDateTime createdAt;
    private boolean isActive;
    private String imageUrl;

    public static final String ROLE_USER = "nhân viên";
    public static final String ROLE_ADMIN = "quản trị viên";

    public static final boolean ACTIVE = true;
    public static final boolean NO_ACTIVE = false;

    public User() {}

    public User(String username, String password, String role, LocalDateTime createdAt, boolean isActive) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.createdAt = createdAt;
        this.isActive = isActive;
    }

    public User(int userId, String username, String password, String role, LocalDateTime createdAt, boolean isActive) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.createdAt = createdAt;
        this.isActive = isActive;
    }

    public User(int userId, String username, String password, String role, boolean isActive) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.createdAt = LocalDateTime.now();
        this.isActive = isActive;
    }

    // Getters
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isActive() { return isActive; }
    public String getImageUrl() { return imageUrl; }

    // Setters
    public void setUserId(int userId) { this.userId = userId; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setActive(boolean active) { isActive = active; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}

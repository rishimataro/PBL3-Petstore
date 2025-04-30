package com.store.app.test;

import com.store.app.petstore.DAO.UserDAO;
import com.store.app.petstore.Models.Entities.User;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class testDAO {
    public static void main(String[] args) {
        UserDAO userDAO = UserDAO.getInstance();
        
        // Test 1: Insert new user
        System.out.println("=== Test 1: Insert User ===");
        User newUser = new User();
        newUser.setUsername("testuser");
        newUser.setPassword("testpass123");
        newUser.setRole("CUSTOMER");
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setActive(true);
        
        int userId = userDAO.insert(newUser);
        System.out.println("Inserted user ID: " + userId);
        
        // Test 2: Find user by ID
        System.out.println("\n=== Test 2: Find User by ID ===");
        User foundUser = userDAO.findById(userId);
        System.out.println("Found user: " + foundUser.getUsername());
        
        // Test 3: Update user
        System.out.println("\n=== Test 3: Update User ===");
        foundUser.setPassword("newpassword123");
        userDAO.update(foundUser);
        User updatedUser = userDAO.findById(userId);
        System.out.println("Updated password (decoded): " + updatedUser.getPassword());
        
        // Test 4: Find all users
        System.out.println("\n=== Test 4: Find All Users ===");
        ArrayList<User> allUsers = userDAO.findAll();
        System.out.println("Total users found: " + allUsers.size());
        
        // Test 5: Find by username
        System.out.println("\n=== Test 5: Find by Username ===");
        User userByUsername = userDAO.findByUsername("testuser");
        System.out.println("Found user by username: " + (userByUsername != null ? "Yes" : "No"));
        
        // Test 6: Test duplicate insertion
        System.out.println("\n=== Test 6: Test Duplicate Insert ===");
        User duplicateUser = new User();
        duplicateUser.setUsername("testuser");
        duplicateUser.setPassword("testpass123");
        duplicateUser.setRole("CUSTOMER");
        duplicateUser.setCreatedAt(LocalDateTime.now());
        duplicateUser.setActive(true);
        
        int resultDuplicate = userDAO.insert(duplicateUser);
        System.out.println("Duplicate insert result: " + (resultDuplicate == UserDAO.isDuplicate ? "Duplicate detected" : "Unexpected result"));
        
        // Test 7: Delete user
        System.out.println("\n=== Test 7: Delete User ===");
        userDAO.delete(foundUser);
        User deletedUser = userDAO.findById(userId);
        System.out.println("User deleted: " + (deletedUser == null ? "Yes" : "No"));
        
        // Test 8: Password encoding/decoding
        System.out.println("\n=== Test 8: Password Encoding/Decoding ===");
        String originalPassword = "mySecretPassword";
        String encoded = UserDAO.encode(originalPassword);
        String decoded = UserDAO.decode(encoded);
        System.out.println("Original: " + originalPassword);
        System.out.println("Encoded: " + encoded);
        System.out.println("Decoded: " + decoded);
        System.out.println("Encoding/Decoding test passed: " + originalPassword.equals(decoded));
        
        System.out.println("\n=== All tests completed ===");
    }
}

package com.store.app.petstore.DAO;

import com.store.app.petstore.Models.Entities.User;
import java.util.List;

public interface UserDAO extends BaseDAO<User> {
    User getUserByUsername(String username);
    User getUserByEmail(String email);
    boolean updatePassword(int userId, String newPassword);
} 
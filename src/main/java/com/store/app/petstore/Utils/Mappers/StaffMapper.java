package com.store.app.petstore.Utils.Mappers;

import com.store.app.petstore.DAO.UserDAO;
import com.store.app.petstore.Models.Entities.Staff;
import com.store.app.petstore.Models.Entities.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class StaffMapper {
    public static Staff fromResutlSet(ResultSet rs) throws SQLException {
        try {
            int staffId = rs.getInt("staff_id");
            int userId = rs.getInt("user_id");
            String fullName = rs.getString("full_name");
            String phone = rs.getString("phone");
            String email = rs.getString("email");
            String address = rs.getString("address");
            double salary = rs.getDouble("salary");
            Timestamp hireDate = rs.getTimestamp("hire_date");
            String staffRole = rs.getString("role");
            boolean isActive = rs.getBoolean("isActive");

            User user = UserDAO.findById(userId);
            if (user == null) {
                user = new User();
                user.setUserId(userId);
                user.setUsername("unknown");
                user.setPassword("");
                user.setRole(User.ROLE_USER);
                user.setActive(true);
            }
            Staff staff = new Staff();
            staff.setUserId(userId);
            staff.setUsername(user.getUsername());
            staff.setPassword(user.getPassword());
            staff.setRole(user.getRole());
            staff.setCreatedAt(user.getCreatedAt());
            staff.setActive(user.isActive());
            staff.setImageUrl(user.getImageUrl());

            staff.setStaffId(staffId);
            staff.setFullName(fullName);
            staff.setPhone(phone);
            staff.setEmail(email);

            staff.setAddress(address != null ? address : "");
            staff.setSalary(salary);

            if (hireDate != null) {
                staff.setHireDate(hireDate.toLocalDateTime());
            } else {
                staff.setHireDate(java.time.LocalDateTime.now());
            }

            staff.setRole(staffRole);
            staff.setActive(isActive);

            return staff;
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException("Error mapping staff: " + e.getMessage(), e);
        }
    }
}

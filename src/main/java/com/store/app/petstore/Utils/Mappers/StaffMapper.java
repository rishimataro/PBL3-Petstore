package com.store.app.petstore.Utils.Mappers;

import com.store.app.petstore.Models.Entities.Staff;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StaffMapper {
    public static Staff fromResutlSet(ResultSet rs) throws SQLException {
        Staff staff = new Staff();
        staff.setStaffId(rs.getInt("staff_id"));
        staff.setUserId(rs.getInt("user_id"));
        staff.setFullName(rs.getString("full_name"));
        staff.setPhone(rs.getString("phone"));
        staff.setEmail(rs.getString("email"));
        staff.setSalary(rs.getDouble("salary"));

        staff.setHireDate(rs.getTimestamp("hire_date").toLocalDateTime());

        staff.setRole(rs.getString("role"));
        staff.setActive(rs.getBoolean("isActive"));
        return staff;
    }
}

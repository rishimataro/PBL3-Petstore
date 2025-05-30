package com.store.app.petstore.DAO;

import com.store.app.petstore.Models.Entities.Staff;
import com.store.app.petstore.Models.Entities.User;
import com.store.app.petstore.Utils.Mappers.StaffMapper;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class StaffDAO {
    private static StaffDAO instance;

    public static StaffDAO getInstance() {
        if (instance == null) {
            instance = new StaffDAO();
        }
        return instance;
    }

    public static Staff findById(Integer id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        if (id == null || id <= 0) {
            return null;
        }

        try {
            conn = DatabaseUtil.getConnection();

            String sql = "SELECT * FROM Staffs WHERE staff_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            rs = stmt.executeQuery();

            if (rs.next()) {
                Staff staff = StaffMapper.fromResutlSet(rs);
                if (staff != null) {
                    return staff;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
        return null;
    }

    public static Staff findByUserId(Integer userId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM Staffs WHERE user_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                // Create a copy of all the data we need from the ResultSet
                int staffId = rs.getInt("staff_id");
                String fullName = rs.getString("full_name");
                String phone = rs.getString("phone");
                String email = rs.getString("email");
                String address = rs.getString("address");
                double salary = rs.getDouble("salary");
                Timestamp hireDate = rs.getTimestamp("hire_date");
                String role = rs.getString("role");
                boolean isActive = rs.getBoolean("isActive");

                DatabaseUtil.closeResources(rs, stmt, conn);

                Staff staff = new Staff();
                staff.setStaffId(staffId);
                staff.setUserId(userId);
                staff.setFullName(fullName);
                staff.setPhone(phone);
                staff.setEmail(email);
                staff.setAddress(address != null ? address : "");
                staff.setSalary(salary);
                if (hireDate != null) {
                    staff.setHireDate(hireDate.toLocalDateTime());
                } else {
                    staff.setHireDate(LocalDateTime.now());
                }
                staff.setRole(role);
                staff.setActive(isActive);

                User user = UserDAO.findById(userId);
                if (user != null) {
                    staff.setUsername(user.getUsername());
                    staff.setPassword(user.getPassword());
                    staff.setCreatedAt(user.getCreatedAt());
                    staff.setImageUrl(user.getImageUrl());
                }

                return staff;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
        return null;
    }

    public static ArrayList<Staff> findByCondition(String condition) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<Staff> staffs = new ArrayList<>();

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM Staffs WHERE " + condition;
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            ArrayList<Integer> staffIds = new ArrayList<>();
            while (rs.next()) {
                staffIds.add(rs.getInt("staff_id"));
            }

            DatabaseUtil.closeResources(rs, stmt, conn);

            for (Integer staffId : staffIds) {
                Staff staff = findById(staffId);
                if (staff != null) {
                    staffs.add(staff);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
        return staffs;
    }

    public static int insert(Staff entity) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "INSERT INTO Staffs (user_id, full_name, phone, email, address, salary, hire_date, role, isActive) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, entity.getUserId());
            stmt.setString(2, entity.getFullName());
            stmt.setString(3, entity.getPhone());
            stmt.setString(4, entity.getEmail());
            stmt.setString(5, entity.getAddress());
            stmt.setDouble(6, entity.getSalary());
            stmt.setTimestamp(7, Timestamp.valueOf(entity.getHireDate()));
            stmt.setString(8, entity.getRole());
            stmt.setBoolean(9, entity.isActive());

            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DatabaseUtil.closeResources(stmt, conn);
        }
    }

    public static int update(Staff entity) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "UPDATE Staffs SET full_name = ?, phone = ?, email = ?, address = ?, salary = ?, hire_date = ?, role = ?, isActive = ? WHERE staff_id = ?";
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, entity.getFullName());
            stmt.setString(2, entity.getPhone());
            stmt.setString(3, entity.getEmail());
            stmt.setString(4, entity.getAddress());
            stmt.setDouble(5, entity.getSalary());
            stmt.setTimestamp(6, Timestamp.valueOf(entity.getHireDate()));
            stmt.setString(7, entity.getRole());
            stmt.setBoolean(8, entity.isActive());
            stmt.setInt(9, entity.getStaffId());

            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DatabaseUtil.closeResources(stmt, conn);
        }
    }

    public static int delete(Staff entity) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "DELETE FROM Staffs WHERE staff_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, entity.getStaffId());

            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DatabaseUtil.closeResources(stmt, conn);
        }
    }

    public static ArrayList<Staff> findAll() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<Staff> staffs = new ArrayList<>();

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM Staffs";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Staff staff = new Staff();
                staff.setStaffId(rs.getInt("staff_id"));
                staff.setUserId(rs.getInt("user_id"));
                staff.setFullName(rs.getString("full_name"));
                staff.setPhone(rs.getString("phone"));
                staff.setEmail(rs.getString("email"));
                staff.setAddress(rs.getString("address"));
                staff.setSalary(rs.getDouble("salary"));
                staff.setHireDate(rs.getTimestamp("hire_date").toLocalDateTime());
                staff.setRole(rs.getString("role"));
                staff.setActive(rs.getInt("isActive") == 1);
                staffs.add(staff);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
        return staffs;
    }

    public static ArrayList<Staff> findActiveStaff() {
        return findByCondition("isActive = 1");
    }

    public static ArrayList<Staff> findInactiveStaff() {
        return findByCondition("isActive = 0");
    }

    // tim kiem thao ten, email, sdt --> search
    public static ArrayList<Staff> searchStaff(String keyword) {
        String searchPattern = "'%" + keyword + "%'";
        return findByCondition("full_name LIKE " + searchPattern + " OR email LIKE " + searchPattern + " OR phone LIKE " + searchPattern);
    }

    // so luong nhan vien
    public static int getStaffCount() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT COUNT(*) as count FROM Staffs";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
    }
}

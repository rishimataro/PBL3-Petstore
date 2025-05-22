package com.store.app.petstore.DAO;

import com.store.app.petstore.Models.Entities.Staff;
import com.store.app.petstore.Utils.Mappers.StaffMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM Staffs WHERE staff_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return StaffMapper.fromResutlSet(rs);
            }
        } catch (SQLException e) {
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
                return StaffMapper.fromResutlSet(rs);
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

            while (rs.next()) {
                staffs.add(StaffMapper.fromResutlSet(rs));
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
            stmt.setTimestamp(7, java.sql.Timestamp.valueOf(entity.getHireDate()));
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
            stmt.setTimestamp(6, java.sql.Timestamp.valueOf(entity.getHireDate()));
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

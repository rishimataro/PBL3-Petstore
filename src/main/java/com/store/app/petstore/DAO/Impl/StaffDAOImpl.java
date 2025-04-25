package com.store.app.petstore.DAO.Impl;

import com.store.app.petstore.DAO.StaffDAO;
import com.store.app.petstore.Models.Entities.Staff;
import com.store.app.petstore.Models.DatabaseManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StaffDAOImpl implements StaffDAO {
    private final Connection connection;

    public StaffDAOImpl() {
        this.connection = DatabaseManager.connect();
    }

    @Override
    public List<Staff> getAll() {
        List<Staff> staffList = new ArrayList<>();
        String query = "SELECT * FROM staff";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                staffList.add(extractStaffFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return staffList;
    }

    @Override
    public Staff getById(int id) {
        String query = "SELECT * FROM staff WHERE staffId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractStaffFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean create(Staff staff) {
        String query = "INSERT INTO staff (userId, fullName, phone, email, salary, hireDate, role, isActive) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            setStaffParameters(pstmt, staff);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        staff.setStaffId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(Staff staff) {
        String query = "UPDATE staff SET userId = ?, fullName = ?, phone = ?, email = ?, salary = ?, hireDate = ?, role = ?, isActive = ? WHERE staffId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            setStaffParameters(pstmt, staff);
            pstmt.setInt(9, staff.getStaffId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String query = "DELETE FROM staff WHERE staffId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Staff getStaffByEmail(String email) {
        String query = "SELECT * FROM staff WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractStaffFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Staff getStaffByPhone(String phone) {
        String query = "SELECT * FROM staff WHERE phone = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, phone);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractStaffFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Staff> searchStaff(String keyword) {
        List<Staff> staffList = new ArrayList<>();
        String query = "SELECT * FROM staff WHERE fullName LIKE ? OR email LIKE ? OR phone LIKE ? OR role LIKE ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            String searchPattern = "%" + keyword + "%";
            for (int i = 1; i <= 4; i++) {
                pstmt.setString(i, searchPattern);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    staffList.add(extractStaffFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return staffList;
    }

    private Staff extractStaffFromResultSet(ResultSet rs) throws SQLException {
        Staff staff = new Staff();
        staff.setStaffId(rs.getInt("staffId"));
        staff.setUserId(rs.getInt("userId"));
        staff.setFullName(rs.getString("fullName"));
        staff.setPhone(rs.getString("phone"));
        staff.setEmail(rs.getString("email"));
        staff.setSalary(rs.getDouble("salary"));
        staff.setHireDate(rs.getTimestamp("hireDate").toLocalDateTime());
        staff.setRole(rs.getString("role"));
        staff.setActive(rs.getBoolean("isActive"));
        return staff;
    }

    private void setStaffParameters(PreparedStatement pstmt, Staff staff) throws SQLException {
        pstmt.setInt(1, staff.getUserId());
        pstmt.setString(2, staff.getFullName());
        pstmt.setString(3, staff.getPhone());
        pstmt.setString(4, staff.getEmail());
        pstmt.setDouble(5, staff.getSalary());
        pstmt.setTimestamp(6, Timestamp.valueOf(staff.getHireDate()));
        pstmt.setString(7, staff.getRole());
        pstmt.setBoolean(8, staff.isActive());
    }
} 
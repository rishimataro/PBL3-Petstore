package com.store.app.petstore.Repositories;

import com.store.app.petstore.Models.DatabaseManager;
import com.store.app.petstore.Models.Entities.Staff;
import com.store.app.petstore.Utils.ParserModel;
import javafx.concurrent.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StaffRepository implements BaseRepository<Staff> {
    @Override
    public Task<List<Staff>> findAll() throws SQLException {
        return new Task<>() {
            @Override
            protected List<Staff> call() throws Exception {
                List<Staff> staffList = new ArrayList<>();
                String query = "SELECT * FROM Staffs";
                try (Connection conn = DatabaseManager.connect();
                     PreparedStatement stmt = conn.prepareStatement(query);
                     ResultSet rs = stmt.executeQuery()) {

                    while (rs.next()) {
                        staffList.add(ParserModel.getStaff(rs));
                    }
                }
                return staffList;
            }
        };
    }

    @Override
    public Task<Optional<Staff>> findById(int id) {
        return new Task<>() {
            @Override
            protected Optional<Staff> call() throws Exception {
                String query = "SELECT * FROM Staffs WHERE staff_id = ?";
                try (Connection conn = DatabaseManager.connect();
                     PreparedStatement stmt = conn.prepareStatement(query)) {

                    stmt.setInt(1, id);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        return Optional.of(ParserModel.getStaff(rs));
                    } else {
                        return Optional.empty();
                    }
                }
            }
        };
    }

    @Override
    public Task<Boolean> save(Staff staff) {
        return new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                String query = """
                        INSERT INTO Staffs( full_name, phone, email, salary, hire_date, role, isActive)
                        VALUES (?, ?, ?, ?, ?, ?, ?)
                        """;
                try (Connection conn = DatabaseManager.connect();
                     PreparedStatement stmt = conn.prepareStatement(query)) {

                    stmt.setString(1, staff.getFullName());
                    stmt.setString(2, staff.getPhone());
                    stmt.setString(3, staff.getEmail());
                    stmt.setDouble(4, staff.getSalary());
                    stmt.setTimestamp(5, Timestamp.valueOf(staff.getHireDate()));
                    stmt.setString(6, staff.getRole());
                    stmt.setBoolean(7, staff.isActive());

                    return stmt.executeUpdate() > 0;
                }
            }
        };
    }

    @Override
    public Task<Boolean> update(Staff staff) {
        return new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                String query = """
                        UPDATE Staffs SET
                            full_name = ?, phone = ?, email = ?, salary = ?, hire_date = ?, role = ?, isActive = ?
                        WHERE staff_id = ?
                        """;
                try (Connection conn = DatabaseManager.connect();
                     PreparedStatement stmt = conn.prepareStatement(query)) {

                    stmt.setString(1, staff.getFullName());
                    stmt.setString(2, staff.getPhone());
                    stmt.setString(3, staff.getEmail());
                    stmt.setDouble(4, staff.getSalary());
                    stmt.setTimestamp(5, Timestamp.valueOf(staff.getHireDate()));
                    stmt.setString(6, staff.getRole());
                    stmt.setBoolean(7, staff.isActive());
                    stmt.setInt(8, staff.getStaffId());

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
                String query = "DELETE FROM Staffs WHERE staff_id = ?";
                try (Connection conn = DatabaseManager.connect();
                     PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setInt(1, id);
                    return stmt.executeUpdate() > 0;
                }
            }
        };
    }
}

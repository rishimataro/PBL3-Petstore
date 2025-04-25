package com.store.app.petstore.Repositories;

import com.store.app.petstore.Models.DatabaseManager;
import com.store.app.petstore.Models.Entities.Customer;
import com.store.app.petstore.Utils.Mappers.CustomerMapper;

import java.sql.*;

public class CustomerRepository {

    public boolean addCustomer(Customer customer) {
        String sql = "INSERT INTO Customers (full_name, phone) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            CustomerMapper.bindCustomerParams(stmt, customer);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateCustomer(Customer customer) {
        String sql = "UPDATE Customers SET full_name = ?, phone = ? WHERE customer_id = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            CustomerMapper.bindCustomerParams(stmt, customer);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Customer getCustomerById(int customerId) {
        String sql = "SELECT * FROM Customers WHERE customer_id = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("full_name"),
                        rs.getString("phone")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteCustomer(int customerId) {
        String sql = "DELETE FROM Customers WHERE customer_id = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

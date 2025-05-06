package com.store.app.petstore.DAO;

import com.store.app.petstore.Models.Entities.Customer;
import java.sql.*;
import java.util.ArrayList;

public class CustomerDAO implements BaseDAO<Customer, Integer> {
    public static CustomerDAO getInstance() { 
        return new CustomerDAO(); 
    }

    @Override
    public int insert(Customer entity) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "INSERT INTO Customers (full_name, phone) VALUES (?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, entity.getFullName());
            stmt.setString(2, entity.getPhone());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return 0;
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int customerId = generatedKeys.getInt(1);
                    entity.setCustomerId(customerId);
                    return customerId;
                }
            }
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
    }

    @Override
    public int update(Customer entity) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "UPDATE Customers SET full_name = ?, phone = ? WHERE customer_id = ?";
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, entity.getFullName());
            stmt.setString(2, entity.getPhone());
            stmt.setInt(3, entity.getCustomerId());
            
            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DatabaseUtil.closeResources(stmt, conn);
        }
    }

    @Override
    public int delete(Customer entity) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "DELETE FROM Customers WHERE customer_id = ?";
            stmt = conn.prepareStatement(sql);
            
            stmt.setInt(1, entity.getCustomerId());
            
            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DatabaseUtil.closeResources(stmt, conn);
        }
    }

    @Override
    public ArrayList<Customer> findAll() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<Customer> customerList = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM Customers";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Customer customer = new Customer(
                    rs.getInt("customer_id"),
                    rs.getString("full_name"),
                    rs.getString("phone")
                );
                customerList.add(customer);
            }
            return customerList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
    }

    @Override
    public Customer findById(Integer id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM Customers WHERE customer_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                Customer customer = new Customer();
                customer.setCustomerId(rs.getInt("customer_id"));
                customer.setFullName(rs.getString("full_name"));
                customer.setPhone(rs.getString("phone"));
                return customer;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
        return null;
    }

    @Override
    public ArrayList<Customer> findByCondition(String condition) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<Customer> customerList = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM Customers WHERE " + condition;
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Customer customer = new Customer(
                    rs.getInt("customer_id"),
                    rs.getString("full_name"),
                    rs.getString("phone")
                );
                customerList.add(customer);
            }
            return customerList.isEmpty() ? null : customerList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
    }

    public Customer findByPhone(String phone) {
        ArrayList<Customer> customers = findByCondition("phone = '" + phone + "'");
        return customers != null && !customers.isEmpty() ? customers.get(0) : null;
    }

    public ArrayList<Customer> findByName(String name) {
        return findByCondition("full_name LIKE '%" + name + "%'");
    }

    public int getCustomerCount() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT COUNT(*) as count FROM Customers";
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

    public Customer findLatest() {
        String sql = "SELECT * FROM CUSTOMER ORDER BY CUSTOMER_ID DESC FETCH FIRST 1 ROW ONLY";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                Customer customer = new Customer();
                customer.setCustomerId(rs.getInt("CUSTOMER_ID"));
                customer.setFullName(rs.getString("FULL_NAME"));
                customer.setPhone(rs.getString("PHONE"));
                return customer;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

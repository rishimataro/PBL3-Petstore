package com.store.app.petstore.Utils.Mappers;

import com.store.app.petstore.Models.Entities.Customer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerMapper  {

    public static Customer fromResultSet(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setCustomerId(rs.getInt("customer_id"));
        customer.setFullName(rs.getString("full_name"));
        customer.setPhone(rs.getString("phone"));
        return customer;
    }
    public static void bindCustomerParams(PreparedStatement stmt, Customer customer) throws SQLException {
        stmt.setString(1, customer.getFullName());
        stmt.setString(2, customer.getPhone());
        stmt.setInt(3, customer.getCustomerId());
    }
}

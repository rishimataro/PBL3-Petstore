package com.store.app.petstore.Utils.Mappers;

import com.store.app.petstore.Models.Entities.Customer;

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
}

package com.store.app.petstore.DAO;

import com.store.app.petstore.Models.Entities.Customer;
import java.util.List;

public interface CustomerDAO extends BaseDAO<Customer> {
    Customer getCustomerByEmail(String email);
    Customer getCustomerByPhone(String phone);
    List<Customer> searchCustomers(String keyword);
} 
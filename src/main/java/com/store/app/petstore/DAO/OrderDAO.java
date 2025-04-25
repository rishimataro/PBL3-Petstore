package com.store.app.petstore.DAO;

import com.store.app.petstore.Models.Entities.Order;
import java.util.List;
 
public interface OrderDAO extends BaseDAO<Order> {
    List<Order> getOrdersByUserId(int userId);
    List<Order> getOrdersByStatus(String status);
    List<Order> getOrdersByDateRange(String startDate, String endDate);
} 
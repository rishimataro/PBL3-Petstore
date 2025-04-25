package com.store.app.petstore.DAO;

import com.store.app.petstore.Models.Entities.OrderDetail;
import java.util.List;

public interface OrderDetailDAO extends BaseDAO<OrderDetail> {
    List<OrderDetail> getOrderDetailsByOrderId(int orderId);
    List<OrderDetail> getOrderDetailsByProductId(int productId);
} 
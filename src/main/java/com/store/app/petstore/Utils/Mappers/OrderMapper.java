package com.store.app.petstore.Utils.Mappers;

import com.store.app.petstore.Models.Entities.Order;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderMapper {

    public static Order fromResultSet(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setOrderId(rs.getInt("order_id"));
        order.setCustomerId(rs.getInt("customer_id"));
        order.setTotalPrice(rs.getDouble("total_price"));
        order.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());
        order.setStaffId(rs.getInt("staff_id"));
        order.setDiscountId(rs.getInt("discount_id"));
        return order;
    }
}

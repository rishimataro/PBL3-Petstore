package com.store.app.petstore.Utils.Mappers;

import com.store.app.petstore.Models.Entities.Order;

import java.sql.PreparedStatement;
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
    public static void bindOrderParams(PreparedStatement stmt, Order order) throws SQLException {
        stmt.setInt(1, order.getCustomerId());
        stmt.setDouble(2, order.getTotalPrice());
        stmt.setTimestamp(3, java.sql.Timestamp.valueOf(order.getOrderDate()));
        stmt.setInt(4, order.getStaffId());
        stmt.setInt(5, order.getDiscountId());
        stmt.setInt(6, order.getOrderId());
    }
}

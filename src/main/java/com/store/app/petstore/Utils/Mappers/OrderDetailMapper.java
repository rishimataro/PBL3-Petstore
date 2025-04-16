package com.store.app.petstore.Utils.Mappers;

import com.store.app.petstore.Models.Entities.OrderDetail;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderDetailMapper {

    public static OrderDetail fromResultSet(ResultSet rs) throws SQLException {
        OrderDetail detail = new OrderDetail();
        detail.setOrderDetailId(rs.getInt("order_detail_id"));
        detail.setOrderId(rs.getInt("order_id"));
        detail.setItemType(rs.getString("item_type"));
        detail.setItemId(rs.getInt("item_id"));
        detail.setQuantity(rs.getInt("quantity"));
        detail.setUnitPrice(rs.getDouble("unit_price"));
        return detail;
    }
    public static void bindOrderDetailParams(PreparedStatement stmt, OrderDetail orderDetail) throws SQLException {
        stmt.setInt(1, orderDetail.getOrderId());
        stmt.setString(2, orderDetail.getItemType());
        stmt.setInt(3, orderDetail.getItemId());
        stmt.setInt(4, orderDetail.getQuantity());
        stmt.setDouble(5, orderDetail.getUnitPrice());
        stmt.setInt(6, orderDetail.getOrderDetailId());
    }
}

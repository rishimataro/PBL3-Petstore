package com.store.app.petstore.Utils.Mappers;

import com.store.app.petstore.Models.Entities.OrderDetail;

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
}

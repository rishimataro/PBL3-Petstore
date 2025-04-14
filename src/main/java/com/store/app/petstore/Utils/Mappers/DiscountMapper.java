package com.store.app.petstore.Utils.Mappers;

import com.store.app.petstore.Models.Entities.Discount;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DiscountMapper {
    public static Discount fromResultSet(ResultSet rs) throws SQLException {
        Discount discount = new Discount();
        discount.setDiscountId(rs.getInt("discount_id"));
        discount.setCode(rs.getString("code"));
        discount.setDiscountType(rs.getString("discount_type"));
        discount.setValue(rs.getDouble("value"));
        discount.setStartDate(rs.getDate("start_date").toLocalDate());
        discount.setEndDate(rs.getDate("end_date").toLocalDate());
        discount.setMinOrderValue(rs.getDouble("min_order_value"));
        discount.setMaxDiscountValue(rs.getDouble("max_discount_value"));
        return discount;
    }
}

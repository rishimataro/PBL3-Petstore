package com.store.app.petstore.Utils;

import com.store.app.petstore.Models.Entities.*;
import com.store.app.petstore.Utils.Mappers.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ParserModel {
    public static User getUser(ResultSet rs) throws SQLException {
        return UserMapper.fromResultSet(rs);
    }

    public static Pet getPet(ResultSet rs) throws SQLException {
        return PetMapper.fromResutSet(rs);
    }

    public static Staff getStaff(ResultSet rs) throws SQLException {
        return StaffMapper.fromResutlSet(rs);
    }

    public static Product getProduct(ResultSet rs) throws SQLException {
        return ProductMapper.fromResutlSet(rs);
    }

    public static OrderDetail getOrderDetail(ResultSet rs) throws SQLException {
        return OrderDetailMapper.fromResultSet(rs);
    }

    public static Discount getDiscount(ResultSet rs) throws SQLException {
        return DiscountMapper.fromResultSet(rs);
    }

    public static Order getOrder(ResultSet rs) throws SQLException {
        return OrderMapper.fromResultSet(rs);
    }

    public static Customer getCustomer(ResultSet rs) throws SQLException {
        return CustomerMapper.fromResultSet(rs);
    }

    public static void setProduct(PreparedStatement stmt, Product product) throws SQLException {
        ProductMapper.bindProductParams(stmt, product);
    }
}

package com.store.app.petstore.Utils.Mappers;

import com.store.app.petstore.Models.Entities.Product;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductMapper {
    public static Product fromResutlSet(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductId(rs.getInt("product_id"));
        product.setName(rs.getString("name"));
        product.setCategory(rs.getString("category"));
        product.setStock(rs.getInt("stock"));
        product.setPrice(rs.getInt("price"));
        product.setDescription(rs.getString("description"));
        product.setImageUrl(rs.getString("image_url"));

        return product;
    }
    public static void bindProductParams(PreparedStatement stmt, Product product) throws SQLException {
        stmt.setString(1, product.getName());
        stmt.setString(2, product.getCategory());
        stmt.setInt(3, product.getStock());
        stmt.setInt(4, product.getPrice());
        stmt.setString(5, product.getDescription());
        stmt.setString(6, product.getImageUrl());
    }
}

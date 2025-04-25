package com.store.app.petstore.Repositories;

import com.store.app.petstore.Models.DatabaseManager;
import com.store.app.petstore.Models.Entities.Product;
import com.store.app.petstore.Utils.Mappers.ProductMapper;
import com.store.app.petstore.Utils.ParserModel;
import javafx.concurrent.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductRepository implements BaseRepository<Product> {

    @Override
    public Task<List<Product>> findAll() {
        return new Task<>() {
            @Override
            protected List<Product> call() throws Exception {
                List<Product> products = new ArrayList<>();
                String sql = "SELECT * FROM Products";
                try (Connection conn = DatabaseManager.connect();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        products.add(ParserModel.getProduct(rs));
                    }
                }
                return products;
            }
        };
    }

    @Override
    public Task<Optional<Product>> findById(int id) {
        return new Task<>() {
            @Override
            protected Optional<Product> call() throws Exception {
                String sql = "SELECT * FROM Products WHERE product_id = ?";
                try (Connection conn = DatabaseManager.connect();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, id);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        return Optional.of(ParserModel.getProduct(rs));
                    }
                    return Optional.empty();
                }
            }
        };
    }

    @Override
    public Task<Boolean> save(Product product) {
        return new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                String sql = "INSERT INTO Products (name, category, stock, price, description, image_url) VALUES (?, ?, ?, ?, ?, ?)";
                try (Connection conn = DatabaseManager.connect();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    ProductMapper.bindProductParams(stmt, product);
                    return stmt.executeUpdate() > 0;
                }
            }
        };
    }


    @Override
    public Task<Boolean> update(Product product) {
        return new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                String sql = "UPDATE Products SET name = ?, category = ?, stock = ?, price = ?, description = ?, image_url = ? WHERE product_id = ?";
                try (Connection conn = DatabaseManager.connect();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    ProductMapper.bindProductParams(stmt, product);
                    stmt.setInt(7, product.getProductId());
                    return stmt.executeUpdate() > 0;
                }
            }
        };
    }

    @Override
    public Task<Boolean> deleteById(int id) {
        return new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                String sql = "DELETE FROM Products WHERE product_id = ?";
                try (Connection conn = DatabaseManager.connect();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, id);
                    return stmt.executeUpdate() > 0;
                }
            }
        };
    }
}

package com.store.app.petstore.Repositories;

import com.store.app.petstore.Models.DatabaseManager;
import com.store.app.petstore.Models.Entities.Discount;
import com.store.app.petstore.Utils.Mappers.DiscountMapper;
import com.store.app.petstore.Utils.ParserModel;
import javafx.concurrent.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DiscountRepository implements BaseRepository<Discount> {

    @Override
    public Task<List<Discount>> findAll() {
        return new Task<>() {
            @Override
            protected List<Discount> call() throws Exception {
                List<Discount> discounts = new ArrayList<>();
                String sql = "SELECT * FROM Discounts";
                try (Connection conn = DatabaseManager.connect();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        discounts.add(ParserModel.getDiscount(rs));
                    }
                }
                return discounts;
            }
        };
    }

    @Override
    public Task<Optional<Discount>> findById(int id) {
        return new Task<>() {
            @Override
            protected Optional<Discount> call() throws Exception {
                String sql = "SELECT * FROM Discounts WHERE discount_id = ?";
                try (Connection conn = DatabaseManager.connect();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, id);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        return Optional.of(ParserModel.getDiscount(rs));
                    }
                    return Optional.empty();
                }
            }
        };
    }

    @Override
    public Task<Boolean> save(Discount discount) {
        return new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                String sql = "INSERT INTO Discounts (code, discount_type, value, start_date, end_date, min_order_value, max_discount_value) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (Connection conn = DatabaseManager.connect();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    DiscountMapper.bindDiscountParams(stmt, discount);
                    return stmt.executeUpdate() > 0;
                }
            }
        };
    }

    @Override
    public Task<Boolean> update(Discount discount) {
        return new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                String sql = "UPDATE Discounts SET code = ?, discount_type = ?, value = ?, start_date = ?, end_date = ?, " +
                        "min_order_value = ?, max_discount_value = ? WHERE discount_id = ?";
                try (Connection conn = DatabaseManager.connect();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    DiscountMapper.bindDiscountParams(stmt, discount);
                    stmt.setInt(8, discount.getDiscountId());
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
                String sql = "DELETE FROM Discounts WHERE discount_id = ?";
                try (Connection conn = DatabaseManager.connect();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, id);
                    return stmt.executeUpdate() > 0;
                }
            }
        };
    }
}

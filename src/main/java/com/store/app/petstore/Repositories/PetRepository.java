package com.store.app.petstore.Repositories;

import com.store.app.petstore.Models.DatabaseManager;
import com.store.app.petstore.Models.Entities.Pet;
import com.store.app.petstore.Utils.Mappers.PetMapper;
import com.store.app.petstore.Utils.ParserModel;
import javafx.concurrent.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PetRepository implements BaseRepository<Pet> {

    @Override
    public Task<List<Pet>> findAll() {
        return new Task<>() {
            @Override
            protected List<Pet> call() throws Exception {
                List<Pet> pets = new ArrayList<>();
                String sql = "SELECT * FROM Pets";
                try (Connection conn = DatabaseManager.connect();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        pets.add(ParserModel.getPet(rs));
                    }
                }
                return pets;
            }
        };
    }

    @Override
    public Task<Optional<Pet>> findById(int id) {
        return new Task<>() {
            @Override
            protected Optional<Pet> call() throws Exception {
                String sql = "SELECT * FROM Pets WHERE pet_id = ?";
                try (Connection conn = DatabaseManager.connect();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, id);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        return Optional.of(ParserModel.getPet(rs));
                    }
                    return Optional.empty();
                }
            }
        };
    }

    @Override
    public Task<Boolean> save(Pet pet) {
        return new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                String sql = "INSERT INTO Pets (name, type, breed, age, price, description, image_url, sex) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                try (Connection conn = DatabaseManager.connect();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    PetMapper.bindProductParams(stmt, pet);
                    return stmt.executeUpdate() > 0;
                }
            }
        };
    }

    @Override
    public Task<Boolean> update(Pet pet) {
        return new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                String sql = "UPDATE Pets SET name = ?, type = ?, breed = ?, age = ?, price = ?, description = ?, image_url = ?, sex = ? " +
                        "WHERE pet_id = ?";
                try (Connection conn = DatabaseManager.connect();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    PetMapper.bindProductParams(stmt, pet);
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
                String sql = "DELETE FROM Pets WHERE pet_id = ?";
                try (Connection conn = DatabaseManager.connect();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, id);
                    return stmt.executeUpdate() > 0;
                }
            }
        };
    }
}

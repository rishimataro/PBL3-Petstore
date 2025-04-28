package com.store.app.petstore.DAO.Impl;

import com.store.app.petstore.DAO.PetDAO;
import com.store.app.petstore.Models.Entities.Pet;
import com.store.app.petstore.Models.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PetDAOImpl implements PetDAO {
    private final Connection connection;

    public PetDAOImpl() {
        this.connection = DatabaseManager.connect();
    }

    @Override
    public List<Pet> getAll() {
        List<Pet> pets = new ArrayList<>();
        String query = "SELECT * FROM pets";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                pets.add(extractPetFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pets;
    }

    @Override
    public Pet getById(int id) {
        String query = "SELECT * FROM pets WHERE petId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractPetFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean create(Pet pet) {
        String query = "INSERT INTO pets (name, type, breed, age, gender, description, imageUrl, price, sex) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            setPetParameters(pstmt, pet);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        pet.setPetId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(Pet pet) {
        String query = "UPDATE pets SET name = ?, type = ?, breed = ?, age = ?, gender = ?, description = ?, imageUrl = ?, price = ?, sex = ? WHERE petId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            setPetParameters(pstmt, pet);
            pstmt.setInt(10, pet.getPetId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String query = "DELETE FROM pets WHERE petId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Pet> getPetsByType(String type) {
        List<Pet> pets = new ArrayList<>();
        String query = "SELECT * FROM pets WHERE type = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, type);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    pets.add(extractPetFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pets;
    }

    @Override
    public List<Pet> getPetsByStatus(String status) {
        List<Pet> pets = new ArrayList<>();
        String query = "SELECT * FROM pets WHERE status = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, status);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    pets.add(extractPetFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pets;
    }

    @Override
    public List<Pet> searchPets(String keyword) {
        List<Pet> pets = new ArrayList<>();
        String query = "SELECT * FROM pets WHERE name LIKE ? OR type LIKE ? OR breed LIKE ? OR description LIKE ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            String searchPattern = "%" + keyword + "%";
            for (int i = 1; i <= 4; i++) {
                pstmt.setString(i, searchPattern);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    pets.add(extractPetFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pets;
    }

    private Pet extractPetFromResultSet(ResultSet rs) throws SQLException {
        Pet pet = new Pet();
        pet.setPetId(rs.getInt("petId"));
        pet.setName(rs.getString("name"));
        pet.setType(rs.getString("type"));
        pet.setBreed(rs.getString("breed"));
        pet.setAge(rs.getInt("age"));
        pet.setSex(rs.getString("gender"));
        pet.setDescription(rs.getString("description"));
        pet.setImageUrl(rs.getString("imageUrl"));
        pet.setPrice(rs.getLong("price"));
        pet.setSex(rs.getString("sex"));
        return pet;
    }

    private void setPetParameters(PreparedStatement pstmt, Pet pet) throws SQLException {
        pstmt.setString(1, pet.getName());
        pstmt.setString(2, pet.getType());
        pstmt.setString(3, pet.getBreed());
        pstmt.setInt(4, pet.getAge());
        pstmt.setString(5, pet.getSex());
        pstmt.setString(6, pet.getDescription());
        pstmt.setString(7, pet.getImageUrl());
        pstmt.setLong(8, pet.getPrice());
        pstmt.setString(9, pet.getSex());
    }
} 
package com.store.app.petstore.DAO;

import com.store.app.petstore.Models.Entities.Pet;
import com.store.app.petstore.Utils.Mappers.PetMapper;

import java.sql.*;
import java.util.ArrayList;

public class PetDAO {
    public static PetDAO getInstance() {
        return new PetDAO();
    }

    public static int insert(Pet entity) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "INSERT INTO Pets (name, type, breed, age, description, image_url, sex, price, isSold) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, entity.getName());
            stmt.setString(2, entity.getType());
            stmt.setString(3, entity.getBreed());
            stmt.setInt(4, entity.getAge());
            stmt.setString(5, entity.getDescription());
            stmt.setString(6, entity.getImageUrl());
            stmt.setString(7, entity.getSex());
            stmt.setInt(8, entity.getPrice());
            stmt.setBoolean(9, entity.getIsSold());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return 0;
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int petId = generatedKeys.getInt(1);
                    entity.setPetId(petId);
                    return petId;
                }
            }
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
    }

    public static int update(Pet entity) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "UPDATE Pets SET name = ?, type = ?, breed = ?, age = ?, description = ?, image_url = ?, sex = ?, price = ?, isSold = ? WHERE pet_id = ?";
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, entity.getName());
            stmt.setString(2, entity.getType());
            stmt.setString(3, entity.getBreed());
            stmt.setInt(4, entity.getAge());
            stmt.setString(5, entity.getDescription());
            stmt.setString(6, entity.getImageUrl());
            stmt.setString(7, entity.getSex());
            stmt.setLong(8, entity.getPrice());
            stmt.setBoolean(9, entity.getIsSold());
            stmt.setInt(10, entity.getPetId());

            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DatabaseUtil.closeResources(stmt, conn);
        }
    }

    public static int delete(Pet entity) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "DELETE FROM Pets WHERE pet_id = ?";
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, entity.getPetId());

            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DatabaseUtil.closeResources(stmt, conn);
        }
    }

    public static ArrayList<Pet> findAll() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<Pet> petList = new ArrayList<>();

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM Pets";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Pet pet = new Pet(
                        rs.getInt("pet_id"),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getString("breed"),
                        rs.getString("sex"),
                        rs.getInt("age"),
                        rs.getInt("price"),
                        rs.getString("image_url"),
                        rs.getString("description"),
                        rs.getBoolean("isSold")
                );
                petList.add(pet);
            }
            return petList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
    }

    public static Pet findById(Integer id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM Pets WHERE pet_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return new Pet(
                        rs.getInt("pet_id"),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getString("breed"),
                        rs.getString("sex"),
                        rs.getInt("age"),
                        rs.getInt("price"),
                        rs.getString("image_url"),
                        rs.getString("description"),
                        rs.getBoolean("isSold")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
        return null;
    }

    public static ArrayList<Pet> findByCondition(String condition) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<Pet> petList = new ArrayList<>();

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM Pets WHERE " + condition;
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                petList.add(PetMapper.fromResutSet(rs));
            }
            return petList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
    }

    public static ArrayList<Pet> findByType(String type) {
        return findByCondition("type = '" + type + "'");
    }

    public static ArrayList<Pet> findByBreed(String breed) {
        return findByCondition("breed = '" + breed + "'");
    }

    public static ArrayList<Pet> findByPriceRange(long minPrice, long maxPrice) {
        return findByCondition("price BETWEEN " + minPrice + " AND " + maxPrice);
    }

    public static ArrayList<Pet> findBySex(String sex) {
        return findByCondition("sex = '" + sex + "'");
    }

    // tim kiem theo ten, loai, giong
    public static ArrayList<Pet> searchPets(String searchText, int limit) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<Pet> petList = new ArrayList<>();

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM Pets WHERE LOWER(name) LIKE ? LIMIT ?";

            stmt = conn.prepareStatement(sql);
            String searchPattern = "%" + searchText.toLowerCase() + "%";
            stmt.setString(1, searchPattern);
            stmt.setInt(2, limit);

            rs = stmt.executeQuery();

            while (rs.next()) {
                petList.add(PetMapper.fromResutSet(rs));
            }
            return petList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
    }

    // so luong thu cung
    public static int getPetCount() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT COUNT(*) as count FROM Pets";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
    }
}

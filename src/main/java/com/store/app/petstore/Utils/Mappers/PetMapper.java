package com.store.app.petstore.Utils.Mappers;

import com.store.app.petstore.Models.Entities.Pet;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PetMapper {

    public static Pet fromResutSet(ResultSet rs) throws SQLException {
        Pet pet = new Pet();
        pet.setPetId(rs.getInt("pet_id"));
        pet.setName(rs.getString("name"));
        pet.setType(rs.getString("type"));
        pet.setBreed(rs.getString("breed"));
        pet.setAge(rs.getInt("age"));
        pet.setPrice(rs.getInt("price"));
        pet.setDescription(rs.getString("description"));
        pet.setImageUrl(rs.getString("image_url"));
        pet.setSex(rs.getString("sex"));
        return pet;
    }
    public static void bindProductParams( PreparedStatement stmt, Pet pet) throws SQLException {
        stmt.setString(1, pet.getName());
        stmt.setString(2, pet.getType());
        stmt.setString(3, pet.getBreed());
        stmt.setInt(4, pet.getAge());
        stmt.setLong(9, pet.getPrice());
        stmt.setString(6, pet.getDescription());
        stmt.setString(7, pet.getImageUrl());
        stmt.setString(8, pet.getSex());
    }
}

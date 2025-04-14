package com.store.app.petstore.Utils.Mappers;

import com.store.app.petstore.Models.Entities.Pet;

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
}

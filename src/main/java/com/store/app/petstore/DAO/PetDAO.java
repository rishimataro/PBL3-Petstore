package com.store.app.petstore.DAO;

import com.store.app.petstore.Models.Entities.Pet;
import java.util.List;
 
public interface PetDAO extends BaseDAO<Pet> {
    List<Pet> getPetsByType(String type);
    List<Pet> getPetsByStatus(String status);
    List<Pet> searchPets(String keyword);
} 
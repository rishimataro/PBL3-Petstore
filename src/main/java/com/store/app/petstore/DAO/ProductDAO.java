package com.store.app.petstore.DAO;

import com.store.app.petstore.Models.Entities.Product;
import java.util.List;
 
public interface ProductDAO extends BaseDAO<Product> {
    List<Product> getProductsByCategory(String category);
    List<Product> getProductsByPriceRange(double minPrice, double maxPrice);
    List<Product> searchProducts(String keyword);
} 
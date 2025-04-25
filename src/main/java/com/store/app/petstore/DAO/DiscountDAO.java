package com.store.app.petstore.DAO;

import com.store.app.petstore.Models.Entities.Discount;
import java.util.List;

public interface DiscountDAO extends BaseDAO<Discount> {
    List<Discount> getActiveDiscounts();
    List<Discount> getDiscountsByType(String type);
    List<Discount> getDiscountsByDateRange(String startDate, String endDate);
} 
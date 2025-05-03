package com.store.app.petstore.Controller;

import com.store.app.petstore.DAO.DiscountDAO;
import com.store.app.petstore.Models.Entities.Discount;
import java.util.function.Consumer;

public class OrderController {
    private DiscountDAO discountDAO;
    
    public OrderController() {
        this.discountDAO = DiscountDAO.getInstance();
    }

    public void applyDiscount(String code, double currentTotal, Consumer<Double> updateTotalCallback) {
        if (code == null || code.trim().isEmpty()) {
            updateTotalCallback.accept(currentTotal);
            return;
        }

        Discount discount = discountDAO.findByCode(code.trim());
        String validationResult = discountDAO.validateDiscount(discount, currentTotal);
        
        if (validationResult != null) {
            throw new IllegalArgumentException(validationResult);
        }

        double discountAmount = 0;
        if (discount.getDiscountType().equals("percent")) {
            // Tính số tiền giảm giá dựa trên phần trăm
            discountAmount = currentTotal * (discount.getValue() / 100.0);
            // Kiểm tra và giới hạn số tiền giảm giá tối đa nếu có
            if (discount.getMaxDiscountValue() > 0) {
                discountAmount = Math.min(discountAmount, discount.getMaxDiscountValue());
            }
        } else if (discount.getDiscountType().equals("fixed")) {
            discountAmount = discount.getValue();
        }

        double finalTotal = Math.max(0, currentTotal - discountAmount);
        updateTotalCallback.accept(finalTotal);
    }
} 
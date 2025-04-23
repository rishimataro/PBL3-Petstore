package com.store.app.petstore.Models.Entities;

import com.store.app.petstore.Models.BaseModel;

import java.time.LocalDate;

public class Discount extends BaseModel {
    private int discountId;
    private String code;
    private String discountType;
    private double value;
    private LocalDate startDate;
    private LocalDate endDate;
    private double minOrderValue;
    private double maxDiscountValue;

    public Discount() {
    }

    public Discount(String code, String discountType, double value, LocalDate startDate, LocalDate endDate, double minOrderValue, double maxDiscountValue) {
        this.code = code;
        this.discountType = discountType;
        this.value = value;
        this.startDate = startDate;
        this.endDate = endDate;
        this.minOrderValue = minOrderValue;
        this.maxDiscountValue = maxDiscountValue;
    }

    public Discount(int discountId, String code, String discountType, double value, LocalDate startDate, LocalDate endDate, double minOrderValue, double maxDiscountValue) {
        this.discountId = discountId;
        this.code = code;
        setDiscountType(discountType);
        setValue(value);
        this.startDate = startDate;
        setEndDate(endDate);
        this.minOrderValue = Math.max(minOrderValue, 0);
        this.maxDiscountValue = maxDiscountValue;
    }

    // Getters
    public int getDiscountId() {
        return discountId;
    }

    public String getCode() {
        return code;
    }

    public String getDiscountType() {
        return discountType;
    }

    public double getValue() {
        return value;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public double getMinOrderValue() {
        return minOrderValue;
    }

    // Setters
    public void setDiscountId(int discountId) {
        this.discountId = discountId;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDiscountType(String discountType) {
        if (discountType.equals("percent") || discountType.equals("fixed")) {
            this.discountType = discountType;
        } else {
            throw new IllegalArgumentException("Discount type must be 'percent' or 'fixed'");
        }
    }

    public void setValue(double value) {
        if (value > 0) {
            this.value = value;
        } else {
            throw new IllegalArgumentException("Discount value must be greater than 0");
        }
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        if (endDate.isAfter(startDate) || endDate.isEqual(startDate)) {
            this.endDate = endDate;
        } else {
            throw new IllegalArgumentException("End date must be on or after start date");
        }
    }

    public void setMinOrderValue(double minOrderValue) {
        this.minOrderValue = Math.max(minOrderValue, 0); // Đảm bảo >= 0
    }

    public double getMaxDiscountValue() {
        return maxDiscountValue;
    }

    public void setMaxDiscountValue(double maxDiscountValue) {
        this.maxDiscountValue = maxDiscountValue;
    }
}

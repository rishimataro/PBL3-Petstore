package com.store.app.petstore.Models.Records;

import java.time.LocalDate;

public class ProductRevenue {
    private String name;
    private double revenue;
    private LocalDate saleDate; // Added for mock filtering

    public ProductRevenue(String name, double revenue, LocalDate saleDate) {
        this.name = name;
        this.revenue = revenue;
        this.saleDate = saleDate;
    }

    public String getName() {
        return name;
    }

    public double getRevenue() {
        return revenue;
    }

    public LocalDate getSaleDate() {
        return saleDate;
    }
}

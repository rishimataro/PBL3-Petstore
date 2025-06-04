package com.store.app.petstore.Models.Records;

import java.time.LocalDate;

public class BestSellingItem {
    private String name;
    private int quantitySold;
    private LocalDate saleDate;

    public BestSellingItem(String name, int quantitySold, LocalDate saleDate) {
        this.name = name;
        this.quantitySold = quantitySold;
        this.saleDate = saleDate;
    }

    public String getName() {
        return name;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public LocalDate getSaleDate() {
        return saleDate;
    }
}

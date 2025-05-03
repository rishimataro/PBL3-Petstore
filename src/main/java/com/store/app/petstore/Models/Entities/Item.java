package com.store.app.petstore.Models.Entities;

public interface Item {
    int getId();
    String getName();
    String getImageUrl();
    int getPrice();
    String getType();
    boolean getIsSold();
} 
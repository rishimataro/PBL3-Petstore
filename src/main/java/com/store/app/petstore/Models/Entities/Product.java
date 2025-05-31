package com.store.app.petstore.Models.Entities;

import com.store.app.petstore.Models.BaseModel;

public class Product extends BaseModel implements Item {
    private int productId;
    private String name;
    private String category;
    private int stock;
    private int price;
    private String description;
    private String imageUrl;
    private boolean isSold;

    public Product() {}

    public Product(String name, String category, int stock, int price, String description, String imageUrl, boolean isSold) {
        this.name = name;
        this.category = category;
        this.stock = stock;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.isSold = isSold;
    }

    public Product(int productId, String name, String category, int stock, int price, String description, String imageUrl, boolean isSold) {
        this.productId = productId;
        this.name = name;
        this.category = category;
        this.stock = stock;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.isSold = isSold;
    }

    @Override
    public int getId() {
        return productId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getType() {
        return category;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    @Override
    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public boolean getIsSold() {
        return isSold;
    }

    public void setIsSold(boolean isSold) {
        this.isSold = isSold;
    }
}

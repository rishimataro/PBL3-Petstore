package com.store.app.petstore.Models.Entities;

import com.store.app.petstore.Models.BaseModel;

public class Product extends BaseModel {
    private int productId;
    private String name;
    private String category;
    private int stock;
    private int price;
    private String description;
    private String imageUrl;

    public Product(int productId, String name, String category, int stock, int price, String description, String imageUrl) {
        this.productId = productId;
        this.name = name;
        this.category = category;
        this.stock = stock;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
    }
    public Product() {}

    public String getCategory() {
        return category;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

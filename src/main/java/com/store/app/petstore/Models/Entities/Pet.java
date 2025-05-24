package com.store.app.petstore.Models.Entities;

import com.store.app.petstore.Models.BaseModel;

public class Pet extends BaseModel implements Item {
    private int petId;
    private String name;
    private String type;
    private String breed;
    private String sex;
    private int age;
    private int price;
    private String imageUrl;
    private String description;
    private boolean isSold;

    public Pet() {}

    public Pet(String name, String type, String breed, String sex, int age, int price, String imageUrl, String description, boolean isSold) {
        this.name = name;
        this.type = type;
        this.breed = breed;
        this.sex = sex;
        this.age = age;
        this.price = price;
        this.imageUrl = imageUrl;
        this.description = description;
        this.isSold = isSold;
    }

    public Pet(int petId, String name, String type, String breed, String sex, int age, int price, String imageUrl, String description, boolean isSold) {
        this.petId = petId;
        this.name = name;
        this.type = type;
        this.breed = breed;
        this.sex = sex;
        this.age = age;
        this.price = price;
        this.imageUrl = imageUrl;
        this.description = description;
        this.isSold = isSold;
    }

    @Override
    public int getId() {
        return petId;
    }

    public int getPetId() {
        return petId;
    }

    public void setPetId(int petId) {
        this.petId = petId;
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
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return petId + name + type + breed + age;
    }

    @Override
    public boolean getIsSold() {
        return isSold;
    }

    public void setIsSold(boolean isSold) {
        this.isSold = isSold;
    }
}

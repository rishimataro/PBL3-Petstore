package com.store.app.petstore.Models.Entities;

import com.store.app.petstore.Models.BaseModel;

public class Pet extends BaseModel {
    private int petId;
    private String name;
    private String type;
    private String breed;
    private int age;
    private String gender;
    private String description;
    private String imageUrl;
    private String sex;
    private long price;

    public Pet() {
        this.petId = 0;
        this.name = "";
        this.type = "";
        this.breed = "";
        this.age = 0;
        this.gender = "";
        this.description = "";
        this.imageUrl = "";
        this.sex = "";
        this.price = 0;
    }

    public Pet(int petId, String name, String type, String breed, int age, String gender, String description, String imageUrl, long price, String sex) {
        this.petId = petId;
        this.name = name;
        this.type = type;
        this.breed = breed;
        setAge(age); // Đảm bảo age >= 0
        this.gender = gender;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
    }

    // Getters
    public int getPetId() {
        return petId;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getBreed() {
        return breed;
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

    public String getGender() {
        return gender;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public long getPrice() {
        return price;
    }

    // Setters
    public void setPetId(int petId) {
        this.petId = petId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public void setAge(int age) {
        if (age >= 0) {
            this.age = age;
        } else {
            throw new IllegalArgumentException("Age must be greater than or equal to 0");
        }
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setPrice(long price) {
        this.price = price;
    }
}

package com.store.app.petstore.Models.Entities;

public class Pet {
    private int petId;
    private String name;
    private String type;
    private String breed;
    private int age;
    private String description;
    private String imageUrl;

    public Pet(int petId, String name, String type, String breed, int age, String description, String imageUrl) {
        this.petId = petId;
        this.name = name;
        this.type = type;
        this.breed = breed;
        setAge(age); // Đảm bảo age >= 0
        this.description = description;
        this.imageUrl = imageUrl;
    }

    // Getters
    public int getPetId() { return petId; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getBreed() { return breed; }
    public int getAge() { return age; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }

    // Setters
    public void setPetId(int petId) { this.petId = petId; }
    public void setName(String name) { this.name = name; }
    public void setType(String type) { this.type = type; }
    public void setBreed(String breed) { this.breed = breed; }

    public void setAge(int age) {
        if (age >= 0) {
            this.age = age;
        } else {
            throw new IllegalArgumentException("Age must be greater than or equal to 0");
        }
    }

    public void setDescription(String description) { this.description = description; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}

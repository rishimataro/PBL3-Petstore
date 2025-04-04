package com.store.app.petstore.Models.Entities;

public class Service {
    private int serviceId;
    private String name;
    private int duration; // Thời gian thực hiện (phút)
    private String description;

    public Service(int serviceId, String name, int duration, String description) {
        this.serviceId = serviceId;
        this.name = name;
        this.duration = duration;
        this.description = description;
    }

    // Getters
    public int getServiceId() { return serviceId; }
    public String getName() { return name; }
    public int getDuration() { return duration; }
    public String getDescription() { return description; }

    // Setters
    public void setServiceId(int serviceId) { this.serviceId = serviceId; }
    public void setName(String name) { this.name = name; }
    public void setDuration(int duration) {
        if (duration > 0) {
            this.duration = duration;
        } else {
            throw new IllegalArgumentException("Duration must be greater than 0");
        }
    }
    public void setDescription(String description) { this.description = description; }

}

package com.store.app.petstore.entities;

import java.time.LocalDate;

public class Staff {
    private int staffId;
    private int userId;
    private String fullName;
    private String phone;
    private String email;
    private double salary;
    private LocalDate hireDate;
    private String role; // "sales", "warehouse", "care"
    private boolean isActive;

    public Staff(int staffId, int userId, String fullName, String phone, String email, double salary, LocalDate hireDate, String role, boolean isActive) {
        this.staffId = staffId;
        this.userId = userId;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.salary = salary;
        this.hireDate = hireDate;
        this.role = role;
        this.isActive = isActive;
    }

    // Getters và Setters
}

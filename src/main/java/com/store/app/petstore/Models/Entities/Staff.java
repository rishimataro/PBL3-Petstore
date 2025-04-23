package com.store.app.petstore.Models.Entities;

import com.store.app.petstore.Models.BaseModel;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Staff extends BaseModel {
    private int staffId;
    private int userId;
    private String fullName;
    private String phone;
    private String email;
    private double salary;
    private LocalDateTime hireDate;
    private String role;
    private boolean isActive;

    public Staff( int userId, String fullName, String phone, String email, double salary,
                 LocalDateTime hireDate, String role, boolean isActive) {
        this.userId = userId;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.salary = salary;
        this.hireDate = hireDate;
        this.role = role;
        this.isActive = isActive;
    }
    public Staff(int staffId, int userId, String fullName, String phone, String email, double salary,
                 LocalDateTime hireDate, String role, boolean isActive) {
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

    public Staff() {
    }

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public LocalDateTime getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDateTime hireDate) {
        this.hireDate = hireDate;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}

package com.store.app.petstore.Models.Entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Staff extends User {
    private int staffId;
    private String fullName;
    private String phone;
    private String email;
    private String address;
    private double salary;
    private LocalDateTime hireDate;
    private String role;
    private boolean isActive;

    public Staff(int userId, String username, String password, String userRole, boolean userIsActive,
                 String fullName, String phone, String email, String address, double salary,
                 LocalDateTime hireDate, String staffRole, boolean staffIsActive) {
        super(userId, username, password, userRole, LocalDateTime.now(), userIsActive);
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.salary = salary;
        this.hireDate = hireDate;
        this.role = staffRole;
        this.isActive = staffIsActive;
    }

    public Staff(int staffId, int userId, String username, String password, String userRole, boolean userIsActive,
                 String fullName, String phone, String email, String address, double salary,
                 LocalDateTime hireDate, String staffRole, boolean staffIsActive) {
        super(userId, username, password, userRole, LocalDateTime.now(), userIsActive);
        this.staffId = staffId;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.salary = salary;
        this.hireDate = hireDate;
        this.role = staffRole;
        this.isActive = staffIsActive;
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
        return super.getUserId();
    }

    public void setUserId(int userId) {
        super.setUserId(userId);
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

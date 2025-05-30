package com.store.app.petstore.Models.Entities;

import com.store.app.petstore.Models.BaseModel;

public class Customer extends BaseModel {
    private int customerId;
    private String fullName;
    private String phone;
    private double totalSpend;

    public Customer() {}

    public Customer(int customerId, String fullName, String phone) {
        this.customerId = customerId;
        this.fullName = fullName;
        this.phone = phone;
        this.totalSpend = 0.0;
    }

    public Customer(String fullName, String phone) {
        this.fullName = fullName;
        this.phone = phone;
        this.totalSpend = 0.0;
    }

    public Customer(int customerId, String fullName, String phone, double totalSpend) {
        this.customerId = customerId;
        this.fullName = fullName;
        this.phone = phone;
        this.totalSpend = totalSpend;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
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

    public double getTotalSpend() {
        return totalSpend;
    }

    public void setTotalSpend(double totalSpend) {
        this.totalSpend = totalSpend;
    }
}

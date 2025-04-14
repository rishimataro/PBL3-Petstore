package com.store.app.petstore.Models.Entities;

import com.store.app.petstore.Models.BaseModel;

public class Customer extends BaseModel {
    private int customerId;
    private String fullName;
    private String phone;
    public Customer() {}

    public Customer(int customerId, String fullName, String phone) {
        this.customerId = customerId;
        this.fullName = fullName;
        this.phone = phone;
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
}

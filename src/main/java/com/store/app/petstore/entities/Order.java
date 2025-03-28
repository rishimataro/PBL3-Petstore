package com.store.app.petstore.entities;

import java.time.LocalDateTime;

public class Order {
    private int orderId;
    private int customerId;
    private int staffId;
    private double totalPrice;
    private LocalDateTime orderDate;
    private String status; // "pending", "completed", "canceled"

    public Order(int orderId, int customerId, int staffId, double totalPrice, LocalDateTime orderDate, String status) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.staffId = staffId;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
        this.status = status;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

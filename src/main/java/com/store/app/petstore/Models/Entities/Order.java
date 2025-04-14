package com.store.app.petstore.Models.Entities;

import com.store.app.petstore.Models.BaseModel;

import java.time.LocalDateTime;

public class Order extends BaseModel {
    private int orderId;
    private int customerId;
    private double totalPrice;
    private LocalDateTime orderDate;
    private int staffId;
    private int discountId;

    public Order() {
    }

    public Order(int customerId, int staffId, double totalPrice, LocalDateTime orderDate, int discountId) {
        this.customerId = customerId;
        this.staffId = staffId;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
        this.discountId = discountId;
    }

    public Order(int orderId, int customerId, int staffId, double totalPrice, LocalDateTime orderDate, int discountId) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.staffId = staffId;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
        this.discountId = discountId;
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

    public int getDiscountId() {
        return discountId;
    }

    public void setDiscountId(int discountId) {
        this.discountId = discountId;
    }
}

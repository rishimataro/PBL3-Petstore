package com.store.app.petstore.Models.Entities;

import com.store.app.petstore.Models.BaseModel;

public class OrderDetail extends BaseModel {
    private int orderDetailId;
    private int orderId;
    private String itemType;
    private int itemId;
    private int quantity;
    private double unitPrice;

    public OrderDetail( int orderId, String itemType, int itemId, int quantity, double unitPrice) {
        this.orderId = orderId;
        this.itemId = itemId;
        this.itemType = itemType;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public OrderDetail(int orderDetailId, int orderId, String itemType, int itemId, int quantity, double unitPrice) {
        this.orderDetailId = orderDetailId;
        this.orderId = orderId;
        this.itemId = itemId;
        this.itemType = itemType;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
    public OrderDetail() {}

    public int getOrderDetailId() {
        return orderDetailId;
    }

    public void setOrderDetailId(int orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }
}

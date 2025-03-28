package com.store.app.petstore.entities;

public class OrderItem {
    private int itemId;
    private Integer petId;
    private Integer productId;
    private Integer serviceId;
    private double unitPrice;

    public OrderItem(int itemId, Integer petId, Integer productId, Integer serviceId, double unitPrice) {
        this.itemId = itemId;
        this.petId = petId;
        this.productId = productId;
        this.serviceId = serviceId;
        setUnitPrice(unitPrice);
    }


    public int getItemId() { return itemId; }
    public Integer getPetId() { return petId; }
    public Integer getProductId() { return productId; }
    public Integer getServiceId() { return serviceId; }
    public double getUnitPrice() { return unitPrice; }


    public void setItemId(int itemId) { this.itemId = itemId; }
    public void setPetId(Integer petId) { this.petId = petId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    public void setServiceId(Integer serviceId) { this.serviceId = serviceId; }

    public void setUnitPrice(double unitPrice) {
        if (unitPrice >= 0) {
            this.unitPrice = unitPrice;
        } else {
            throw new IllegalArgumentException("Unit price must be >= 0");
        }
    }
}

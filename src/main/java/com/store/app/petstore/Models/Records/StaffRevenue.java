package com.store.app.petstore.Models.Records;

public class StaffRevenue {
    private final String orderId;
    private final String customer;
    private final String total;

    public StaffRevenue(String orderId, String customer, String total) {
        this.orderId = orderId;
        this.customer = customer;
        this.total = total;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustomer() {
        return customer;
    }

    public String getTotal() {
        return total;
    }
}

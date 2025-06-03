package com.store.app.petstore.Models.Records;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class StaffRevenueStats {
    private final SimpleStringProperty staffName;
    private final SimpleIntegerProperty totalOrders;
    private final SimpleDoubleProperty totalRevenue;

    // Constructor with orders count
    public StaffRevenueStats(String staffName, int totalOrders, double totalRevenue) {
        this.staffName = new SimpleStringProperty(staffName);
        this.totalOrders = new SimpleIntegerProperty(totalOrders);
        this.totalRevenue = new SimpleDoubleProperty(totalRevenue);
    }

    // Constructor without orders count (for backward compatibility)
    public StaffRevenueStats(String staffName, double totalRevenue) {
        this.staffName = new SimpleStringProperty(staffName);
        this.totalOrders = new SimpleIntegerProperty(0);
        this.totalRevenue = new SimpleDoubleProperty(totalRevenue);
    }

    public String getStaffName() {
        return staffName.get();
    }

    public int getTotalOrders() {
        return totalOrders.get();
    }

    public double getTotalRevenue() {
        return totalRevenue.get();
    }

    // Property methods for binding
    public SimpleStringProperty staffNameProperty() {
        return staffName;
    }

    public SimpleIntegerProperty totalOrdersProperty() {
        return totalOrders;
    }

    public SimpleDoubleProperty totalRevenueProperty() {
        return totalRevenue;
    }

    @Override
    public String toString() {
        return String.format("StaffRevenueStats{staffName='%s', totalOrders=%d, totalRevenue=%.2f}",
                           getStaffName(), getTotalOrders(), getTotalRevenue());
    }
}

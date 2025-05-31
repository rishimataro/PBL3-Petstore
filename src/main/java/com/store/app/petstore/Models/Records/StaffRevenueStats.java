package com.store.app.petstore.Models.Records;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class StaffRevenueStats {
    private final SimpleStringProperty staffName;
    private final SimpleDoubleProperty totalRevenue;

    public StaffRevenueStats(String staffName, double totalRevenue) {
        this.staffName = new SimpleStringProperty(staffName);
        this.totalRevenue = new SimpleDoubleProperty(totalRevenue);
    }

    public String getStaffName() {
        return staffName.get();
    }

    public double getTotalRevenue() {
        return totalRevenue.get();
    }

    // Nếu bạn cần cho binding
    public SimpleStringProperty staffNameProperty() {
        return staffName;
    }

    public SimpleDoubleProperty totalRevenueProperty() {
        return totalRevenue;
    }
}

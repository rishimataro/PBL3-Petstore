package com.store.app.petstore.Models.Records;

import java.time.LocalDateTime;
import java.util.Objects;

public record OrderSummaryRecord(
        int orderId,
        String customerName,
        String staffName,
        LocalDateTime orderDate,
        long totalPrice,
        double discountValue,
        String discountType
) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderSummaryRecord that = (OrderSummaryRecord) o;
        return orderId == that.orderId && totalPrice == that.totalPrice && Double.compare(discountValue, that.discountValue) == 0 && Objects.equals(staffName, that.staffName) && Objects.equals(customerName, that.customerName) && Objects.equals(orderDate, that.orderDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, customerName, staffName, orderDate, totalPrice, discountValue);
    }
}

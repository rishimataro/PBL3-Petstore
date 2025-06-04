package com.store.app.petstore.Models.Records;

public record OrderDetailRecord(
    int orderId,
    String itemType,
    int itemId,
    int quantity,
    double price
) {}

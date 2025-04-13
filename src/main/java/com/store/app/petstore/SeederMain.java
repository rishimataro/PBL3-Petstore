package com.store.app.petstore;

import com.store.app.petstore.Models.*;

public class SeederMain {
    public static void main(String[] args) {
        System.out.println("Bắt đầu seed dữ liệu...");

        PetSeeder.seedPets(50);                // Seed 50 pets
        DiscountSeeder.seedDiscounts(20);      // Seed 20 discounts
        OrderSeeder.seedOrders(50);            // Seed 50 orders
        OrderDetailSeeder.seedOrderDetails(50); // Seed 50 order details

        System.out.println("Hoàn tất seed dữ liệu!");
    }
}

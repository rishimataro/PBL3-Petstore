package com.store.app.petstore.Models;

import com.store.app.petstore.Models.Seeder.*;
import eu.hansolo.tilesfx.Command;

import javax.swing.*;

public class DatabaseSeeder {
    public DatabaseSeeder() {
//        new CustomerTableSeeder();
//        new OrderTableSeeder();
//        new PetTableSeeder();
//        new DiscountTableSeeder();
//        new OrderDetailTableSeeder();
        new ProductTableSeeder();
//        new UserTableSeeder();
//        new StaffTableSeeder();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DatabaseSeeder::new);
    }
}

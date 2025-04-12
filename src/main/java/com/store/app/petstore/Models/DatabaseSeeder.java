package com.store.app.petstore.Models;

import com.store.app.petstore.Models.Seeder.CustomerTableSeeder;
import com.store.app.petstore.Models.Seeder.ProductTableSeeder;
import com.store.app.petstore.Models.Seeder.StaffTableSeeder;
import com.store.app.petstore.Models.Seeder.UserTableSeeder;

import javax.swing.*;

public class DatabaseSeeder {
    public DatabaseSeeder() {
//        SwingUtilities.invokeLater(() -> {new ProductTableSeeder();});
//        SwingUtilities.invokeLater(() -> {new UserTableSeeder();});
        SwingUtilities.invokeLater(() -> new StaffTableSeeder());
//        SwingUtilities.invokeLater(() -> new CustomerTableSeeder());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {new DatabaseSeeder();});
    }
}

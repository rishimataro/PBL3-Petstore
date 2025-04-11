package com.store.app.petstore.Models;

import com.store.app.petstore.Models.Seeder.ProductTableSeeder;
import com.store.app.petstore.Models.Seeder.UserTableSeeder;

import javax.swing.*;

public class DatabaseSeeder {
    public DatabaseSeeder() {
        new ProductTableSeeder();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {new DatabaseSeeder();});
//        SwingUtilities.invokeLater(() -> {new UserTableSeeder();});
    }
}

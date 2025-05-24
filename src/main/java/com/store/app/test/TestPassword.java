package com.store.app.test;

import org.mindrot.jbcrypt.BCrypt;

public class TestPassword {
    public static void main(String[] args) {

        String hashedpassword = "$2a$10$14OGGqVfKNcNJYSfvzcknu46IYiKglqR2XSW5iiex.vjidLVq6Mxy";
        String password = "o3Trjrndn8Fqb";
        System.out.println("Password: " + BCrypt.checkpw(password, hashedpassword));
    }
}

package com.store.app.test;


import com.store.app.petstore.Services.AuthService;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;


public class AuthServiceTest {

    @Test
    public void testLoginSuccess() {
        AuthService auth = new AuthService();
        assertTrue("Login should fail with incorrect password", auth.login("admin", "o3Trjrndn8Fqb"));
    }

    @Test
    public void testLoginWrongUsername() {
        AuthService auth = new AuthService();
        assertFalse("Login should fail with incorrect username", auth.login("user", "123456"));
    }

    @Test
    public void testLoginEmptyFields() {
        AuthService auth = new AuthService();
        assertFalse("Login should fail with empty credentials", auth.login("", ""));
    }
}

package com.store.app.petstore.Controllers;

import com.store.app.petstore.Services.AuthService;

public class AuthController {
    private final AuthService authService;

    public AuthController() {
        authService = new AuthService();
    }

    public boolean login(String username, String password) {
        boolean isLoginSuccessful = authService.login(username, password);

        if (isLoginSuccessful) {
            System.out.println("Đăng nhập thành công!");
        } else {
            System.out.println("Tên đăng nhập hoặc mật khẩu không đúng.");
        }
        return isLoginSuccessful;
    }

    public void logout() {
        authService.logout();
        System.out.println("Đăng xuất thành công.");
    }
}

package com.store.app.petstore.Sessions;


import com.store.app.petstore.Models.Entities.Staff;
import com.store.app.petstore.Models.Entities.User;
import com.store.app.petstore.Utils.ParserModel;

public class SessionManager {
    private static User currentUser;
    private static Staff currentStaff;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }
    public static void setCurrentStaff(Staff staff) {
        currentStaff = staff;
    }

    public static User getCurrentUser() {
        return currentUser;
    }
    public static Staff getCurrentStaff() {
        return currentStaff;
    }

    public static void clear() {
        currentUser = null;
        currentStaff = null;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static boolean isAdmin() {
        return currentUser != null && currentUser.getRole().equals("admin");
    }
}

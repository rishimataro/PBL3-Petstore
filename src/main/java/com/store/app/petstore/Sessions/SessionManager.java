package com.store.app.petstore.Sessions;

import com.store.app.petstore.Models.Entities.*;
import javafx.scene.control.Tab;
import java.util.ArrayList;
import java.util.Map;

public class SessionManager {
    private static User currentUser;
    private static Staff currentStaff;
    private static Order currentOrder;
    private static ArrayList<OrderDetail> currentOrderDetails;
    private static Map<Integer, Product> currentOrderProducts;
    private static Map<Integer, Pet> currentOrderPets;
    private static Discount currentDiscount;
    private static Tab currentTab;
    private static Tab tabToRemove;

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

    public static void setCurrentOrder(Order order) {
        currentOrder = order;
    }
    public static Order getCurrentOrder() {
        return currentOrder;
    }
    public static void setCurrentOrderDetails(ArrayList<OrderDetail> details) {
        currentOrderDetails = details;
    }
    public static ArrayList<OrderDetail> getCurrentOrderDetails() {
        return currentOrderDetails;
    }
    public static void setCurrentOrderProducts(Map<Integer, Product> products) {
        currentOrderProducts = products;
    }
    public static Map<Integer, Product> getCurrentOrderProducts() {
        return currentOrderProducts;
    }
    public static void setCurrentOrderPets(Map<Integer, Pet> pets) {
        currentOrderPets = pets;
    }
    public static Map<Integer, Pet> getCurrentOrderPets() {
        return currentOrderPets;
    }
    public static void setCurrentDiscount(Discount discount) {
        currentDiscount = discount;
    }
    public static Discount getCurrentDiscount() {
        return currentDiscount;
    }

    public static void setTabToRemove(Tab tab) {
        tabToRemove = tab;
    }

    public static Tab getTabToRemove() {
        return tabToRemove;
    }

    public static void setCurrentTab(Tab tab) {
        currentTab = tab;
    }

    public static Tab getCurrentTab() {
        return currentTab;
    }

    public static void clearCurrentOrder() {
        currentOrder = null;
        currentOrderDetails = null;
        currentOrderProducts = null;
        currentOrderPets = null;
        currentDiscount = null;
        currentTab = null;
        tabToRemove = null;
    }
}

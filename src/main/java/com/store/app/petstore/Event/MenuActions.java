package com.store.app.petstore.Event;

import javafx.event.ActionEvent;

public interface MenuActions {
    void handleOrder(ActionEvent event);
    void handleBillHistory(ActionEvent event);
    void handleAccountInfo(ActionEvent event);
    void handleLogout(ActionEvent event);
} 
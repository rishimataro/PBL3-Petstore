package com.store.app.petstore.Controller;

import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

public interface MenuUI {
    void initializeMenu();
    void setUserImage(Image image);
    void showContextMenu(MouseEvent event);
} 
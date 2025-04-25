package com.store.app.petstore.Event;

import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseEvent;

public interface PopupMenuActions {
    void showPopupMenu(ContextMenu menu, MouseEvent event);
    void hidePopupMenu(ContextMenu menu);
} 
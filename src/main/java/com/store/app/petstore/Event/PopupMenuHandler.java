package com.store.app.petstore.Event;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseEvent;

public class PopupMenuHandler implements PopupMenuActions {
    @Override
    public void showPopupMenu(ContextMenu menu, MouseEvent event) {
        if (menu != null && event.getSource() instanceof Node) {
            menu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY());
        }
    }

    @Override
    public void hidePopupMenu(ContextMenu menu) {
        if (menu != null) {
            menu.hide();
        }
    }
} 
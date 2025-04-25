package com.store.app.petstore.Controller;

import com.store.app.petstore.Event.MenuEventHandlers;
import com.store.app.petstore.Event.PopupMenuHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class StaffMenuController implements MenuUI {
    @FXML
    private ImageView userImage;
    @FXML
    private ImageView menuIcon;
    @FXML
    private AnchorPane rootPane;
    
    private ContextMenu contextMenu;
    private final MenuEventHandlers menuHandlers;
    private final PopupMenuHandler popupHandler;

    public StaffMenuController() {
        this.menuHandlers = new MenuEventHandlers();
        this.popupHandler = new PopupMenuHandler();
    }

    @FXML
    @Override
    public void initializeMenu() {
        // Initialize context menu
        contextMenu = new ContextMenu();
        
        // Create menu items
        MenuItem orderItem = new MenuItem("Đặt hàng");
        MenuItem billHistoryItem = new MenuItem("Lịch sử hóa đơn");
        MenuItem accountInfoItem = new MenuItem("Thông tin tài khoản");
        MenuItem logoutItem = new MenuItem("Đăng xuất");

        // Set event handlers for menu items
        orderItem.setOnAction(menuHandlers::handleOrder);
        billHistoryItem.setOnAction(menuHandlers::handleBillHistory);
        accountInfoItem.setOnAction(menuHandlers::handleAccountInfo);
        logoutItem.setOnAction(menuHandlers::handleLogout);

        // Add items to context menu
        contextMenu.getItems().addAll(orderItem, billHistoryItem, accountInfoItem, logoutItem);
        
        // Set up event handlers for showing/hiding menu
        menuIcon.setOnMouseClicked(this::showContextMenu);
        rootPane.setOnMouseClicked(event -> popupHandler.hidePopupMenu(contextMenu));
    }

    @Override
    public void setUserImage(Image image) {
        userImage.setImage(image);
    }

    @FXML
    @Override
    public void showContextMenu(MouseEvent event) {
        popupHandler.showPopupMenu(contextMenu, event);
    }
} 
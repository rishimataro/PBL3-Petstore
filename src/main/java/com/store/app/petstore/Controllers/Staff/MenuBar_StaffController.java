package com.store.app.petstore.Controllers.Staff;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MenuBar_StaffController implements Initializable {

    @FXML
    private FontAwesomeIconView menuIcon;

    @FXML
    private AnchorPane root;

    @FXML
    private Circle userImage;

    private ContextMenu contextMenu;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUserImage("src/main/resources/Images/dog.png");
        setMenu();
    }

    public void setUserImage(String imagePath) {
        try {
            Image image;
            if (imagePath != null && !imagePath.isEmpty()) {
                if (imagePath.startsWith("http") || imagePath.startsWith("file:")) {
                    image = new Image(imagePath);
                } else {
                    image = new Image("file:" + imagePath);
                }
            } else {
                image = new Image(Objects.requireNonNull(getClass().getResource("/Images/user.jpg")).toExternalForm());
            }
            userImage.setFill(new ImagePattern(image));
        } catch (Exception e) {
            System.out.println("Không thể tải ảnh. Đang dùng ảnh mặc định.");
            try {
                Image fallback = new Image(Objects.requireNonNull(getClass().getResource("/Images/user.jpg")).toExternalForm());
                userImage.setFill(new ImagePattern(fallback));
            } catch (Exception ex) {
                System.out.println("Không tìm thấy ảnh mặc định.");
                ex.printStackTrace();
            }
        }
    }

    private ContextMenu contextMenuItem() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem orderItem = new MenuItem("Đặt hàng");
        MenuItem billItem = new MenuItem("Lịch sử hóa đơn");
        MenuItem infoItem = new MenuItem("Thông tin tài khoản");
        MenuItem logoutItem = new MenuItem("Đăng xuất");

        contextMenu.setStyle(
                "-fx-font-family: 'Arial';" +
                "-fx-font-size: 14px;" +
                "-fx-background-color: white;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 6;"
        );

        orderItem.setOnAction(event -> System.out.println("Đặt hàng"));
        billItem.setOnAction(event -> System.out.println("Lịch sử hóa đơn"));
        infoItem.setOnAction(e -> System.out.println("Xem thông tin tài khoản"));
        logoutItem.setOnAction(e -> System.out.println("Đăng xuất"));

        contextMenu.getItems().addAll(orderItem, billItem, infoItem, logoutItem);
        contextMenu.setAutoHide(true);
        return contextMenu;
    }

    private void setMenu() {
        contextMenu = contextMenuItem();
        menuIcon.setOnMouseClicked(this::showContextMenu);

        menuIcon.setOnMouseEntered(event -> {
            if (!contextMenu.isShowing()) {
                contextMenu.show(menuIcon, event.getScreenX(), event.getScreenY());
            }
        });

        menuIcon.setOnMouseExited(event -> {
            PauseTransition delay = new PauseTransition(Duration.millis(150));
            delay.setOnFinished(e -> {
                if (contextMenu.isShowing()) {
                    Node popupContent = contextMenu.getSkin().getNode();
                    if (!popupContent.isHover() && !menuIcon.isHover()) {
                        contextMenu.hide();
                    }
                }
            });
            delay.play();
        });
    }

    private void showContextMenu(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            contextMenu.show(menuIcon, event.getScreenX(), event.getScreenY());
        }
    }
}

package com.store.app.petstore.Controllers.Staff;

import com.store.app.petstore.Models.Entities.User;
import com.store.app.petstore.Models.Entities.Staff;
import com.store.app.petstore.Sessions.SessionManager;
import com.store.app.petstore.Views.ViewFactory;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.image.ImageView;
import com.store.app.petstore.Controllers.ControllerUtils;
import javafx.event.ActionEvent;

import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class StaffMenuController implements Initializable {

    @FXML
    private FontAwesomeIconView menuIcon;

    @FXML
    private AnchorPane root;

    @FXML
    private Circle userImage;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label nameLabel;

    private ContextMenu contextMenu;
    private SessionManager sessionManager;
    private User currentUser;
    private Staff currentStaff;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sessionManager = new SessionManager();
        currentUser = sessionManager.getCurrentUser();
        currentStaff = sessionManager.getCurrentStaff();
        setupUserImage();
        setMenu();
        setupUserName();
    }

    private void setupUserName() {
        if (currentUser != null) {
            usernameLabel.setText(currentUser.getUsername());
            if (currentStaff != null) {
                nameLabel.setText(currentStaff.getFullName());
            } else {
                nameLabel.setText("Chưa cập nhật thông tin");
            }
        }
    }

    private void setupUserImage() {
        try {
            String userImagePath = currentUser.getImageUrl();

            String imageUrl = null;
            if (userImagePath != null && !userImagePath.isEmpty()) {
                imageUrl = Objects.requireNonNull(getClass().getResource(userImagePath)).toExternalForm();
            } else {
                imageUrl = Objects.requireNonNull(getClass().getResource("/Images/User/default.jpg")).toExternalForm();
            }

            Image image = new Image(imageUrl, false);
            double size = userImage.getRadius() * 2;

            double imgWidth = image.getWidth();
            double imgHeight = image.getHeight();
            double side = Math.min(imgWidth, imgHeight);
            double x = (imgWidth - side) / 2;
            double y = (imgHeight - side) / 2;

            ImageView imageView = new ImageView(image);
            imageView.setViewport(new javafx.geometry.Rectangle2D(x, y, side, side));
            imageView.setFitWidth(size);
            imageView.setFitHeight(size);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);

            Circle clip = new Circle(size / 2, size / 2, size / 2);
            imageView.setClip(clip);

            WritableImage clippedImage = imageView.snapshot(null, null);

            userImage.setFill(new ImagePattern(clippedImage));
        } catch (Exception ex) {
            System.err.println("Error loading user image: " + ex.getMessage());
            userImage.setFill(javafx.scene.paint.Color.GRAY);
        }
    }


    private ContextMenu contextMenuItem() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem dashboardItem = new MenuItem("Trang chủ");
        MenuItem orderItem = new MenuItem("Đặt hàng");
        MenuItem billItem = new MenuItem("Lịch sử hóa đơn");
        MenuItem infoItem = new MenuItem("Thông tin tài khoản");
        MenuItem logoutItem = new MenuItem("Đăng xuất");

        contextMenu.setStyle(
                "-fx-font-family: 'Arial';" +
                "-fx-font-size: 14px;" +
                "-fx-background-color: white;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 6;" +
                        "-fx-cursor: hand;"
        );

        dashboardItem.setOnAction(event -> {
            Stage currentStage = (Stage) root.getScene().getWindow();
            ViewFactory.getInstance().switchContent("dashboard", currentStage);
        });

        orderItem.setOnAction(event -> {
            Stage currentStage = (Stage) root.getScene().getWindow();
            ViewFactory.getInstance().switchContent("order", currentStage);
        });

        billItem.setOnAction(event -> {
            Stage currentStage = (Stage) root.getScene().getWindow();
            ViewFactory.getInstance().switchContent("billhistory", currentStage);
        });

        infoItem.setOnAction(event -> {
            Stage currentStage = (Stage) root.getScene().getWindow();
            ViewFactory.getInstance().switchContent("profile", currentStage);
        });

        logoutItem.setOnAction(event -> {
            handleLogout(null);
        });

        contextMenu.getItems().addAll(dashboardItem, orderItem, billItem, infoItem, logoutItem);
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

    @FXML
    void handleLogout(ActionEvent event) {
        Stage currentStage = (Stage) root.getScene().getWindow();
        if (ControllerUtils.showConfirmationAndWait("Đăng xuất", "Bạn có chắc chắn muốn đăng xuất không?\nNhấn OK để xác nhận.")) {
            sessionManager.clear();
            ViewFactory.getInstance().switchContent("login", currentStage);
        } else {
            ControllerUtils.showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Đăng xuất không thành công");
        }
    }

}




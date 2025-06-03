package com.store.app.petstore.Controllers.Staff;

import com.store.app.petstore.Models.Entities.User;
import com.store.app.petstore.Models.Entities.Staff;
import com.store.app.petstore.Sessions.SessionManager;
import com.store.app.petstore.Views.StaffFactory;
import com.store.app.petstore.Views.UtilsFactory;
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
import java.util.ResourceBundle;
import java.io.File;

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
    private User currentUser;
    private Staff currentStaff;
    private static StaffMenuController instance;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;
        currentUser = SessionManager.getCurrentUser();
        currentStaff = SessionManager.getCurrentStaff();
        setupUserImage();
        setMenu();
        setupUserName();
    }

    public static StaffMenuController getInstance() {
        return instance;
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
            // Refresh user from session
            currentUser = SessionManager.getCurrentUser();
            currentStaff = SessionManager.getCurrentStaff();
            String userImagePath = currentUser != null ? currentUser.getImageUrl() : null;

            Image image = null;
            if (userImagePath != null && !userImagePath.isEmpty()) {
                if (userImagePath.startsWith("/")) {
                    // Try resource path
                    URL resource = getClass().getResource(userImagePath);
                    if (resource != null) {
                        image = new Image(resource.toExternalForm());
                    } else {
                        // Try file system (src/main/resources + userImagePath)
                        File imageFile = new File("src/main/resources" + userImagePath);
                        if (imageFile.exists()) {
                            image = new Image(imageFile.toURI().toString());
                        }
                    }
                } else {
                    // Absolute or relative file path
                    File imageFile = new File(userImagePath);
                    if (imageFile.exists()) {
                        image = new Image(imageFile.toURI().toString());
                    }
                }
            }
            if (image == null || image.isError()) {
                // Fallback to default
                URL defaultResource = getClass().getResource("/Images/User/default.jpg");
                if (defaultResource != null) {
                    image = new Image(defaultResource.toExternalForm());
                }
            }

            if (image != null && !image.isError()) {
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
            } else {
                userImage.setFill(javafx.scene.paint.Color.GRAY);
            }
        } catch (Exception ex) {
            System.err.println("Error loading user image: " + ex.getMessage());
            userImage.setFill(javafx.scene.paint.Color.GRAY);
        }
    }

    /**
     * Convert absolute path to resource path
     */
    private String convertToResourcePath(String absolutePath) {
        if (absolutePath.contains("Images/User/")) {
            int index = absolutePath.indexOf("Images/User/");
            return "/" + absolutePath.substring(index);
        }
        return "/Images/User/default.jpg";
    }

    /**
     * Refresh avatar image
     */
    public void refreshAvatar() {
        setupUserImage();
        setupUserName(); // Also refresh name in case it changed
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
                        "-fx-background-radius: 6;" +
                        "-fx-cursor: hand;");

        orderItem.setOnAction(event -> {
            Stage currentStage = (Stage) root.getScene().getWindow();
            StaffFactory.getInstance().switchContent("order", currentStage);
        });

        billItem.setOnAction(event -> {
            Stage currentStage = (Stage) root.getScene().getWindow();
            StaffFactory.getInstance().switchContent("billhistory", currentStage);
        });

        infoItem.setOnAction(event -> {
            Stage currentStage = (Stage) root.getScene().getWindow();
            StaffFactory.getInstance().switchContent("profile", currentStage);
        });

        logoutItem.setOnAction(event -> {
            handleLogout(null);
        });

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

    @FXML
    void handleLogout(ActionEvent event) {
        Stage currentStage = (Stage) root.getScene().getWindow();
        if (ControllerUtils.showConfirmationAndWait("Đăng xuất",
                "Bạn có chắc chắn muốn đăng xuất không?\nNhấn OK để xác nhận.")) {
            SessionManager.clear();
            currentStage.close();
            UtilsFactory.getInstance().showWindow("login");
        } else {
            ControllerUtils.showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Đăng xuất không thành công");
        }
    }

    /**
     * Static method to allow other controllers to trigger avatar refresh
     */
    public static void updateAvatar() {
        if (instance != null) {
            instance.refreshAvatar();
        }
    }
}
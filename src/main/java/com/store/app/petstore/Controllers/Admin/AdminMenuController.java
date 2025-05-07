package com.store.app.petstore.Controllers.Admin;

import com.store.app.petstore.Controllers.ControllerUtils;
import com.store.app.petstore.Models.Entities.Staff;
import com.store.app.petstore.Models.Entities.User;
import com.store.app.petstore.Sessions.SessionManager;
import com.store.app.petstore.Views.AdminFactory;
import com.store.app.petstore.Views.ModelView;
import com.store.app.petstore.Views.ViewFactory;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class AdminMenuController implements Initializable {
    @FXML
    private MenuBar menuBar;

    @FXML
    private Menu menuHome;

    @FXML
    private MenuItem menuItemAccounts;

    @FXML
    private MenuItem menuItemBestSelling;

    @FXML
    private MenuItem menuItemCustomers;

    @FXML
    private MenuItem menuItemDiscounts;

    @FXML
    private MenuItem menuItemOverview;

    @FXML
    private MenuItem menuItemPets;

    @FXML
    private MenuItem menuItemProducts;

    @FXML
    private MenuItem menuItemRevenue;

    @FXML
    private MenuItem menuItemStaff;

    @FXML
    private Menu menuLogout;

    @FXML
    private Menu menuManagement;

    @FXML
    private Menu menuReports;

    @FXML
    private Menu menuRevenue;

    @FXML
    private Menu menuStatistics;

    @FXML
    private Label nameLabel;

    @FXML
    private AnchorPane root;

    @FXML
    private Circle userImage;

    @FXML
    private Label usernameLabel;

    private SessionManager sessionManager;
    private User currentUser;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sessionManager = new SessionManager();
        currentUser = sessionManager.getCurrentUser();

        setupUserName();
        setupUserImage();
        root.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                setMenu();
            }
        });
    }

    private void setupUserName() {
        if (currentUser != null) {
            usernameLabel.setText(currentUser.getUsername());
        } else {
            nameLabel.setText("Chưa cập nhật thông tin");
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

            javafx.scene.image.WritableImage clippedImage = imageView.snapshot(null, null);

            userImage.setFill(new ImagePattern(clippedImage));
        } catch (Exception ex) {
            System.err.println("Error loading user image: " + ex.getMessage());
            userImage.setFill(javafx.scene.paint.Color.GRAY);
        }
    }

    private void setMenu() {
        Stage currentStage = (Stage) root.getScene().getWindow();
        //Dashboard
        menuHome.setOnAction(e -> {
            AdminFactory.getInstance().switchContent("dashboard", currentStage);
        });

        //Management
        menuItemAccounts.setOnAction(e -> {
            AdminFactory.getInstance().switchContent("usermanagement", currentStage);
        });
        menuItemPets.setOnAction(e -> {
            AdminFactory.getInstance().switchContent("petmanagement", currentStage);
        });
        menuItemProducts.setOnAction(e -> {
            AdminFactory.getInstance().switchContent("productmanagement", currentStage);
        });
        menuItemDiscounts.setOnAction(e -> {
            AdminFactory.getInstance().switchContent("discountmanagement", currentStage);
        });
        menuItemCustomers.setOnAction(e -> {
            AdminFactory.getInstance().switchContent("customermanagement", currentStage);
        });
        menuItemStaff.setOnAction(e -> {
            AdminFactory.getInstance().switchContent("staffmanagement", currentStage);
        });

        //Statistics
        menuItemOverview.setOnAction(e -> {
            AdminFactory.getInstance().switchContent("overview", currentStage);
        });

        menuItemRevenue.setOnAction(e -> {
            AdminFactory.getInstance().switchContent("revenue", currentStage);
        });

        menuItemBestSelling.setOnAction(e -> {
            AdminFactory.getInstance().switchContent("bestseller", currentStage);
        });

        menuLogout.setOnAction(e -> {
            handleLogout(null);
        });
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

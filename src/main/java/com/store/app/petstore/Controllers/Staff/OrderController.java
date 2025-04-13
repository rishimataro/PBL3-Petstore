package com.store.app.petstore.Controllers.Staff;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;

import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

import com.store.app.petstore.Models.Entities.Pet;

public class OrderController implements Initializable {

    @FXML
    private AnchorPane root;

    @FXML
    private Circle userImage;

    @FXML
    private FontAwesomeIconView menuIcon;

    private ContextMenu contextMenu;

    @FXML
    private GridPane grid;

    @FXML
    private ScrollPane scrollPane;

    private ArrayList<Pet> pets = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUserImage("src/main/resources/Images/dog.png");
        setMenu();

        int row = 0;
        int col = 0;
        ArrayList<Pet> pets = getPets();

        for (Pet pet : pets) {
            try {

                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/FXML/Staff/ItemList.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                ItemListController itemController = fxmlLoader.getController();

                if (pet != null) {
                    itemController.setData(pet);
                }

                if (col == 4) {
                    row++;
                    col = 0;
                }

                grid.add(anchorPane, col++, row);
                GridPane.setMargin(anchorPane, new Insets(10));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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

    private void setMenu() {
        MenuItem orderItem = new MenuItem("Đặt hàng");
        MenuItem billItem = new MenuItem("Lịch sử hóa đơn");
        MenuItem infoItem = new MenuItem("Thông tin tài khoản");
        MenuItem logoutItem = new MenuItem("Đăng xuất");

        contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(orderItem, billItem, infoItem, logoutItem);

        orderItem.setOnAction(event -> System.out.println("Đặt hàng"));
        billItem.setOnAction(event -> System.out.println("Lịch sử hóa đơn"));
        infoItem.setOnAction(e -> System.out.println("Xem thông tin tài khoản"));
        logoutItem.setOnAction(e -> System.out.println("Đăng xuất"));

        contextMenu.setAutoHide(true);

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

    private ArrayList<Pet> getPets() {
        ArrayList<Pet> pets = new ArrayList<>();
        Pet p;

        for(int i = 0; i < 20; i++) {
            p = new Pet();
            p.setName("LUCKY");
            p.setType("Chó");
            p.setBreed("Shiba");
            p.setImageUrl("/Images/dog.png");
            pets.add(p);
        }

        return pets;
    }

}

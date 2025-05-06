package com.store.app.petstore.Controllers.Staff;

import com.store.app.petstore.Models.Entities.Item;
import com.store.app.petstore.Models.Entities.Product;
import com.store.app.petstore.Controllers.ControllerUtils;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.util.Objects;
import java.util.function.Consumer;

public class ItemList2Controller {
    @FXML
    private FontAwesomeIconView downIcon;
    @FXML
    private ImageView imgPet;
    @FXML
    private Label namePet;
    @FXML
    private Label quantity;
    @FXML
    private Label total;
    @FXML
    private FontAwesomeIconView trashIcon;
    @FXML
    private Label unitPrice;
    @FXML
    private FontAwesomeIconView upIcon;
    @FXML
    private Item item;

    @FXML
    private void initialize() {
        
    }

    public void setData(Item item) {
        this.item = item;
        namePet.setText(item.getName());
        unitPrice.setText(ControllerUtils.formatCurrency(item.getPrice()));
        quantity.setText("1");
        updateTotal();
        Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(item.getImageUrl())));
        imgPet.setImage(img);
    }

    public Item getItem() {
        return item;
    }

    public double getTotal() {
        return ControllerUtils.parseCurrency(total.getText().isEmpty() ? "0" : total.getText());
    }

    public void AddItem(int amount) {
        int currentAmount = Integer.parseInt(quantity.getText().isEmpty() ? "0" : quantity.getText());
        int newAmount = currentAmount + amount;

        if (newAmount < 0) {
            newAmount = 0;
        }

        quantity.setText(String.valueOf(newAmount));
        updateTotal();
    }

    private Runnable onQuantityChanged;

    public void setOnQuantityChanged(Runnable callback) {
        this.onQuantityChanged = callback;
    }

    private void updateTotal() {
        double price = ControllerUtils.parseCurrency(unitPrice.getText());
        int quantityValue = Integer.parseInt(quantity.getText());
        double totalValue = price * quantityValue;
        total.setText(ControllerUtils.formatCurrency(totalValue));
        if (onQuantityChanged != null) {
            onQuantityChanged.run();
        }
    }

    private Consumer<AnchorPane> onDeleteCallback; // Callback để xóa item
    private AnchorPane parentPane; // Tham chiếu đến pane cha

    @FXML
    private void handleDelete(ActionEvent event) {
        if (onDeleteCallback != null && parentPane != null) {
            onDeleteCallback.accept(parentPane);
        }
    }

    public void setOnDeleteCallback(Consumer<AnchorPane> callback) {
        this.onDeleteCallback = callback;
    }

    public void setParentPane(AnchorPane pane) {
        this.parentPane = pane;
    }

    @FXML
    private void handleUpIcon() {
        int currentAmount = Integer.parseInt(quantity.getText());
        if (item instanceof Product product) {
            if (currentAmount < product.getStock()) {
                quantity.setText(String.valueOf(currentAmount + 1));
                updateTotal();
            }
        } else {
            ControllerUtils.showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Không thể tăng số lượng thú cưng!");
        }
    }

    @FXML
    private void handleDownIcon() {
        if (item instanceof Product product) {
            if (product.getStock() == 0) {
                ControllerUtils.showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Sản phẩm đã hết hàng!");
                return;
            }
            int currentAmount = Integer.parseInt(quantity.getText());
            if (currentAmount > 1) {
                quantity.setText(String.valueOf(currentAmount - 1));
                updateTotal();
            }
        }
        else {
            ControllerUtils.showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Không thể giảm số lượng thú cưng!");
        }
    }
}

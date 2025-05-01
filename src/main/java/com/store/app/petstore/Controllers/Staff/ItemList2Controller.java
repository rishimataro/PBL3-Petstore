package com.store.app.petstore.Controllers.Staff;

import com.store.app.petstore.Models.Entities.Pet;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;
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
    private Pet p;
    
    private Runnable onDeleteCallback;

    public void setData(Pet p) {
        this.p = p;
        namePet.setText(p.getName());
        unitPrice.setText(p.getPrice() + "");
        quantity.setText("1");
        updateTotal();
        Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(p.getImageUrl())));
        imgPet.setImage(img);
        
        setupTrashIconListener();
    }

    public void setOnDeleteCallback(Runnable callback) {
        this.onDeleteCallback = callback;
    }

    private void setupTrashIconListener() {
        trashIcon.setOnMouseClicked(e -> {
            if (onDeleteCallback != null) {
                onDeleteCallback.run();
            }
        });
    }

    public void setOnDeleteCallback(Runnable callback) {
        this.onDeleteCallback = callback;
    }

    public double getTotal() {
        return Double.parseDouble(total.getText().isEmpty() ? "0.0" : total.getText());
    }
    public void AddItem(int amount) {
        int currentAmount = Integer.parseInt(quantity.getText().isEmpty() ? "0" : quantity.getText());
        int newAmount = currentAmount + amount;

        if (newAmount < 0) {
            newAmount = 0;
        }

        quantity.setText(String.valueOf(newAmount));
        System.out.println(quantity.getText());

        updateTotal();
    }

    private void updateTotal() {
        double price = Double.parseDouble(unitPrice.getText());
        int quantityValue = Integer.parseInt(quantity.getText());
        double totalValue = price * quantityValue;
        total.setText(String.format("%.2f", totalValue));
    }
}

package com.store.app.petstore.Controllers.Staff;

import com.store.app.petstore.Controllers.ControllerUtils;
import com.store.app.petstore.Models.Entities.OrderDetail;
import com.store.app.petstore.Models.Entities.Pet;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;
import java.util.Objects;


public class PetItemDetailController {
    @FXML
    private ImageView petImg;
    @FXML
    private Label petName;
    @FXML
    private Label quality;
    @FXML
    private Label unitPrice;
    @FXML
    private Label total;
    @FXML
    public void setData(Pet pet, OrderDetail orderDetail){
        int quantity = orderDetail.getQuantity();
        double unitPriceValue = orderDetail.getUnitPrice();
        double totalValue = quantity * unitPriceValue;
        petName.setText(pet.getName());
        unitPrice.setText(ControllerUtils.formatCurrency(unitPriceValue));
        quality.setText(quantity + "");
        total.setText(ControllerUtils.formatCurrency(totalValue));
        InputStream imageStream = null;
        if (pet.getImageUrl() != null) {
            imageStream = getClass().getResourceAsStream(pet.getImageUrl());
        }

        Image img;
        if (imageStream != null) {
            img = new Image(imageStream);
        } else {
            img = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Images/noImage.png")));
        }
        petImg.setImage(img);
    }
}

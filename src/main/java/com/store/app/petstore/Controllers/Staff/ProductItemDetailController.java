package com.store.app.petstore.Controllers.Staff;

import com.store.app.petstore.Models.Entities.OrderDetail;
import com.store.app.petstore.Models.Entities.Product;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;
import java.util.Objects;

public class ProductItemDetailController {
    @FXML
    private ImageView productImg;
    @FXML
    private Label productName;
    @FXML
    private Label quality;
    @FXML
    private Label unitPrice;
    @FXML
    private Label total;

    @FXML
    public void setData(Product product, OrderDetail orderDetail){
        int quantity = orderDetail.getQuantity();
        double unitPriceValue = orderDetail.getUnitPrice();
        double totalValue = quantity * unitPriceValue;
        productName.setText(product.getName());
        unitPrice.setText(unitPriceValue + "");
        quality.setText(quantity + "");
        total.setText(totalValue + "");

        InputStream imageStream = null;
        try {
            if (product.getImageUrl() != null) {
                imageStream = getClass().getResourceAsStream(product.getImageUrl());
            }
        } catch (Exception e) {
            // Log the error if needed
        }

        Image img;
        if (imageStream != null) {
            img = new Image(imageStream);
        } else {
            img = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Images/noImage.png")));
        }
        productImg.setImage(img);
    }
}

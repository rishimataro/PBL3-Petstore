package com.store.app.petstore.Controllers.Admin;

import com.store.app.petstore.Models.Entities.Product;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class ProductInforController {
    @FXML
    private TextField productIdField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField categoryField;
    @FXML
    private TextField stockField;
    @FXML
    private TextField priceField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private Button addButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button editButton;
    @FXML
    private Button saveButton;
    @FXML
    private ImageView productImage;
    @FXML
    private Pane maskPane;

    private Product product;

    public void setData(Product product) {
        this.product = product;
        productIdField.setText(String.valueOf(product.getProductId()));
        nameField.setText(product.getName());
        categoryField.setText(product.getCategory());
        stockField.setText(String.valueOf(product.getStock()));
        priceField.setText(String.valueOf(product.getPrice()));
        descriptionArea.setText(product.getDescription());

        // Load product image if available
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Image img = new Image(getClass().getResourceAsStream(product.getImageUrl()));
            productImage.setImage(img);
        }
    }

    @FXML
    private void handleAdd() {
        // Clear all fields
        clearFields();
        // Enable editing
        enableEditing();
    }

    @FXML
    private void handleDelete() {
        // Implement delete functionality
        if (product != null) {
            // Delete product from database
            // Refresh product list
        }
    }

    @FXML
    private void handleEdit() {
        enableEditing();
    }

    @FXML
    private void handleSave() {
        if (product == null) {
            product = new Product();
        }

        // Update product with new values
        product.setName(nameField.getText());
        product.setCategory(categoryField.getText());
        product.setStock(Integer.parseInt(stockField.getText()));
        product.setPrice(Integer.parseInt(priceField.getText()));
        product.setDescription(descriptionArea.getText());

        // Save to database
        // Refresh product list

        disableEditing();
    }

    private void enableEditing() {
        nameField.setEditable(true);
        categoryField.setEditable(true);
        stockField.setEditable(true);
        priceField.setEditable(true);
        descriptionArea.setEditable(true);
    }

    private void disableEditing() {
        nameField.setEditable(false);
        categoryField.setEditable(false);
        stockField.setEditable(false);
        priceField.setEditable(false);
        descriptionArea.setEditable(false);
    }

    private void clearFields() {
        productIdField.clear();
        nameField.clear();
        categoryField.clear();
        stockField.clear();
        priceField.clear();
        descriptionArea.clear();
        productImage.setImage(null);
    }
} 
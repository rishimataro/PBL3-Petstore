package com.store.app.petstore.Utils;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 * Demo application to showcase the ImageUtils functionality.
 * This class demonstrates how to use the ImageUtils class to process images
 * to fit square and circular areas while maintaining aspect ratio.
 */
public class ImageProcessingDemo extends Application {

    private static final String[] TEST_IMAGES = {
        "/Images/Pet/pet1.jpg",
        "/Images/User/default.jpg",
        "/images/logo.png"
    };
    
    private static final String DEFAULT_IMAGE = "/images/logo.png";
    
    @Override
    public void start(Stage primaryStage) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(20);
        grid.setVgap(20);
        
        // Add headers
        grid.add(new Label("Original"), 0, 0);
        grid.add(new Label("Square (No Squeeze)"), 1, 0);
        grid.add(new Label("Circle"), 2, 0);
        
        int row = 1;
        for (String imagePath : TEST_IMAGES) {
            Image image = ImageUtils.loadImageWithDefault(imagePath, DEFAULT_IMAGE);
            if (image == null) continue;
            
            // Original image
            ImageView originalView = new ImageView(image);
            originalView.setFitWidth(100);
            originalView.setFitHeight(100);
            originalView.setPreserveRatio(true);
            
            // Square processed image
            ImageView squareView = ImageUtils.processToSquare(image, 100);
            
            // Circle processed image
            Circle circle = new Circle(50);
            circle.setFill(ImageUtils.processToCircle(image, 50));
            
            // Add to grid
            VBox originalBox = new VBox(10, originalView, new Label("Size: " + image.getWidth() + "x" + image.getHeight()));
            grid.add(originalBox, 0, row);
            grid.add(squareView, 1, row);
            grid.add(circle, 2, row);
            
            row++;
        }
        
        // Add a demonstration of a rectangle with rounded corners
        Image image = ImageUtils.loadImageWithDefault(TEST_IMAGES[0], DEFAULT_IMAGE);
        if (image != null) {
            Rectangle roundedRect = new Rectangle(0, 0, 100, 100);
            roundedRect.setArcWidth(20);
            roundedRect.setArcHeight(20);
            
            ImageView processedView = ImageUtils.processToSquare(image, 100);
            ImagePattern pattern = new ImagePattern(processedView.snapshot(null, null));
            roundedRect.setFill(pattern);
            
            grid.add(new Label("Rounded Rectangle:"), 0, row);
            grid.add(roundedRect, 1, row);
        }
        
        // Add a button to close the demo
        Button closeButton = new Button("Close Demo");
        closeButton.setOnAction(e -> primaryStage.close());
        grid.add(closeButton, 0, row + 1, 3, 1);
        
        Scene scene = new Scene(grid);
        primaryStage.setTitle("Image Processing Demo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    /**
     * Main method to launch the demo application
     */
    public static void main(String[] args) {
        launch(args);
    }
}

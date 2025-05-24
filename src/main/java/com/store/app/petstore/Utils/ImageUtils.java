package com.store.app.petstore.Utils;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.io.InputStream;

/**
 * Utility class for image processing operations in the application.
 * Provides methods for loading, cropping, and formatting images.
 */
public class ImageUtils {

    /**
     * Loads an image from a resource path and returns it.
     * If the image cannot be loaded, returns null.
     *
     * @param imagePath The resource path to the image
     * @return The loaded Image object or null if loading fails
     */
    public static Image loadImage(String imagePath) {
        try {
            InputStream imageStream = ImageUtils.class.getResourceAsStream(imagePath);
            if (imageStream != null) {
                return new Image(imageStream);
            }
        } catch (Exception e) {
            System.err.println("Error loading image '" + imagePath + "': " + e.getMessage());
        }
        return null;
    }

    /**
     * Loads an image from a resource path and returns it.
     * If the image cannot be loaded, returns the default image.
     *
     * @param imagePath The resource path to the image
     * @param defaultImagePath The resource path to the default image
     * @return The loaded Image object or the default image if loading fails
     */
    public static Image loadImageWithDefault(String imagePath, String defaultImagePath) {
        Image image = loadImage(imagePath);
        if (image == null) {
            image = loadImage(defaultImagePath);
        }
        return image;
    }

    /**
     * Processes an image to fit a square area while maintaining aspect ratio.
     * The image will be cropped if necessary to fill the square completely.
     *
     * @param image The source image to process
     * @param size The size of the square (width and height)
     * @return An ImageView containing the processed image
     */
    public static ImageView processToSquare(Image image, double size) {
        if (image == null) {
            return new ImageView();
        }

        ImageView imageView = new ImageView(image);
        
        // Calculate the crop dimensions to get a square from the center
        double imgWidth = image.getWidth();
        double imgHeight = image.getHeight();
        
        // Determine the crop size (take the smaller dimension)
        double cropSize = Math.min(imgWidth, imgHeight);
        
        // Calculate the crop origin (center the crop)
        double x = (imgWidth - cropSize) / 2;
        double y = (imgHeight - cropSize) / 2;
        
        // Set the viewport to crop the image
        imageView.setViewport(new Rectangle2D(x, y, cropSize, cropSize));
        
        // Set the size of the ImageView
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        
        // Ensure the image quality is good
        imageView.setSmooth(true);
        
        return imageView;
    }

    /**
     * Processes an image to fit a circular area while maintaining aspect ratio.
     * The image will be cropped if necessary to fill the circle completely.
     *
     * @param image The source image to process
     * @param radius The radius of the circle
     * @return An ImagePattern that can be used to fill a Circle shape
     */
    public static ImagePattern processToCircle(Image image, double radius) {
        if (image == null) {
            return new ImagePattern(null);
        }

        double size = radius * 2;
        
        // First process to square
        ImageView imageView = processToSquare(image, size);
        
        // Apply circular clipping
        Circle clip = new Circle(size / 2, size / 2, size / 2);
        imageView.setClip(clip);
        
        // Create a snapshot of the clipped image
        WritableImage clippedImage = imageView.snapshot(null, null);
        
        // Return as ImagePattern for easy use with shapes
        return new ImagePattern(clippedImage);
    }

    /**
     * Sets an image to an ImageView with square cropping.
     * The image will maintain its aspect ratio and be cropped if necessary.
     *
     * @param imageView The ImageView to set the image to
     * @param imagePath The resource path to the image
     * @param defaultImagePath The resource path to the default image
     * @return True if the image was set successfully, false otherwise
     */
    public static boolean setSquareImage(ImageView imageView, String imagePath, String defaultImagePath) {
        if (imageView == null) {
            return false;
        }

        Image image = loadImageWithDefault(imagePath, defaultImagePath);
        if (image == null) {
            return false;
        }

        double size = Math.min(imageView.getFitWidth(), imageView.getFitHeight());
        if (size <= 0) {
            size = 100; // Default size if not specified
        }

        ImageView processedImageView = processToSquare(image, size);
        imageView.setImage(processedImageView.getImage());
        imageView.setViewport(processedImageView.getViewport());
        
        return true;
    }

    /**
     * Creates a square thumbnail of the given image.
     * The image will maintain its aspect ratio and be cropped if necessary.
     *
     * @param image The source image
     * @param size The size of the square thumbnail
     * @return A new Image object containing the thumbnail
     */
    public static Image createSquareThumbnail(Image image, double size) {
        if (image == null) {
            return null;
        }

        ImageView imageView = processToSquare(image, size);
        return imageView.snapshot(null, null);
    }
}

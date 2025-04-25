package com.store.app.petstore.Views;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LoginView {
    private Stage stage;
    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton;
    private Label errorLabel;

    public LoginView(Stage stage) {
        this.stage = stage;
        createLoginView();
    }

    private void createLoginView() {
        // Create main container
        VBox mainContainer = new VBox(20);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(40));
        mainContainer.setStyle("-fx-background-color: #f5f6fa;");

        // Create logo
        ImageView logo = new ImageView(new Image(getClass().getResourceAsStream("/Images/logo1.png")));
        logo.setFitWidth(200);
        logo.setPreserveRatio(true);

        // Create title
        Label titleLabel = new Label("Welcome Back");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#2f3640"));

        // Create subtitle
        Label subtitleLabel = new Label("Please login to your account");
        subtitleLabel.setFont(Font.font("System", 14));
        subtitleLabel.setTextFill(Color.web("#7f8c8d"));

        // Create form container
        VBox formContainer = new VBox(15);
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setMaxWidth(400);
        formContainer.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 30;");

        // Username field
        VBox usernameContainer = new VBox(5);
        Label usernameLabel = new Label("Username");
        usernameLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        usernameLabel.setTextFill(Color.web("#2f3640"));
        
        HBox usernameFieldContainer = new HBox(10);
        usernameFieldContainer.setAlignment(Pos.CENTER_LEFT);
        usernameFieldContainer.setStyle("-fx-background-color: #f1f2f6; -fx-background-radius: 5; -fx-padding: 0 10 0 10;");
        
        FontAwesomeIconView userIcon = new FontAwesomeIconView(FontAwesomeIcon.USER);
        userIcon.setSize("20");
        userIcon.setFill(Color.web("#7f8c8d"));
        
        usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.setStyle("-fx-background-color: transparent; -fx-padding: 10;");
        usernameField.setPrefHeight(40);
        
        usernameFieldContainer.getChildren().addAll(userIcon, usernameField);
        usernameContainer.getChildren().addAll(usernameLabel, usernameFieldContainer);

        // Password field
        VBox passwordContainer = new VBox(5);
        Label passwordLabel = new Label("Password");
        passwordLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        passwordLabel.setTextFill(Color.web("#2f3640"));
        
        HBox passwordFieldContainer = new HBox(10);
        passwordFieldContainer.setAlignment(Pos.CENTER_LEFT);
        passwordFieldContainer.setStyle("-fx-background-color: #f1f2f6; -fx-background-radius: 5; -fx-padding: 0 10 0 10;");
        
        FontAwesomeIconView lockIcon = new FontAwesomeIconView(FontAwesomeIcon.LOCK);
        lockIcon.setSize("20");
        lockIcon.setFill(Color.web("#7f8c8d"));
        
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setStyle("-fx-background-color: transparent; -fx-padding: 10;");
        passwordField.setPrefHeight(40);
        
        passwordFieldContainer.getChildren().addAll(lockIcon, passwordField);
        passwordContainer.getChildren().addAll(passwordLabel, passwordFieldContainer);

        // Error label
        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12;");
        errorLabel.setVisible(false);

        // Login button
        loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #40739e; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        loginButton.setPrefWidth(200);
        loginButton.setPrefHeight(40);
        loginButton.setCursor(Cursor.HAND);
        
        FontAwesomeIconView arrowIcon = new FontAwesomeIconView(FontAwesomeIcon.ARROW_RIGHT);
        arrowIcon.setSize("16");
        arrowIcon.setFill(Color.WHITE);
        loginButton.setGraphic(arrowIcon);

        // Add hover effect to login button
        loginButton.setOnMouseEntered(e -> loginButton.setStyle("-fx-background-color: #487eb0; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;"));
        loginButton.setOnMouseExited(e -> loginButton.setStyle("-fx-background-color: #40739e; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;"));

        // Add all components to form container
        formContainer.getChildren().addAll(usernameContainer, passwordContainer, errorLabel, loginButton);

        // Add all components to main container
        mainContainer.getChildren().addAll(logo, titleLabel, subtitleLabel, formContainer);

        // Create scene
        Scene scene = new Scene(mainContainer, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/Styles/Login.css").toExternalForm());

        // Configure stage
        stage.setTitle("Pet Store - Login");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initStyle(StageStyle.DECORATED);
    }

    // Getters for controller access
    public TextField getUsernameField() {
        return usernameField;
    }

    public PasswordField getPasswordField() {
        return passwordField;
    }

    public Button getLoginButton() {
        return loginButton;
    }

    public Label getErrorLabel() {
        return errorLabel;
    }

    public void show() {
        stage.show();
    }
} 
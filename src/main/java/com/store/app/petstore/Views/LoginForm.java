package com.store.app.petstore.Views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.effect.DropShadow;

public class LoginForm {

    private VBox rootView;
    private TextField userField;
    private PasswordField passField;
    private Text msgText;

    public LoginForm() {
        createView();
    }

    private void createView() {
        rootView = new VBox(15);
        rootView.setAlignment(Pos.CENTER);
        rootView.setPadding(new Insets(40));
        rootView.setStyle("-fx-background-color: #F0F4F8;");

        msgText = new Text();
        msgText.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));

        rootView.getChildren().addAll(title(), formPane(), msgText);
    }

    private void handleLogin() {
        String username = userField.getText();
        String password = passField.getText();

        boolean isAuthenticated = username.equals("admin") && password.equals("admin");

        if(isAuthenticated){
            msgText.setFill(Color.GREEN);
            msgText.setText("Đăng nhập thành công!");
        } else {
            msgText.setFill(Color.RED);
            msgText.setText("Mật khẩu hoặc tên đăng nhập sai !");
        }
    }
    private Text title() {
        Text title = new Text("Đăng nhập ");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        title.setFill(Color.web("#404040"));
        return title;
    }
    private Label userLabel() {
        Label userLabel = new Label("Tên đăng nhập :");
        userLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        userField = new TextField();
        userField.setPromptText("Nhập tên đăng nhập ");
        userField.setPrefWidth(250);
        return userLabel;
    }
    private Label passLabel() {
        Label passLabel = new Label("Mật khẩu :");
        passLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        passField = new PasswordField();
        passField.setPromptText("Nhập mật khẩu");
        passField.setPrefWidth(250);
        return passLabel;
    }
    private Button loginBtn() {
        Button loginBtn = new Button("Đăng nhập");
        loginBtn.setPrefWidth(250);
        loginBtn.setStyle("-fx-background-color: #3D6EF7; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        loginBtn.setEffect(new DropShadow(5, Color.gray(0.5)));

        loginBtn.setOnAction(e -> handleLogin());
        return loginBtn;
    }
    private VBox formBox() {
        VBox formBox = new VBox(10, userLabel(), userField, passLabel(), passField, loginBtn());
        formBox.setAlignment(Pos.CENTER_LEFT);
        formBox.setPadding(new Insets(20));
        formBox.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
        formBox.setEffect(new DropShadow(10, Color.grayRgb(200)));
        return formBox;
    }
    private StackPane formPane() {
        StackPane formPane = new StackPane(formBox());
        formPane.setPadding(new Insets(20));
        return formPane;
    }

    public VBox getView() {
        return rootView;
    }
}
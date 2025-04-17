package com.store.app.petstore.Views;

import com.store.app.petstore.Controllers.LoginController;
import com.store.app.petstore.Controllers.Staff.StaffController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewFactory {
    private AnchorPane StaffView;

    public ViewFactory(){}

    public AnchorPane getStaffView() {
        if (StaffView == null) {
            try{
                StaffView = new FXMLLoader(getClass().getResource("/FXML/Staff/Staff.fxml")).load();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        return StaffView;
    }

    public void showOrderWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Staff/Staff.fxml"));
        StaffController staffController = new StaffController();
        loader.setController(staffController);
        Scene scene = null;

        try{
            scene = new Scene(loader.load());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Staff Order");
        stage.show();
    }
    public void showLoginWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

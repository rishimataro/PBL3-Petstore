package com.store.app.petstore.Controllers.Admin.Statistic;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class OverViewController {
    public void show(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Statistics");
        primaryStage.setScene(new Scene(createUI(), 900, 900));
        primaryStage.show();
    }
    public AnchorPane createAdminMenu() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Admin/AdminMenu.fxml"));
        AnchorPane pane = (AnchorPane) loader.load();
        return pane;
    }
    public AnchorPane createStatistic() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Admin/Statistics/Overview.fxml"));
        AnchorPane pane = (AnchorPane) loader.load();
        return pane;
    }
    public VBox createUI() throws IOException {
        VBox vBox = new VBox();
        vBox.setSpacing(10);

        vBox.getChildren().add(createAdminMenu());
        vBox.getChildren().add(createStatistic());
        return vBox;
    }
}

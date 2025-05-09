package com.store.app.petstore.Controllers.Admin.Statistic;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;

public class BestSellerController {
    @FXML private Button btnOverview;
    @FXML private Button btnRevenue;
    @FXML private Button btnBestSeller;
    @FXML private ChoiceBox<String> petFilter1;
    @FXML private ChoiceBox<String> petFilter2;
    @FXML private ChoiceBox<String> petFilter3;
    @FXML private Button btnViewPets;
    @FXML private Label lblPetStats;
    @FXML private ChoiceBox<String> staffFilter1;
    @FXML private ChoiceBox<String> staffFilter2;
    @FXML private ChoiceBox<String> staffFilter3;
    @FXML private Button btnViewStaff;
    @FXML private Label lblStaffStats;

    public void show(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Thống kê bán chạy");
        primaryStage.setScene(new Scene(createUI(), 900, 900));
        primaryStage.show();
    }

    @FXML
    public void initialize() {
        loadBestSellerStats();
    }

    private void loadBestSellerStats() {
        // TODO: Implement best seller statistics loading
    }

    public AnchorPane createAdminMenu() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Admin/AdminMenu.fxml"));
        return loader.load();
    }

    public AnchorPane createStatistic() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Admin/Statistics/BestSeller.fxml"));
        return loader.load();
    }

    public VBox createUI() throws IOException {
        VBox vBox = new VBox(10);
        vBox.getChildren().addAll(createAdminMenu(), createStatistic());
        return vBox;
    }
}

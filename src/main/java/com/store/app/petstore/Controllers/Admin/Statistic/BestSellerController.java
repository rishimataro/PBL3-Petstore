package com.store.app.petstore.Controllers.Admin.Statistic;

import com.store.app.petstore.DAO.StatisticDAO.BestSellerDAO;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.collections.FXCollections;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.ProgressBar;

import com.store.app.petstore.Models.Records.BestSellingItem;

import java.sql.SQLException;
import java.util.List;
import java.time.LocalDate;

public class BestSellerController {
    @FXML
    private DatePicker petFilter1;
    @FXML
    private DatePicker petFilter2;
    @FXML
    private ChoiceBox<String> petFilter3;
    @FXML
    private Label lblPetStats;
    @FXML
    private Label lblProductStats;
    @FXML
    private DatePicker productFilter1;
    @FXML
    private DatePicker productFilter2;
    @FXML
    private ChoiceBox<String> productFilter3;
    @FXML
    private javafx.scene.control.TableView<BestSellingItem> petTableView;
    @FXML
    private javafx.scene.control.TableView<BestSellingItem> productTableView;
    @FXML
    private ProgressBar petProgressBar;
    @FXML
    private ProgressBar productProgressBar;

//    public void show(Stage primaryStage) throws IOException {
//        primaryStage.setTitle("Thống kê bán chạy");
//        primaryStage.setScene(new Scene(createUI(), 900, 900));
//        primaryStage.show();
//    }

    @FXML
    public void initialize() {
        petFilter3.setItems(FXCollections.observableArrayList("Tất cả", "Mèo", "Chó"));
        petFilter3.setValue("Tất cả");
        productFilter3.setItems(FXCollections.observableArrayList("Tất cả", "Đồ chơi", "Thức ăn", "Phụ kiện"));
        productFilter3.setValue("Tất cả");

        // Set up Pet TableView columns
        petTableView.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("name"));
        petTableView.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("quantitySold"));

        // Set up Product TableView columns
        productTableView.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("name"));
        productTableView.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("quantitySold"));

    }

    @FXML
    private void onBtnViewProductClick() {
        LocalDate startDate = productFilter1.getValue();
        LocalDate endDate = productFilter2.getValue();
        String category = productFilter3.getValue();

        if (startDate == null || endDate == null) {
            System.out.println("Please select both start and end dates for Product statistics.");
            petTableView.setItems(FXCollections.observableArrayList());
            return;
        }

        productProgressBar.setVisible(true);
        productTableView.setPlaceholder(new Label(""));
        productTableView.setVisible(false);

        Task<List<BestSellingItem>> task = new Task<>() {
            @Override
            protected List<BestSellingItem> call() throws Exception {
                return getBestSellingProducts(startDate, endDate, category);
            }
        };

        task.setOnSucceeded(event -> {
            List<BestSellingItem> result = task.getValue();
            productTableView.setItems(FXCollections.observableArrayList(result));
            productProgressBar.setVisible(false);
            productTableView.setVisible(true);
            if (task.getValue().isEmpty()) {
                productTableView.setPlaceholder(new Label("Không có dữ liệu để hiển thị"));
            }
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            System.err.println("Error loading best selling pets: " + ex.getMessage());
            productTableView.setItems(FXCollections.observableArrayList());
            productProgressBar.setVisible(false);
            productTableView.setVisible(true);
            productTableView.setPlaceholder(new Label("Không có dữ liệu để hiển thị"));
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void onBtnViewPetsClick() {
        LocalDate startDate = petFilter1.getValue();
        LocalDate endDate = petFilter2.getValue();
        String category = petFilter3.getValue();

        if (startDate == null || endDate == null) {
            System.out.println("Please select both start and end dates for Pet statistics.");
            petTableView.setItems(FXCollections.observableArrayList());
            return;
        }

        petProgressBar.setVisible(true);
        petTableView.setPlaceholder(new Label(""));
        petTableView.setVisible(false);

        Task<List<BestSellingItem>> task = new Task<>() {
            @Override
            protected List<BestSellingItem> call() throws Exception {
                return getBestSellingPets(startDate, endDate, category);
            }
        };

        task.setOnSucceeded(event -> {
            List<BestSellingItem> result = task.getValue();
            petTableView.setItems(FXCollections.observableArrayList(result));
            petProgressBar.setVisible(false);
            petTableView.setVisible(true);
            if (task.getValue().isEmpty()) {
                petTableView.setPlaceholder(new Label("Không có dữ liệu để hiển thị"));
            }
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            System.err.println("Error loading best selling pets: " + ex.getMessage());
            productTableView.setItems(FXCollections.observableArrayList());
            petProgressBar.setVisible(false);
            petTableView.setVisible(true);
            petTableView.setPlaceholder(new Label("Không có dữ liệu để hiển thị"));
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private List<BestSellingItem> getBestSellingPets(LocalDate startDate, LocalDate endDate, String category) throws SQLException {
        return BestSellerDAO.getBestSellingPets(startDate, endDate, category);
    }
    private List<BestSellingItem> getBestSellingProducts(LocalDate startDate, LocalDate endDate, String category) throws SQLException {
        return BestSellerDAO.getBestSellingProducts(startDate, endDate, category);
    }

//    public AnchorPane createAdminMenu() throws IOException {
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Admin/AdminMenu.fxml"));
//        return loader.load();
//    }
//
//    public AnchorPane createStatistic() throws IOException {
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Admin/Statistics/BestSeller.fxml"));
//        return loader.load();
//    }

//    public VBox createUI() throws IOException {
//        VBox vBox = new VBox(10);
//        vBox.getChildren().addAll(createAdminMenu(), createStatistic());
//        return vBox;
//    }

}

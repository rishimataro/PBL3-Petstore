package com.store.app.petstore.Controllers.Staff;
import javafx.scene.Node;

import com.store.app.petstore.DAO.PetDAO;
import com.store.app.petstore.Models.Entities.Pet;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrderController {

    private final VBox root = new VBox();
    private final AnchorPane contentPane = new AnchorPane();
    private final TabPane tabPane = new TabPane();
    private final ScrollPane scrollPane = new ScrollPane();
    private final GridPane grid = new GridPane();
    private final List<Pet> petList = new ArrayList<>();
    private Button createNewTabButton;

    public void show(Stage stage) {
        setupLayout();
        loadPetList();
        configureGridLayout();
        populateGridWithPets();
        configureScrollPane();
        stage.setScene(new Scene(root, 990, 512)); // Adjust height to include VBox + AnchorPane
        stage.show();
    }

    private void setupLayout() {
        root.getChildren().add(createMenu()); // giả định StaffMenu.fxml thay bằng Node tương đương
        root.getChildren().add(contentPane);

        contentPane.setPrefSize(990, 442);
        contentPane.getStyleClass().add("root");
        contentPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/Styles/Staff/Order.css")).toExternalForm());

        // Right Pane - Đơn hàng
        AnchorPane rightPane = createRightPane();
        AnchorPane.setTopAnchor(rightPane, 0.0);
        AnchorPane.setRightAnchor(rightPane, 0.0);

        // Left Pane - Sản phẩm
        AnchorPane leftPane = createLeftPane();
        AnchorPane.setTopAnchor(leftPane, 0.0);
        AnchorPane.setLeftAnchor(leftPane, 0.0);

        contentPane.getChildren().addAll(leftPane, rightPane);
    }

    private Node createMenu() {
        // Giả định tạo placeholder cho StaffMenu
        return new Label("Staff Menu Placeholder");
    }

    private AnchorPane createRightPane() {
        AnchorPane rightPane = new AnchorPane();
        rightPane.setPrefSize(440, 442);

        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getStyleClass().add("tab-container");
        AnchorPane.setTopAnchor(tabPane, 15.0);
        AnchorPane.setRightAnchor(tabPane, 20.0);
        tabPane.setPrefSize(410, 280);

        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(10);
        infoGrid.setPrefSize(410, 80);
        AnchorPane.setTopAnchor(infoGrid, 300.0);
        AnchorPane.setRightAnchor(infoGrid, 20.0);

        infoGrid.getColumnConstraints().addAll(
                new ColumnConstraints(108),
                new ColumnConstraints(162),
                new ColumnConstraints(140)
        );
        infoGrid.getRowConstraints().addAll(
                new RowConstraints(30),
                new RowConstraints(30),
                new RowConstraints(30)
        );
        Label totalLabel = new Label("Tổng tiền:");
        totalLabel.getStyleClass().add("infomoney-label");
        Label voucherLabel = new Label("Voucher:");
        voucherLabel.getStyleClass().add("infomoney-label");
        GridPane.setRowIndex(voucherLabel, 1); // Corrected static call
        Label customerPayLabel = new Label("Khách cần trả:");
        customerPayLabel.getStyleClass().add("infomoney-label");
        GridPane.setRowIndex(customerPayLabel, 2); // Corrected static call

        TextField codeField = new TextField();
        codeField.setPromptText("Nhập code (nếu có)");
        codeField.getStyleClass().add("code-textfield");
        GridPane.setColumnIndex(codeField, 1);
        GridPane.setRowIndex(codeField, 1);

        Label money1 = new Label("0");
        money1.getStyleClass().add("money-label");
        GridPane.setColumnIndex(money1, 2);

        Label money2 = new Label("0");
        money2.getStyleClass().add("money-label");
        GridPane.setColumnIndex(money2, 2);
        GridPane.setRowIndex(money2, 1);

        Label money3 = new Label("0");
        money3.getStyleClass().add("money-label");
        GridPane.setColumnIndex(money3, 2);
        GridPane.setRowIndex(money3, 2);

        infoGrid.getChildren().addAll(totalLabel, voucherLabel, customerPayLabel, codeField, money1, money2, money3);

        Button confirmButton = new Button("Xác nhận");
        confirmButton.setPrefSize(119, 26);
        AnchorPane.setBottomAnchor(confirmButton, 20.0);
        AnchorPane.setRightAnchor(confirmButton, 20.0);

        createNewTabButton = new Button("Tạo đơn hàng mới");
        createNewTabButton.setPrefSize(119, 26);
        AnchorPane.setBottomAnchor(createNewTabButton, 50.0);
        AnchorPane.setRightAnchor(createNewTabButton, 20.0);
        createNewTabButton.setOnAction(e -> handleCreateNewTab());

        rightPane.getChildren().addAll(tabPane, infoGrid, confirmButton, createNewTabButton);
        return rightPane;
    }

    private AnchorPane createLeftPane() {
        AnchorPane leftPane = new AnchorPane();
        leftPane.setPrefSize(550, 442);

        TextField searchField = new TextField();
        searchField.setPromptText("Tìm kiếm sản phẩm");
        searchField.getStyleClass().add("find-textfield");
        AnchorPane.setTopAnchor(searchField, 15.0);
        AnchorPane.setLeftAnchor(searchField, 20.0);
        searchField.setPrefSize(270, 30);

        ChoiceBox<?> choiceBox1 = new ChoiceBox<>();
        choiceBox1.setStyle("-fx-border-color: #FFB500;");
        AnchorPane.setTopAnchor(choiceBox1, 15.0);
        AnchorPane.setLeftAnchor(choiceBox1, 305.0);

        ChoiceBox<?> choiceBox2 = new ChoiceBox<>();
        choiceBox2.setStyle("-fx-border-color: #017FCB;");
        AnchorPane.setTopAnchor(choiceBox2, 15.0);
        AnchorPane.setRightAnchor(choiceBox2, 15.0);

        scrollPane.setPrefSize(515, 370);
        AnchorPane.setLeftAnchor(scrollPane, 20.0);
        AnchorPane.setBottomAnchor(scrollPane, 20.0);

        leftPane.getChildren().addAll(searchField, choiceBox1, choiceBox2, scrollPane);
        return leftPane;
    }

    private void loadPetList() {
        petList.addAll(PetDAO.getInstance().findAll());
    }

    private void configureScrollPane() {
        grid.setMinSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        grid.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        grid.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        scrollPane.setContent(grid);
    }

    private void configureGridLayout() {
        grid.setHgap(0);
        grid.setVgap(0);
    }

    private void populateGridWithPets() {
        int column = 0;
        int row = 0;

        for (Pet pet : petList) {
            try {
                AnchorPane petPane = createPetItemPane(pet);
                petPane.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        createOrderTab(pet);
                    }
                });

                if (column == 4) {
                    column = 0;
                    row++;
                }

                grid.add(petPane, column++, row);
                GridPane.setMargin(petPane, new Insets(7));
            } catch (IOException e) {
                System.err.println("Failed to load pet item view: " + e.getMessage());
            }
        }
    }

    private AnchorPane createPetItemPane(Pet pet) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Staff/ItemList.fxml"));
        AnchorPane pane = loader.load();
        ItemListController controller = loader.getController();
        controller.setData(pet);
        return pane;
    }

    private void createOrderTab(Pet pet) {
        if (pet == null) return;

        Tab newTab = new Tab("Đơn hàng - " + pet.getName());

        VBox tabContent = new VBox();
        tabContent.getStyleClass().add("tab-content");

        tabContent.getChildren().add(new Label("Pet name: " + pet.getName()));
        tabContent.getChildren().add(new Label("Type: " + pet.getType()));
        tabContent.getChildren().add(new Label("Price: " + pet.getPrice()));

        ScrollPane contentScrollPane = new ScrollPane(tabContent);
        contentScrollPane.getStyleClass().add("scroll-pane");

        newTab.setContent(contentScrollPane);
        tabPane.getTabs().add(newTab);
        tabPane.getSelectionModel().select(newTab);
    }

    private void handleCreateNewTab() {
        // Custom logic for creating new tab
        Tab newTab = new Tab("Đơn hàng mới");
        VBox content = new VBox(new Label("Đơn hàng mới đang được xử lý..."));
        newTab.setContent(content);
        tabPane.getTabs().add(newTab);
        tabPane.getSelectionModel().select(newTab);
    }
}

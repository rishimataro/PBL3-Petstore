
package com.store.app.petstore.Controllers.Staff;

import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import com.store.app.petstore.DAO.PetDAO;
import com.store.app.petstore.Models.Entities.Pet;
import javafx.util.Duration;

import java.io.IOException;
import java.util.*;

public class OrderController {
    private int currentColumnCount = 0;
    private final PauseTransition resizeDebouncer = new PauseTransition(Duration.millis(300));
    private Map<Pet, AnchorPane> petPaneCache = new HashMap<>();
    private final VBox root = new VBox();
    private final AnchorPane contentPane = new AnchorPane();
    private final TabPane tabPane = new TabPane();
    private final ScrollPane scrollPane = new ScrollPane();
    private final GridPane grid = new GridPane();
    private final List<Pet> orderList = new ArrayList<>();
    private final List<Pet> petList = new ArrayList<>();
    private Button createNewTabButton;

    public void show(Stage stage) {
        setupLayout();
        loadPetList(stage);
        configureGridLayout();
        setupGridColumnBinding(stage);
        configureScrollPane();
        Scene scene = new Scene(root, 990, 512);
        stage.setScene(scene);
        scrollPane.maxHeightProperty().bind(scene.heightProperty());
        stage.show();
    }

    private Node createMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Staff/StaffMenu.fxml"));
            return loader.load();
        } catch (IOException e) {
            System.err.println("Error loading StaffMenu.fxml: " + e.getMessage());
            return new Label("Error loading menu");
        }
    }

    private void setupLayout() {
        root.getChildren().add(createMenu()); // Add StaffMenu to the root
        root.getChildren().add(contentPane);
        contentPane.setPrefSize(990, 442); // Adjust content pane size
        contentPane.getStyleClass().add("root");
        contentPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/Styles/Staff/Order.css")).toExternalForm());

        AnchorPane rightPane = createRightPane();
        AnchorPane.setTopAnchor(rightPane, 0.0);
        AnchorPane.setRightAnchor(rightPane, 0.0);

        AnchorPane leftPane = createLeftPane();
        AnchorPane.setTopAnchor(leftPane, 0.0);
        AnchorPane.setLeftAnchor(leftPane, 0.0);

        contentPane.getChildren().addAll(leftPane, rightPane);
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
        infoGrid.getColumnConstraints().addAll(new ColumnConstraints(108), new ColumnConstraints(162), new ColumnConstraints(140));
        infoGrid.getRowConstraints().addAll(new RowConstraints(30), new RowConstraints(30), new RowConstraints(30));

        Label totalLabel = new Label("Tổng tiền:");
        totalLabel.getStyleClass().add("infomoney-label");
        Label voucherLabel = new Label("Voucher:");
        voucherLabel.getStyleClass().add("infomoney-label");
        GridPane.setRowIndex(voucherLabel, 1);
        Label customerPayLabel = new Label("Khách cần trả:");
        customerPayLabel.getStyleClass().add("infomoney-label");
        GridPane.setRowIndex(customerPayLabel, 2);

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

        TextField searchField = new TextField();
        searchField.setPromptText("Tìm kiếm sản phẩm");
        searchField.getStyleClass().add("find-textfield");
        AnchorPane.setTopAnchor(searchField, 15.0);
        AnchorPane.setLeftAnchor(searchField, 20.0);
        searchField.setPrefSize(270, 30);

        ChoiceBox<?> choiceBox1 = new ChoiceBox<>();
        choiceBox1.setStyle("-fx-border-color: #FFB500;");
        ChoiceBox<?> choiceBox2 = new ChoiceBox<>();
        choiceBox2.setStyle("-fx-border-color: #017FCB;");
        HBox hbox = new HBox(10);
        hbox.getChildren().addAll(choiceBox1, choiceBox2);
        hbox.setAlignment(Pos.CENTER_RIGHT);
        hbox.setPrefWidth(260);

        AnchorPane.setTopAnchor(hbox, 15.0);
        AnchorPane.setRightAnchor(hbox, 20.0);
        AnchorPane.setTopAnchor(scrollPane, 60.0);
        scrollPane.setPadding(new Insets(10, 0, 120, 0));

        leftPane.getChildren().addAll(searchField, hbox, scrollPane);
        return leftPane;
    }

    private void loadPetList(Stage stage) {
        Task<List<Pet>> task = new Task<>() {
            @Override
            protected List<Pet> call() {
                return PetDAO.getInstance().findAll();
            }
            @Override
            protected void succeeded() {
                petList.clear();
                petList.addAll(getValue());
                rearrangeGridItems(); // thêm dòng này
                updateGridColumns(calculateOptimalColumnCount(stage.getWidth()));
            }
            @Override
            protected void failed() {
                getException().printStackTrace();
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void configureScrollPane() {
        scrollPane.setContent(grid);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        scrollPane.setPrefHeight(Region.USE_COMPUTED_SIZE);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        scrollPane.setPannable(true);
    }

    private void configureGridLayout() {
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(10));

        grid.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        grid.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        grid.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    }

    private void setupGridColumnBinding(Stage stage) {
        resizeDebouncer.setOnFinished(e -> {
            int newColumnCount = calculateOptimalColumnCount(stage.getWidth());
            updateGridColumns(newColumnCount);
        });
        stage.widthProperty().addListener((obs, oldVal, newVal) -> resizeDebouncer.playFromStart());
    }

    private int calculateOptimalColumnCount(double windowWidth) {
        double availableWidth = windowWidth - 600;
        int columnCount = (int)Math.floor(availableWidth / 110);
        return Math.min(9, Math.max(1, columnCount));
    }

    private void updateGridColumns(int newColumnCount) {
        if (newColumnCount == currentColumnCount) return;
        currentColumnCount = newColumnCount;
        rearrangeGridItems();
    }

    private void rearrangeGridItems() {
        grid.getChildren().clear();
        int column = 0, row = 0;
        double itemWidth = calculateItemWidth(grid.getWidth(), currentColumnCount);

        for (Pet pet : petList) {
            try {
                AnchorPane petPane = getOrCreatePetPane(pet);

                petPane.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        try {
                            createOrderTab(pet);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                if (column == currentColumnCount) {
                    column = 0;
                    row++;
                }
                grid.add(petPane, column++, row);
                GridPane.setMargin(petPane, new Insets(7));
            } catch (IOException e) {
                System.err.println("Error loading pet: " + e.getMessage());
            }
        }
    }

    private double calculateItemWidth(double windowWidth, int columnCount) {
        double totalMargin = columnCount * 14;
        return (windowWidth - 420 - totalMargin) / columnCount;
    }

    private AnchorPane getOrCreatePetPane(Pet pet) throws IOException {
        if (petPaneCache.containsKey(pet)) {
            return petPaneCache.get(pet);
        }
        AnchorPane pane = createPetItemPane(pet);
        petPaneCache.put(pet, pane);
        return pane;
    }

    private AnchorPane createPetItemPane(Pet pet) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Staff/ItemList.fxml"));
        AnchorPane pane = loader.load();
        ItemListController controller = loader.getController();
        controller.setData(pet);
        return pane;
    }
    private AnchorPane createPetItemOrderPane(Pet pet) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Staff/ItemList2.fxml"));
        AnchorPane pane = loader.load();
        ItemList2Controller controller = loader.getController();
        controller.setData(pet);
        return pane;
    }

    private void createOrderTab(Pet pet) throws IOException {
        if (pet == null) return;

        Tab tab = tabPane.getSelectionModel().getSelectedItem();
        if (tab == null) return;

        ScrollPane scrollPane = (ScrollPane) tab.getContent();
        VBox tabContent = (VBox) scrollPane.getContent();

        // Kiểm tra xem pet đã có trong VBox chưa (dựa vào ID hoặc tên)
        boolean exists = tabContent.getChildren().stream().anyMatch(node -> {
            if (node instanceof AnchorPane pane) {
                return pane.getUserData() != null && pane.getUserData().equals(pet.getPetId()); // Giả sử pet có getId()
            }
            return false;
        });

        if (!exists) {
            AnchorPane itemPane = createPetItemOrderPane(pet);
            itemPane.setUserData(pet.getPetId()); // Gắn ID để tránh thêm trùng
            tabContent.getChildren().add(itemPane);
        }
    }

    private void handleCreateNewTab() {
        Tab newTab = new Tab("Đơn hàng mới");
        VBox content = new VBox(10);
        content.getStyleClass().add("tab-content");
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        newTab.setContent(scrollPane);
        tabPane.getTabs().add(newTab);
        tabPane.getSelectionModel().select(newTab);
    }
}

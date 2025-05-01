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
    private Label totalAmount;
    private Label finalAmount;
    private Label voucherDiscount;
    private final AnchorPane contentPane = new AnchorPane();
    private final TabPane tabPane = new TabPane();
    private final ScrollPane scrollPane = new ScrollPane();
    private final GridPane grid = new GridPane();
    private final List<Pet> petList = new ArrayList<>();
    private Button createNewTabButton;

    public void show(Stage stage) {
        setupLayout();
        loadPetList(stage);
        configureGridLayout();
        setupGridColumnBinding(stage);
        configureScrollPane();
        calcAmount();
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
        root.getChildren().add(createMenu());
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

        totalAmount = new Label("0");
        totalAmount.getStyleClass().add("money-label");
        GridPane.setColumnIndex(totalAmount, 2);
        voucherDiscount = new Label("0");
        voucherDiscount.getStyleClass().add("money-label");
        GridPane.setColumnIndex(voucherDiscount, 2);
        GridPane.setRowIndex(voucherDiscount, 1);
        finalAmount = new Label("0");
        finalAmount.getStyleClass().add("money-label");
        GridPane.setColumnIndex(finalAmount, 2);
        GridPane.setRowIndex(finalAmount, 2);

        infoGrid.getChildren().addAll(totalLabel, voucherLabel, customerPayLabel, codeField, totalAmount, voucherDiscount, finalAmount);

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
        int columnCount = (int) Math.floor(availableWidth / 110);
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
        if (controller != null) {
            controller.setData(pet);
            pane.getProperties().put("controller", controller);
        }
        return pane;
    }

    private boolean isPetAlreadyInTab(Pet pet, VBox tabContent) {
        for (Node node : tabContent.getChildren()) {
            if (node instanceof AnchorPane pane) {
                if (pane.getUserData() != null && pane.getUserData().equals(pet.getPetId())) {
                    return true;
                }
            }
        }
        return false;
    }
    private void createOrderTab(Pet pet) throws IOException {
        if (pet == null) {
            return;
        }

        Tab tab = tabPane.getSelectionModel().getSelectedItem();
        if (tab == null || !(tab.getContent() instanceof ScrollPane)) {
            return;
        }

        ScrollPane scrollPane = (ScrollPane) tab.getContent();
        if (!(scrollPane.getContent() instanceof VBox)) {
            return;
        }

        VBox tabContent = (VBox) scrollPane.getContent();

        // Check if pet already exists in tab
        for (Node node : tabContent.getChildren()) {
            if (node instanceof AnchorPane pane && 
                pane.getUserData() != null && 
                pane.getUserData().equals(pet.getPetId())) {
                
                Object controllerObj = pane.getProperties().get("controller");
                if (controllerObj instanceof ItemList2Controller controller) {
                    controller.AddItem(1);
                }
                return;
            }
        }

        // Add new pet item to tab
        AnchorPane itemPane = createPetItemOrderPane(pet);
        itemPane.setUserData(pet.getPetId());
        tabContent.getChildren().add(itemPane);
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

    private void calcAmount() {
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == null || !(newTab.getContent() instanceof ScrollPane)) {
                return;
            }

            ScrollPane scrollPane = (ScrollPane) newTab.getContent();
            if (!(scrollPane.getContent() instanceof VBox)) {
                return;
            }

            VBox tabContent = (VBox) scrollPane.getContent();
            double total = 0;
            System.out.println("SEtp0");
            for (Node node : tabContent.getChildren()) {
                System.out.println("Step1");
                if (node instanceof AnchorPane pane) {
                    System.out.println("Step2");
                    Object controllerObj = pane.getProperties().get("controller");

                    System.out.println("Step3");
                    if (controllerObj instanceof ItemList2Controller controller) {
                        total += controller.getTotal();
                    }
                }
            }

            totalAmount.setText(String.format("%.0f", total));
        });
    }
}

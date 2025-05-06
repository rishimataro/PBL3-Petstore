package com.store.app.petstore.Controllers.Staff;

import com.store.app.petstore.DAO.PetDAO;
import com.store.app.petstore.DAO.ProductDAO;
import com.store.app.petstore.DAO.DiscountDAO;
import com.store.app.petstore.Models.Entities.Item;
import com.store.app.petstore.Models.Entities.Pet;
import com.store.app.petstore.Models.Entities.Product;
import com.store.app.petstore.Models.Entities.Discount;
import com.store.app.petstore.Controllers.ControllerUtils;
import com.store.app.petstore.Views.ViewFactory;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.util.Duration;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class OrderController implements Initializable {
    @FXML
    private ChoiceBox<String> categoryChoiceBox;

    @FXML
    private ChoiceBox<String> sortChoiceBox;

    @FXML
    private FontAwesomeIconView clearSearchIcon;

    @FXML
    private Button confirmButton;

    @FXML
    private Button createNewTabButton;

    @FXML
    private Label finalMoneyLabel;

    @FXML
    private Label finalMoneyValueLabel;

    @FXML
    private AnchorPane root;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private FontAwesomeIconView searchIcon;

    @FXML
    private TextField searchTextField;

    @FXML
    private TabPane tabPane;

    @FXML
    private Label totalMoneyLabel;

    @FXML
    private Label totalMoneyValueLabel;

    @FXML
    private Label voucherLabel;

    @FXML
    private TextField voucherTextField;

    @FXML
    private Label voucherValueLabel;

    private final FlowPane flowPane = new FlowPane();
    private Map<Item, AnchorPane> itemPaneCache = new HashMap<>();
    private int currentColumnCount = 0;
    private final List<Item> itemList = new ArrayList<>();
    private final PauseTransition resizeDebouncer = new PauseTransition(Duration.millis(300));
    private final PauseTransition searchDebouncer = new PauseTransition(Duration.millis(300));
    private Task<List<?>> currentSearchTask = null;
    private final Map<String, List<?>> searchCache = new HashMap<>();
    private static final int SEARCH_LIMIT = 50;
    private static final int CACHE_SIZE = 100;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupChoiceBox();
        setupGridLayout();
        configureScrollPane();
        loadItemsByCategory("Thú cưng");
        setupGridColumnBinding();
        handleCreateNewTab();
        calcAmount();
        setupClearSearchIcon();
        setupDiscountHandling();
        
        // Add search functionality with debounce
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() >= 2 || newValue.isEmpty()) {
                searchDebouncer.playFromStart();
            }
        });
        
        searchDebouncer.setOnFinished(event -> {
            handleSearch();
        });
    }

    private void setupButtonActions() {
        confirmButton.setOnAction(event -> {
            setupConfirm();
        });

        createNewTabButton.setOnAction(event -> {
            handleCreateNewTab();
        });
    }

    private void setupConfirm() {
        if (tabPane.getTabs().isEmpty()) {
            ControllerUtils.showAlert(Alert.AlertType.WARNING, "Thông báo", "Vui lòng tạo đơn hàng trước khi xác nhận!");
            return;
        }
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        if (selectedTab == null || !(selectedTab.getContent() instanceof ScrollPane)) {
            ControllerUtils.showAlert(Alert.AlertType.WARNING, "Thông báo", "Vui lòng chọn đơn hàng để xác nhận!");
            return;
        }
        ScrollPane tabScrollPane = (ScrollPane) selectedTab.getContent();
        if (tabScrollPane.getContent() instanceof VBox) {
            VBox tabContent = (VBox) tabScrollPane.getContent();
            if (tabContent.getChildren().isEmpty()) {
                ControllerUtils.showAlert(Alert.AlertType.WARNING, "Thông báo", "Đơn hàng không có sản phẩm nào!");
                return;
            }

            Stage currentStage = (Stage) root.getScene().getWindow();
            ViewFactory.getInstance().switchContent("payment", currentStage);

        } else {
            ControllerUtils.showAlert(Alert.AlertType.WARNING, "Thông báo", "Vui lòng chọn đơn hàng để xác nhận!");
        }
    }

    private void setupClearSearchIcon() {
        clearSearchIcon.setVisible(false);
        clearSearchIcon.setOnMouseClicked(event -> {
            searchTextField.clear();
            searchTextField.requestFocus();
            clearSearchIcon.setVisible(false);
        });
    }

    private void setupChoiceBox() {
        categoryChoiceBox.getItems().addAll("Thú cưng", "Phụ kiện", "Đồ chơi", "Thức ăn");
        categoryChoiceBox.setValue("Thú cưng");
        categoryChoiceBox.setOnAction(event -> {
            String selectedCategory = categoryChoiceBox.getValue();
            loadItemsByCategory(selectedCategory);
        });

        sortChoiceBox.getItems().addAll("Mới nhất", "Giá thấp đến cao", "Giá cao đến thấp");
        sortChoiceBox.setValue("Mới nhất");
        sortChoiceBox.setOnAction(event -> {
            String selectedSort = sortChoiceBox.getValue();
            sortItems(selectedSort);
        });
    }

    private void sortItems(String sortOption) {
        if (itemList.isEmpty()) return;

        Comparator<Item> priceComparator = (item1, item2) -> {
            double price1 = item1 instanceof Pet ? ((Pet) item1).getPrice() : ((Product) item1).getPrice();
            double price2 = item2 instanceof Pet ? ((Pet) item2).getPrice() : ((Product) item2).getPrice();
            return Double.compare(price1, price2);
        };

        switch (sortOption) {
            case "Giá thấp đến cao":
                itemList.sort(priceComparator);
                break;
            case "Giá cao đến thấp":
                itemList.sort(priceComparator.reversed());
                break;
            case "Mới nhất":
                break;
        }
        rearrangeGridItems();
    }

    private void setupGridLayout() {
        flowPane.setHgap(12);
        flowPane.setVgap(15);
        flowPane.setPadding(new Insets(2));
        flowPane.setPrefWrapLength(0);
    }

    private void configureScrollPane() {
        scrollPane.setContent(flowPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefHeight(Region.USE_COMPUTED_SIZE);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPannable(true);
    }

    private void loadItemsByCategory(String category) {
        Task<List<?>> task = new Task<>() {
            @Override
            protected List<?> call() {
                if (category.equals("Thú cưng")) {
                    return PetDAO.getInstance().findAll().stream()
                            .filter(pet -> !pet.getIsSold())
                            .collect(Collectors.toList());
                } else {
                    return ProductDAO.getInstance().findByCategory(category).stream()
                            .filter(product -> !product.getIsSold())
                            .collect(Collectors.toList());
                }
            }
            @Override
            protected void succeeded() {
                List<?> items = getValue();
                if (items != null) {
                    itemList.clear();
                    itemList.addAll((List<Item>) items);
                    rearrangeGridItems();
                }
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

    private void setupGridColumnBinding() {
        resizeDebouncer.setOnFinished(e -> {
            int newColumnCount = calculateOptimalColumnCount(root.getWidth());
            updateGridColumns(newColumnCount);
        });
        root.widthProperty().addListener((obs, oldVal, newVal) -> resizeDebouncer.playFromStart());
    }

    private int calculateOptimalColumnCount(double windowWidth) {
        double availableWidth = windowWidth - 600;
        int columnCount = (int) Math.floor(availableWidth / 110);
        return Math.max(4, Math.min(9, columnCount));
    }

    private void updateGridColumns(int newColumnCount) {
        if (newColumnCount == currentColumnCount) return;
        currentColumnCount = newColumnCount;
        rearrangeGridItems();
    }

    private void rearrangeGridItems() {
        flowPane.getChildren().clear();
        for (Item item : itemList) {
            try {
                if (item instanceof Pet pet && isPetInAnyOrderTab(pet)) {
                    continue;
                }

                if (item instanceof Product product && product.getStock() <= 0) {
                    continue;
                }
                
                AnchorPane itemPane = getOrCreateItemPane(item);
                itemPane.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        try {
                            createOrderTab(item);
                            // If it's a pet, remove it from display immediately
                            if (item instanceof Pet) {
                                flowPane.getChildren().remove(itemPane);
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                flowPane.getChildren().add(itemPane);
            } catch (IOException e) {
                System.err.println("Error loading item: " + e.getMessage());
            }
        }
    }

    private AnchorPane getOrCreateItemPane(Item item) throws IOException {
        if (itemPaneCache.containsKey(item)) {
            return itemPaneCache.get(item);
        }
        AnchorPane pane = createItemPane(item);
        itemPaneCache.put(item, pane);
        return pane;
    }

    private AnchorPane createItemPane(Item item) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Staff/ItemList.fxml"));
        AnchorPane pane = loader.load();
        ItemListController controller = loader.getController();
        controller.setData(item);
        return pane;
    }

    @FXML
    public void handleCreateNewTab() {
        Tab newTab = new Tab("Đơn hàng mới");
        VBox content = new VBox(10);
        content.setAlignment(Pos.CENTER);
        content.getStyleClass().add("tab-content");
        content.setPadding(new Insets(10,3,10,20));
        ScrollPane tabScrollPane = new ScrollPane(content);
        tabScrollPane.setPrefHeight(300);
        tabScrollPane.setPrefWidth(430);
        newTab.setContent(tabScrollPane);
        tabPane.getTabs().add(newTab);
        tabPane.getSelectionModel().select(newTab);
    }

    private VBox getCurrentTabContent() {
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        if (selectedTab != null && selectedTab.getContent() instanceof ScrollPane) {
            ScrollPane tabScrollPane = (ScrollPane) selectedTab.getContent();
            if (tabScrollPane.getContent() instanceof VBox) {
                return (VBox) tabScrollPane.getContent();
            }
        }
        return null;
    }

    private Consumer<AnchorPane> createDeleteCallback() {
        return paneToRemove -> {
            VBox tabContent = getCurrentTabContent();
            if (tabContent != null) {
                FadeTransition fadeTransition = new FadeTransition(Duration.millis(200), paneToRemove);
                fadeTransition.setFromValue(1.0);
                fadeTransition.setToValue(0.0);
                fadeTransition.setOnFinished(e -> {
                    tabContent.getChildren().remove(paneToRemove);
                    Tab tab = tabPane.getSelectionModel().getSelectedItem();
                    if (tab == null || !(tab.getContent() instanceof ScrollPane)) {
                        return;
                    }
                    updateAmount(tab);
                    
                    // If it was a pet, add it back to the display
                    Object controllerObj = paneToRemove.getProperties().get("controller");
                    if (controllerObj instanceof ItemList2Controller controller) {
                        Item item = controller.getItem();
                        if (item instanceof Pet) {
                            try {
                                AnchorPane itemPane = getOrCreateItemPane(item);
                                itemPane.setOnMouseClicked(event -> {
                                    if (event.getButton() == MouseButton.PRIMARY) {
                                        try {
                                            createOrderTab(item);
                                            flowPane.getChildren().remove(itemPane);
                                        } catch (IOException ex) {
                                            throw new RuntimeException(ex);
                                        }
                                    }
                                });
                                flowPane.getChildren().add(itemPane);
                            } catch (IOException ex) {
                                System.err.println("Error adding pet back to display: " + ex.getMessage());
                            }
                        }
                    }
                });
                fadeTransition.play();
            }
        };
    }

    private AnchorPane createItemOrderPane(Item item) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Staff/ItemList2.fxml"));
        AnchorPane pane = loader.load();
        ItemList2Controller controller = loader.getController();
        if (controller != null) {
            controller.setParentPane(pane);
            controller.setOnDeleteCallback(createDeleteCallback());
            controller.setData(item);
            controller.setOnQuantityChanged(() -> updateAmount(tabPane.getSelectionModel().getSelectedItem()));
            pane.setUserData(item.getId());
            pane.getProperties().put("controller", controller);
        }
        return pane;
    }

    private boolean isPetInAnyOrderTab(Pet pet) {
        return tabPane.getTabs().stream()
            .map(tab -> (ScrollPane) tab.getContent())
            .filter(scrollPane -> scrollPane.getContent() instanceof VBox)
            .map(scrollPane -> (VBox) scrollPane.getContent())
            .flatMap(vbox -> vbox.getChildren().stream())
            .filter(node -> node instanceof AnchorPane)
            .map(node -> (AnchorPane) node)
            .map(pane -> (ItemList2Controller) pane.getProperties().get("controller"))
            .filter(Objects::nonNull)
            .map(ItemList2Controller::getItem)
            .filter(item -> item instanceof Pet)
            .anyMatch(item -> item.getId() == pet.getId());
    }

    private void createOrderTab(Item item) throws IOException {
        if (item == null) {
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

        if (item instanceof Pet pet) {
            if (isPetInAnyOrderTab(pet)) {
                return;
            }
        }

        for (Node node : tabContent.getChildren()) {
            if (node instanceof AnchorPane pane) {
                Object controllerObj = pane.getProperties().get("controller");
                if (controllerObj instanceof ItemList2Controller controller) {
                    if (controller.getItem().getId() == item.getId()) {
                        controller.AddItem(1);
                        updateAmount(tab);
                        return;
                    }
                }
            }
        }

        AnchorPane itemPane = createItemOrderPane(item);
        itemPane.setUserData(item.getId());
        tabContent.getChildren().add(itemPane);

        updateAmount(tab);
    }

    private void updateAmount(Tab tab) {
        if (tab == null || !(tab.getContent() instanceof ScrollPane)) {
            return;
        }

        ScrollPane scrollPane = (ScrollPane) tab.getContent();
        if (!(scrollPane.getContent() instanceof VBox)) {
            return;
        }

        VBox tabContent = (VBox) scrollPane.getContent();
        double total = 0;
        for (Node node : tabContent.getChildren()) {
            if (node instanceof AnchorPane pane) {
                Object controllerObj = pane.getProperties().get("controller");

                if (controllerObj instanceof ItemList2Controller controller) {
                    total += controller.getTotal();
                }
            }
        }

        totalMoneyValueLabel.setText(ControllerUtils.formatCurrency(total));

        double finalAmountNumber = ControllerUtils.parseCurrency(totalMoneyValueLabel.getText()) - 
                                 ControllerUtils.parseCurrency(voucherValueLabel.getText());

        finalMoneyValueLabel.setText(ControllerUtils.formatCurrency(finalAmountNumber));
    }

    private void calcAmount() {
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            updateAmount(newTab);
        });
    }

    private void handleSearch() {
        String searchText = searchTextField.getText().trim();
        if (searchText.isEmpty()) {
            loadItemsByCategory(categoryChoiceBox.getValue());
            return;
        }

        String cacheKey = categoryChoiceBox.getValue() + "_" + searchText;
        if (searchCache.containsKey(cacheKey)) {
            itemList.clear();
            itemList.addAll((List<Item>) searchCache.get(cacheKey));
            rearrangeGridItems();
            return;
        }

        if (currentSearchTask != null && currentSearchTask.isRunning()) {
            currentSearchTask.cancel();
        }

        currentSearchTask = new Task<>() {
            @Override
            protected List<?> call() {
                if (categoryChoiceBox.getValue().equals("Thú cưng")) {
                    return PetDAO.getInstance().findAll().stream()
                            .filter(pet -> !pet.getIsSold())
                            .filter(pet -> pet.getName().toLowerCase().contains(searchText.toLowerCase()))
                            .limit(SEARCH_LIMIT)
                            .collect(Collectors.toList());
                } else {
                    return ProductDAO.getInstance().searchProducts(searchText).stream()
                            .filter(product -> !product.getIsSold())
                            .limit(SEARCH_LIMIT)
                            .collect(Collectors.toList());
                }
            }
        };

        currentSearchTask.setOnSucceeded(event -> {
            List<?> results = currentSearchTask.getValue();
            if (results != null) {
                // Update cache
                if (searchCache.size() >= CACHE_SIZE) {
                    searchCache.clear();
                }
                searchCache.put(cacheKey, results);

                itemList.clear();
                itemList.addAll((List<Item>) results);
                rearrangeGridItems();
            }
        });

        currentSearchTask.setOnFailed(event -> {
            currentSearchTask.getException().printStackTrace();
        });

        Thread searchThread = new Thread(currentSearchTask);
        searchThread.setDaemon(true);
        searchThread.start();
    }

    private void setupDiscountHandling() {
        voucherTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() >= 3) {
                handleDiscountCode(newValue);
            } else {
                voucherValueLabel.setText("0");
                updateAmount(tabPane.getSelectionModel().getSelectedItem());
            }
        });
    }

    private boolean isValidDiscount(Discount discount) {
        LocalDate today = LocalDate.now();
        
        if (today.isBefore(discount.getStartDate())) {
            ControllerUtils.showAlert(Alert.AlertType.WARNING, "Thông báo", 
                String.format("Mã giảm giá này sẽ có hiệu lực từ ngày %s", 
                discount.getStartDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
            return false;
        }
        
        if (today.isAfter(discount.getEndDate())) {
            ControllerUtils.showAlert(Alert.AlertType.WARNING, "Thông báo", 
                String.format("Mã giảm giá này đã hết hạn từ ngày %s", 
                discount.getEndDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
            return false;
        }
        
        return true;
    }

    private void handleDiscountCode(String code) {
        Task<Discount> task = new Task<>() {
            @Override
            protected Discount call() {
                try {
                    return DiscountDAO.getInstance().findByCode(code);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            }
        };

        task.setOnSucceeded(event -> {
            try {
                Discount discount = task.getValue();
                double totalAmount = ControllerUtils.parseCurrency(totalMoneyValueLabel.getText());
                String validationMessage = DiscountDAO.getInstance().validateDiscount(discount, totalAmount);
                
                if (validationMessage == null) {
                    applyDiscount(discount);
                } else {
                    voucherValueLabel.setText("0");
                    ControllerUtils.showAlert(Alert.AlertType.WARNING, "Thông báo", validationMessage);
                }
                updateAmount(tabPane.getSelectionModel().getSelectedItem());
            } catch (Exception e) {
                e.printStackTrace();
                voucherValueLabel.setText("0");
                ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Có lỗi xảy ra khi xử lý mã giảm giá!");
                updateAmount(tabPane.getSelectionModel().getSelectedItem());
            }
        });

        task.setOnFailed(event -> {
            Throwable exception = task.getException();
            if (exception != null) {
                exception.printStackTrace();
            }
            voucherValueLabel.setText("0");
            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể kiểm tra mã giảm giá! Vui lòng thử lại sau.");
            updateAmount(tabPane.getSelectionModel().getSelectedItem());
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void applyDiscount(Discount discount) {
        double totalAmount = ControllerUtils.parseCurrency(totalMoneyValueLabel.getText());
        double discountAmount;
        
        if (discount.getDiscountType().equals("percent")) {
            discountAmount = totalAmount * (discount.getValue() / 100);
            if (discount.getMaxDiscountValue() > 0) {
                discountAmount = Math.min(discountAmount, discount.getMaxDiscountValue());
            }
        } else {
            discountAmount = discount.getValue();
        }

        voucherValueLabel.setText(ControllerUtils.formatCurrency(discountAmount));
    }
}

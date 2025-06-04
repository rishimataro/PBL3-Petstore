package com.store.app.petstore.Controllers.Staff;

import com.store.app.petstore.DAO.PetDAO;
import com.store.app.petstore.DAO.ProductDAO;
import com.store.app.petstore.DAO.DiscountDAO;
import com.store.app.petstore.Models.Entities.Item;
import com.store.app.petstore.Models.Entities.Pet;
import com.store.app.petstore.Models.Entities.Product;
import com.store.app.petstore.Models.Entities.Discount;
import com.store.app.petstore.Models.Entities.Order;
import com.store.app.petstore.Models.Entities.OrderDetail;
import com.store.app.petstore.Controllers.ControllerUtils;
import com.store.app.petstore.Views.ViewFactory;
import com.store.app.petstore.Sessions.SessionManager;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.util.Duration;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class OrderController implements Initializable {
    private static final int SEARCH_LIMIT = 50;
    private static final int CACHE_SIZE = 100;
    private static final int DEBOUNCE_DELAY_MS = 100; // Further reduced for more responsive search
    private static final int INCREMENTAL_SEARCH_BATCH_SIZE = 10; // Number of items to show in incremental search
    private static final String DEFAULT_CATEGORY = "Thú cưng";
    private static final double SPACING = 10.0;

    private final SessionManager sessionManager = new SessionManager();

    @FXML private AnchorPane root;

    @FXML private ScrollPane scrollPane;
    private final FlowPane flowPane = new FlowPane();
    private int currentColumnCount = 0;
    private final List<Item> itemList = new ArrayList<>();
    private final Map<Item, AnchorPane> itemPaneCache = new HashMap<>();
    private final Set<Integer> temporarilyRemovedPetIds = new HashSet<>();

    @FXML private ChoiceBox<String> categoryChoiceBox;
    @FXML private ChoiceBox<String> sortChoiceBox;
    @FXML private TextField searchTextField;
    @FXML private FontAwesomeIconView searchIcon;
    @FXML private FontAwesomeIconView clearSearchIcon;
    private final PauseTransition searchDebouncer = new PauseTransition(Duration.millis(DEBOUNCE_DELAY_MS));
    private Task<List<?>> currentSearchTask = null;
    private final Map<String, List<?>> searchCache = new HashMap<>();
    private final PauseTransition resizeDebouncer = new PauseTransition(Duration.millis(DEBOUNCE_DELAY_MS));
    private final List<Item> incrementalSearchResults = new ArrayList<>();

    @FXML private AnchorPane orderContentPane;
    @FXML private ScrollPane orderScrollPane;
    private final VBox orderVBox = new VBox(SPACING);

    @FXML private Label totalMoneyLabel;
    @FXML private Label totalMoneyValueLabel;
    @FXML private Label voucherLabel;
    @FXML private TextField voucherTextField;
    @FXML private Label voucherValueLabel;
    @FXML private Label finalMoneyLabel;
    @FXML private Label finalMoneyValueLabel;

    @FXML private Button confirmButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupItemDisplayArea();
        setupOrderDisplayArea();
        setupFilterComponents();
        setupSearchComponents();
        setupDiscountHandling();
        setupButtonActions();

        Platform.runLater(() -> {
            searchTextField.setEditable(true);
            searchTextField.setFocusTraversable(true);
            searchTextField.setMouseTransparent(false);

            Parent parent = searchTextField.getParent();
            while (parent != null) {
                parent.setPickOnBounds(true);
                parent.setMouseTransparent(false);
                parent = parent.getParent();
            }
        });

        loadItemsByCategory(DEFAULT_CATEGORY);
        loadOrderFromSession();
//        orderScrollPane.setOnScroll(event -> {
//            double deltaY = event.getDeltaY() * 10;
//            double contentHeight = scrollPane.getContent().getBoundsInLocal().getHeight();
//            scrollPane.setVvalue(scrollPane.getVvalue() - deltaY / contentHeight);
//        });
    }

    private void setupItemDisplayArea() {
        setupGridLayout();
        configureScrollPane();
    }

    private void setupOrderDisplayArea() {
        orderVBox.setAlignment(Pos.TOP_CENTER);
        orderVBox.getStyleClass().add("tab-content");
        orderVBox.setPadding(new Insets(10, 3, 10, 20));
        orderScrollPane.setContent(orderVBox);
    }

    private void setupFilterComponents() {
        setupChoiceBox();
    }

    private void setupSearchComponents() {
        setupClearSearchIcon();

        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            clearSearchIcon.setVisible(!newValue.isEmpty());

            if (newValue.length() <= 2) {
                searchDebouncer.setDuration(Duration.millis(50));
            } else if (newValue.length() <= 4) {
                searchDebouncer.setDuration(Duration.millis(100));
            } else {
                searchDebouncer.setDuration(Duration.millis(150));
            }

            if (currentSearchTask != null && currentSearchTask.isRunning()) {
                currentSearchTask.cancel();
            }

            searchDebouncer.playFromStart();

            if (newValue.length() <= 2 && !newValue.isEmpty()) {
                handleSearch();
            }
        });

        searchDebouncer.setOnFinished(event -> handleSearch());
    }

    private void setupButtonActions() {
        confirmButton.setOnAction(event -> handleConfirmOrder());

        root.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (oldScene != null && newScene == null) {
                temporarilyRemovedPetIds.clear();
            }
        });
    }

    private void setupConfirm() {
        if (orderVBox.getChildren().isEmpty()) {
            ControllerUtils.showAlert(Alert.AlertType.WARNING, "Thông báo",
                "Vui lòng tạo đơn hàng trước khi xác nhận!");
            return;
        }
        Stage currentStage = (Stage) root.getScene().getWindow();
        ViewFactory.getInstance().switchContent("payment", currentStage);
    }

    private void setupClearSearchIcon() {
        clearSearchIcon.setVisible(false);
        searchTextField.setPickOnBounds(true);

        searchTextField.setOnMouseClicked(event -> {
            double textFieldWidth = searchTextField.getWidth();
            if (event.getX() > textFieldWidth - 30 && !searchTextField.getText().isEmpty()) {
                searchTextField.clear();
                searchTextField.requestFocus();
                clearSearchIcon.setVisible(false);

                loadItemsByCategory(categoryChoiceBox.getValue());
                event.consume();
            } else {
                searchTextField.requestFocus();
            }
        });

        clearSearchIcon.setOnMouseClicked(event -> {
            searchTextField.clear();
            searchTextField.requestFocus();
            clearSearchIcon.setVisible(false);
            loadItemsByCategory(categoryChoiceBox.getValue());
            event.consume();
        });

        clearSearchIcon.toFront();
        clearSearchIcon.setMouseTransparent(false);
    }

    private void setupChoiceBox() {
        categoryChoiceBox.getItems().addAll("Thú cưng", "Phụ kiện", "Đồ chơi", "Thức ăn");
        categoryChoiceBox.setValue(DEFAULT_CATEGORY);
        categoryChoiceBox.setOnAction(event -> {
            searchCache.clear();
            searchTextField.clear();
            loadItemsByCategory(categoryChoiceBox.getValue());
        });

        sortChoiceBox.getItems().addAll("Mới nhất", "Giá thấp đến cao", "Giá cao đến thấp");
        sortChoiceBox.setValue("Mới nhất");
        sortChoiceBox.setOnAction(event -> sortItems(sortChoiceBox.getValue()));
    }

    private void sortItems(String sortOption) {
        if (itemList.isEmpty()) return;

        Comparator<Item> priceComparator = (item1, item2) -> {
            double price1 = item1.getPrice();
            double price2 = item2.getPrice();
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
        itemPaneCache.clear();

        Task<List<?>> task = new Task<>() {
            @Override
            protected List<?> call() {
                if (category.equals("Thú cưng")) {
                    return Objects.requireNonNull(PetDAO.findAll()).stream()
                            .filter(pet -> !pet.getIsSold())
                            .filter(pet -> !temporarilyRemovedPetIds.contains(pet.getPetId()))
                            .collect(Collectors.toList());
                } else {
                    return ProductDAO.findByCategory(category).stream()
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
                ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi",
                    "Không thể tải danh sách sản phẩm. Vui lòng thử lại sau.");
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void updateGridColumns(int newColumnCount) {
        if (newColumnCount == currentColumnCount) return;

        currentColumnCount = newColumnCount;
        rearrangeGridItems();
    }

    private void rearrangeGridItems() {
        flowPane.getChildren().clear();
        itemPaneCache.clear();

        for (Item item : itemList) {
            try {
                if (item instanceof Product product && product.getStock() <= 0) {
                    continue;
                }

                if (item instanceof Pet pet && temporarilyRemovedPetIds.contains(pet.getPetId())) {
                    continue;
                }

                AnchorPane itemPane = getOrCreateItemPane(item);

                itemPane.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        try {
                            boolean itemExists = isItemInOrder(item);

                            if (!itemExists) {
                                AnchorPane newItemPane = createItemOrderPane(item);
                                newItemPane.setUserData(item.getId());
                                orderVBox.getChildren().add(newItemPane);
                                updateAmount();

                                if (item instanceof Pet) {
                                    flowPane.getChildren().remove(itemPane);
                                    itemPaneCache.remove(item);
                                }
                            }
                        } catch (IOException e) {
                            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi",
                                "Không thể thêm sản phẩm vào đơn hàng.");
                            e.printStackTrace();
                        }
                    }
                });

                if (!flowPane.getChildren().contains(itemPane)) {
                    flowPane.getChildren().add(itemPane);
                }
            } catch (IOException e) {
                ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi",
                    "Không thể tải sản phẩm: " + e.getMessage());
                e.printStackTrace();
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

    private Consumer<AnchorPane> createDeleteCallback() {
        return paneToRemove -> {
            VBox tabContent = orderVBox;
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(200), paneToRemove);
            fadeTransition.setFromValue(1.0);
            fadeTransition.setToValue(0.0);

            fadeTransition.setOnFinished(e -> {
                tabContent.getChildren().remove(paneToRemove);
                updateAmount();

                Object controllerObj = paneToRemove.getProperties().get("controller");
                if (controllerObj instanceof ItemList2Controller controller) {
                    Item item = controller.getItem();
                    String currentCategory = categoryChoiceBox.getValue();

                    if (item instanceof Pet pet) {
                        temporarilyRemovedPetIds.remove(pet.getPetId());

                        if (currentCategory.equals("Thú cưng")) {
                            try {
                                AnchorPane itemPane = getOrCreateItemPane(item);
                                itemPane.setOnMouseClicked(event -> {
                                    if (event.getButton() == MouseButton.PRIMARY) {
                                        try {
                                            boolean itemExists = isItemInOrder(item);

                                            if (!itemExists) {
                                                AnchorPane newItemPane = createItemOrderPane(item);
                                                newItemPane.setUserData(item.getId());
                                                orderVBox.getChildren().add(newItemPane);
                                                updateAmount();

                                                temporarilyRemovedPetIds.add(pet.getPetId());

                                                flowPane.getChildren().remove(itemPane);
                                            }
                                        } catch (IOException ex) {
                                            throw new RuntimeException(ex);
                                        }
                                    }
                                });

                                if (!flowPane.getChildren().contains(itemPane)) {
                                    flowPane.getChildren().add(itemPane);
                                }
                            } catch (IOException ex) {
                                System.err.println("Error adding pet back to display: " + ex.getMessage());
                            }
                        }
                    } else if (item instanceof Product product) {
                        if (product.getCategory().equals(currentCategory)) {
                            try {
                                AnchorPane itemPane = getOrCreateItemPane(product);
                                itemPane.setOnMouseClicked(event -> {
                                    if (event.getButton() == MouseButton.PRIMARY) {
                                        try {
                                            boolean itemExists = isItemInOrder(product);

                                            if (!itemExists) {
                                                AnchorPane newItemPane = createItemOrderPane(product);
                                                newItemPane.setUserData(product.getId());
                                                orderVBox.getChildren().add(newItemPane);
                                                updateAmount();
                                            }
                                        } catch (IOException ex) {
                                            throw new RuntimeException(ex);
                                        }
                                    }
                                });
                                if (!flowPane.getChildren().contains(itemPane)) {
                                    flowPane.getChildren().add(itemPane);
                                }
                            } catch (IOException ex) {
                                System.err.println("Error adding product back to display: " + ex.getMessage());
                            }
                        }
                    }
                }
            });
            fadeTransition.play();
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
            controller.setOnQuantityChanged(this::updateAmount);

            if (item instanceof Pet pet) {
                temporarilyRemovedPetIds.add(pet.getPetId());
            }

            controller.setOnStockChanged(() -> {
                if (item instanceof Product product) {
                    int quantity = controller.getQuantity();
                    int remainingStock = product.getStock() - quantity;

                    if (remainingStock <= 0) {
                        flowPane.getChildren().removeIf(node -> {
                            if (node instanceof AnchorPane itemPane) {
                                Object controllerObj = itemPane.getProperties().get("controller");
                                if (controllerObj instanceof ItemList2Controller itemController) {
                                    return itemController.getItem().getId() == product.getId();
                                }
                            }
                            return false;
                        });
                    }
                }
            });

            pane.setUserData(item.getId());
            pane.getProperties().put("controller", controller);
        }

        return pane;
    }

    private boolean isItemInOrder(Item item) {
        if (item == null) return false;

        for (Node node : orderVBox.getChildren()) {
            if (node instanceof AnchorPane pane) {
                Object controllerObj = pane.getProperties().get("controller");
                if (controllerObj instanceof ItemList2Controller controller) {
                    if (controller.getItem().getId() == item.getId()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void updateAmount() {
        double total = 0;
        for (Node node : orderVBox.getChildren()) {
            if (node instanceof AnchorPane pane) {
                Object controllerObj = pane.getProperties().get("controller");

                if (controllerObj instanceof ItemList2Controller controller) {
                    total += controller.getTotal();
                }
            }
        }

        totalMoneyValueLabel.setText(ControllerUtils.formatCurrency(total));

        double discountAmount = ControllerUtils.parseCurrency(voucherValueLabel.getText());
        double finalAmount = total - discountAmount;

        finalAmount = Math.max(0, finalAmount);

        finalMoneyValueLabel.setText(ControllerUtils.formatCurrency(finalAmount));
    }

    private void updateIncrementalSearchResults() {
        if (incrementalSearchResults.isEmpty()) {
            return;
        }

        itemList.clear();
        itemList.addAll(incrementalSearchResults);

        rearrangeGridItems();
    }

    private void handleSearch() {
        String searchText = searchTextField.getText().trim();

        if (searchText.isEmpty()) {
            loadItemsByCategory(categoryChoiceBox.getValue());
            return;
        }

        itemPaneCache.clear();

        String currentCategory = categoryChoiceBox.getValue();
        String cacheKey = currentCategory + "_" + searchText;

        if (searchCache.containsKey(cacheKey)) {
            itemList.clear();
            itemList.addAll((List<Item>) searchCache.get(cacheKey));
            rearrangeGridItems();
            return;
        }

        if (searchText.length() >= 3) {
            for (int prefixLength = searchText.length() - 1; prefixLength >= 2; prefixLength--) {
                String prefix = searchText.substring(0, prefixLength);
                String prefixCacheKey = currentCategory + "_" + prefix;

                if (searchCache.containsKey(prefixCacheKey)) {
                    List<Item> prefixResults = (List<Item>) searchCache.get(prefixCacheKey);

                    String searchTextLower = searchText.toLowerCase();
                    List<Item> filteredResults = prefixResults.stream()
                            .filter(item -> {
                                if (item instanceof Pet pet) {
                                    if (searchTextLower.length() <= 2) {
                                        if (pet.getName().toLowerCase().startsWith(searchTextLower)) {
                                            return true;
                                        }
                                        return pet.getType().toLowerCase().startsWith(searchTextLower) ||
                                               pet.getBreed().toLowerCase().startsWith(searchTextLower);
                                    } else {
                                        return pet.getName().toLowerCase().contains(searchTextLower) ||
                                               pet.getType().toLowerCase().contains(searchTextLower) ||
                                               pet.getBreed().toLowerCase().contains(searchTextLower) ||
                                               pet.getSex().toLowerCase().contains(searchTextLower) ||
                                               pet.getDescription().toLowerCase().contains(searchTextLower) ||
                                               String.valueOf(pet.getPrice()).contains(searchTextLower) ||
                                               String.valueOf(pet.getAge()).contains(searchTextLower);
                                    }
                                } else if (item instanceof Product product) {
                                    if (searchTextLower.length() <= 2) {
                                        if (product.getName().toLowerCase().startsWith(searchTextLower)) {
                                            return true;
                                        }
                                        return product.getCategory().toLowerCase().startsWith(searchTextLower);
                                    } else {
                                        return product.getName().toLowerCase().contains(searchTextLower) ||
                                               product.getCategory().toLowerCase().contains(searchTextLower) ||
                                               product.getDescription().toLowerCase().contains(searchTextLower) ||
                                               String.valueOf(product.getPrice()).contains(searchTextLower) ||
                                               String.valueOf(product.getStock()).contains(searchTextLower);
                                    }
                                }
                                return false;
                            })
                            .collect(Collectors.toList());

                    searchCache.put(cacheKey, filteredResults);

                    itemList.clear();
                    itemList.addAll(filteredResults);
                    rearrangeGridItems();
                    return;
                }
            }
        }

        if (currentSearchTask != null && currentSearchTask.isRunning()) {
            currentSearchTask.cancel();
        }

        incrementalSearchResults.clear();

        currentSearchTask = new Task<>() {
            @Override
            protected List<?> call() throws Exception {
                String currentCategory = categoryChoiceBox.getValue();
                String searchTextLower = searchText.toLowerCase();

                if (currentCategory.equals("Thú cưng")) {
                    List<Pet> availablePets = Objects.requireNonNull(PetDAO.findAll()).stream()
                            .filter(pet -> !pet.getIsSold())
                            .filter(pet -> !temporarilyRemovedPetIds.contains(pet.getPetId()))
                            .collect(Collectors.toList());

                    List<Pet> results = new ArrayList<>();

                    List<Pet> matchingPets = availablePets.parallelStream()
                            .filter(pet -> {
                                if (searchTextLower.length() <= 2) {
                                    if (pet.getName().toLowerCase().startsWith(searchTextLower)) {
                                        return true;
                                    }
                                    return pet.getType().toLowerCase().startsWith(searchTextLower) ||
                                           pet.getBreed().toLowerCase().startsWith(searchTextLower);
                                } else {
                                    if (pet.getName().toLowerCase().contains(searchTextLower)) {
                                        return true;
                                    }

                                    return pet.getType().toLowerCase().contains(searchTextLower) ||
                                           pet.getBreed().toLowerCase().contains(searchTextLower) ||
                                           pet.getSex().toLowerCase().contains(searchTextLower) ||
                                           pet.getDescription().toLowerCase().contains(searchTextLower) ||
                                           String.valueOf(pet.getPrice()).contains(searchTextLower) ||
                                           String.valueOf(pet.getAge()).contains(searchTextLower);
                                }
                            })
                            .limit(SEARCH_LIMIT)
                            .collect(Collectors.toList());

                    for (int i = 0; i < matchingPets.size(); i++) {
                        Pet pet = matchingPets.get(i);
                        results.add(pet);

                        if (i % INCREMENTAL_SEARCH_BATCH_SIZE == 0 || i == matchingPets.size() - 1) {
                            List<Pet> incrementalBatch = new ArrayList<>(results);

                            Platform.runLater(() -> {
                                if (!isCancelled()) {
                                    incrementalSearchResults.clear();
                                    incrementalSearchResults.addAll(incrementalBatch);

                                    updateIncrementalSearchResults();
                                }
                            });

                            Thread.sleep(10);
                        }
                    }

                    return results;
                } else {
                    List<Product> availableProducts = ProductDAO.findByCategory(currentCategory).stream()
                            .filter(product -> !product.getIsSold())
                            .collect(Collectors.toList());

                    List<Product> results = new ArrayList<>();

                    List<Product> matchingProducts = availableProducts.parallelStream()
                            .filter(product -> {
                                if (searchTextLower.length() <= 2) {
                                    if (product.getName().toLowerCase().startsWith(searchTextLower)) {
                                        return true;
                                    }
                                    return product.getCategory().toLowerCase().startsWith(searchTextLower);
                                } else {
                                    if (product.getName().toLowerCase().contains(searchTextLower)) {
                                        return true;
                                    }

                                    return product.getCategory().toLowerCase().contains(searchTextLower) ||
                                           product.getDescription().toLowerCase().contains(searchTextLower) ||
                                           String.valueOf(product.getPrice()).contains(searchTextLower) ||
                                           String.valueOf(product.getStock()).contains(searchTextLower);
                                }
                            })
                            .limit(SEARCH_LIMIT)
                            .collect(Collectors.toList());

                    for (int i = 0; i < matchingProducts.size(); i++) {
                        Product product = matchingProducts.get(i);
                        results.add(product);

                        if (i % INCREMENTAL_SEARCH_BATCH_SIZE == 0 || i == matchingProducts.size() - 1) {
                            List<Product> incrementalBatch = new ArrayList<>(results);

                            Platform.runLater(() -> {
                                if (!isCancelled()) {
                                    incrementalSearchResults.clear();
                                    incrementalSearchResults.addAll(incrementalBatch);

                                    updateIncrementalSearchResults();
                                }
                            });

                            Thread.sleep(10);
                        }
                    }

                    return results;
                }
            }
        };

        currentSearchTask.setOnSucceeded(event -> {
            List<?> results = currentSearchTask.getValue();
            if (results != null) {
                if (searchCache.size() >= CACHE_SIZE) {
                    int toRemove = CACHE_SIZE / 4;
                    List<String> keys = new ArrayList<>(searchCache.keySet());
                    for (int i = 0; i < Math.min(toRemove, keys.size()); i++) {
                        searchCache.remove(keys.get(i));
                    }
                }

                searchCache.put(cacheKey, results);

                if (searchText.length() <= 5) {
                    for (int prefixLength = 2; prefixLength < searchText.length(); prefixLength++) {
                        String prefix = searchText.substring(0, prefixLength);
                        String prefixCacheKey = currentCategory + "_" + prefix;

                        if (!searchCache.containsKey(prefixCacheKey)) {
                            searchCache.put(prefixCacheKey, results);
                        }
                    }
                }

                itemList.clear();
                itemList.addAll((List<Item>) results);
                rearrangeGridItems();
            }
        });

        currentSearchTask.setOnFailed(event -> {
            currentSearchTask.getException().printStackTrace();
            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi",
                "Không thể tìm kiếm sản phẩm. Vui lòng thử lại sau.");
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
                updateAmount();
            }
        });
    }

    private boolean isValidDiscount(Discount discount) {
        if (discount == null) {
            return false;
        }

        LocalDate today = LocalDate.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        if (today.isBefore(discount.getStartDate())) {
            ControllerUtils.showAlert(Alert.AlertType.WARNING, "Thông báo",
                String.format("Mã giảm giá này sẽ có hiệu lực từ ngày %s",
                discount.getStartDate().format(dateFormatter)));
            return false;
        }

        if (today.isAfter(discount.getEndDate())) {
            ControllerUtils.showAlert(Alert.AlertType.WARNING, "Thông báo",
                String.format("Mã giảm giá này đã hết hạn từ ngày %s",
                discount.getEndDate().format(dateFormatter)));
            return false;
        }

        return true;
    }

    private void handleDiscountCode(String code) {
        Task<Discount> task = new Task<>() {
            @Override
            protected Discount call() {
                try {
                    return DiscountDAO.findByCode(code);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            }
        };

        task.setOnSucceeded(event -> {
            try {
                Discount discount = task.getValue();

                if (discount == null) {
                    voucherValueLabel.setText("0");
                    ControllerUtils.showAlert(Alert.AlertType.WARNING, "Thông báo",
                        "Mã giảm giá không tồn tại!");
                    updateAmount();
                    return;
                }

                double totalAmount = ControllerUtils.parseCurrency(totalMoneyValueLabel.getText());
                String validationMessage = DiscountDAO.validateDiscount(discount, totalAmount);

                if (validationMessage == null) {
                    applyDiscount(discount);
                } else {
                    voucherValueLabel.setText("0");
                    ControllerUtils.showAlert(Alert.AlertType.WARNING, "Thông báo", validationMessage);
                }
                updateAmount();
            } catch (Exception e) {
                e.printStackTrace();
                voucherValueLabel.setText("0");
                ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi",
                    "Có lỗi xảy ra khi xử lý mã giảm giá!");
                updateAmount();
            }
        });

        task.setOnFailed(event -> {
            Throwable exception = task.getException();
            if (exception != null) {
                exception.printStackTrace();
            }
            voucherValueLabel.setText("0");
            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi",
                "Không thể kiểm tra mã giảm giá! Vui lòng thử lại sau.");
            updateAmount();
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

        discountAmount = Math.min(discountAmount, totalAmount);

        voucherValueLabel.setText(ControllerUtils.formatCurrency(discountAmount));
    }

    @FXML
    private void handleConfirmOrder() {
        if (orderVBox.getChildren().isEmpty()) {
            ControllerUtils.showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn ít nhất một sản phẩm!");
            return;
        }

        ArrayList<OrderDetail> orderDetails = new ArrayList<>();
        Map<Integer, Product> products = new HashMap<>();
        Map<Integer, Pet> pets = new HashMap<>();
        double totalAmount = 0;

        for (Node node : orderVBox.getChildren()) {
            if (node instanceof AnchorPane pane) {
                Object controllerObj = pane.getProperties().get("controller");
                if (controllerObj instanceof ItemList2Controller controller) {
                    Item item = controller.getItem();
                    int quantity = controller.getQuantity();
                    if (quantity > 0) {
                        OrderDetail detail = new OrderDetail();
                        detail.setItemId(item.getId());
                        detail.setQuantity(quantity);
                        detail.setUnitPrice(item.getPrice());
                        if (item instanceof Product product) {
                            detail.setItemType("product");
                            products.put(product.getProductId(), product);
                        } else if (item instanceof Pet pet) {
                            detail.setItemType("pet");
                            pets.put(pet.getPetId(), pet);
                        }
                        orderDetails.add(detail);
                        totalAmount += item.getPrice() * quantity;
                    }
                }
            }
        }

        if (orderDetails.isEmpty()) {
            ControllerUtils.showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn ít nhất một sản phẩm!");
            return;
        }

        Order order = new Order();
        order.setTotalPrice(totalAmount);
        order.setOrderDate(LocalDateTime.now());
        order.setStaffId(SessionManager.getCurrentStaff().getStaffId());

        final Discount discount;
        String voucherCode = voucherTextField.getText().trim();
        if (!voucherCode.isEmpty()) {
            discount = DiscountDAO.findByCode(voucherCode);
        } else {
            discount = null;
        }

        SessionManager.setCurrentOrder(order);
        SessionManager.setCurrentOrderDetails(orderDetails);
        SessionManager.setCurrentOrderProducts(products);
        SessionManager.setCurrentOrderPets(pets);
        SessionManager.setCurrentDiscount(discount);

        temporarilyRemovedPetIds.clear();

        Stage currentStage = (Stage) root.getScene().getWindow();
        ViewFactory.getInstance().switchContent("payment", currentStage);
    }

    private void loadOrderFromSession() {
        Order order = SessionManager.getCurrentOrder();
        ArrayList<OrderDetail> orderDetails = SessionManager.getCurrentOrderDetails();
        Map<Integer, Product> products = SessionManager.getCurrentOrderProducts();
        Map<Integer, Pet> pets = SessionManager.getCurrentOrderPets();
        Discount discount = SessionManager.getCurrentDiscount();

        if (order != null && orderDetails != null && products != null && pets != null) {
            VBox tabContent = orderVBox;
            if (tabContent != null) {
                tabContent.getChildren().clear();
                for (OrderDetail detail : orderDetails) {
                    Item item = null;
                    if ("product".equals(detail.getItemType())) {
                        item = products.get(detail.getItemId());
                    } else if ("pet".equals(detail.getItemType())) {
                        item = pets.get(detail.getItemId());
                    }
                    if (item != null) {
                        try {
                            AnchorPane itemPane = createItemOrderPane(item);
                            Object controllerObj = itemPane.getProperties().get("controller");
                            if (controllerObj instanceof ItemList2Controller controller) {
                                controller.addItem(-1);
                                controller.addItem(detail.getQuantity());
                            }
                            itemPane.setUserData(item.getId());
                            tabContent.getChildren().add(itemPane);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            if (discount != null) {
                voucherTextField.setText(discount.getCode());
                double totalAmount = order.getTotalPrice();
                double discountAmount = 0;
                if ("percent".equals(discount.getDiscountType())) {
                    discountAmount = totalAmount * (discount.getValue() / 100.0);
                    if (discount.getMaxDiscountValue() > 0) {
                        discountAmount = Math.min(discountAmount, discount.getMaxDiscountValue());
                    }
                } else {
                    discountAmount = discount.getValue();
                }
                voucherValueLabel.setText(ControllerUtils.formatCurrency(discountAmount));
            }
            updateAmount();
        }
    }
}

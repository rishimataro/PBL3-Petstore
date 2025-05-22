package com.store.app.petstore.Controllers.Staff;

// Data Access Objects
import com.store.app.petstore.DAO.PetDAO;
import com.store.app.petstore.DAO.ProductDAO;
import com.store.app.petstore.DAO.DiscountDAO;

// Entity Models
import com.store.app.petstore.Models.Entities.Item;
import com.store.app.petstore.Models.Entities.Pet;
import com.store.app.petstore.Models.Entities.Product;
import com.store.app.petstore.Models.Entities.Discount;
import com.store.app.petstore.Models.Entities.Order;
import com.store.app.petstore.Models.Entities.OrderDetail;

// Utilities and Services
import com.store.app.petstore.Controllers.ControllerUtils;
import com.store.app.petstore.Views.ViewFactory;
import com.store.app.petstore.Sessions.SessionManager;

// JavaFX Components
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

// Java Standard Libraries
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Controller for the Order management interface in the staff section.
 * This controller handles:
 * 1. Displaying available items (pets and products) for selection
 * 2. Adding items to the current order
 * 3. Managing item quantities and order details
 * 4. Applying discounts
 * 5. Calculating order totals
 * 6. Processing order confirmation
 */
public class OrderController implements Initializable {
    // Constants
    private static final int SEARCH_LIMIT = 50;
    private static final int CACHE_SIZE = 100;
    private static final int DEBOUNCE_DELAY_MS = 100; // Further reduced for more responsive search
    private static final int INCREMENTAL_SEARCH_BATCH_SIZE = 10; // Number of items to show in incremental search
    private static final String DEFAULT_CATEGORY = "Thú cưng";
    private static final double SPACING = 10.0;

    // Services
    private final SessionManager sessionManager = new SessionManager();

    // Root container
    @FXML private AnchorPane root;

    // Item display components
    @FXML private ScrollPane scrollPane;
    private final FlowPane flowPane = new FlowPane();
    private int currentColumnCount = 0;
    private final List<Item> itemList = new ArrayList<>();
    private final Map<Item, AnchorPane> itemPaneCache = new HashMap<>();
    private final Set<Integer> temporarilyRemovedPetIds = new HashSet<>();

    // Filter and search components
    @FXML private ChoiceBox<String> categoryChoiceBox;
    @FXML private ChoiceBox<String> sortChoiceBox;
    @FXML private TextField searchTextField;
    @FXML private FontAwesomeIconView searchIcon;
    @FXML private FontAwesomeIconView clearSearchIcon;
    private final PauseTransition searchDebouncer = new PauseTransition(Duration.millis(DEBOUNCE_DELAY_MS));
    private Task<List<?>> currentSearchTask = null;
    private final Map<String, List<?>> searchCache = new HashMap<>();
    private final PauseTransition resizeDebouncer = new PauseTransition(Duration.millis(DEBOUNCE_DELAY_MS));
    private final List<Item> incrementalSearchResults = new ArrayList<>(); // For storing incremental search results

    // Order components
    @FXML private AnchorPane orderContentPane;
    @FXML private ScrollPane orderScrollPane;
    private final VBox orderVBox = new VBox(SPACING);

    // Order summary components
    @FXML private Label totalMoneyLabel;
    @FXML private Label totalMoneyValueLabel;
    @FXML private Label voucherLabel;
    @FXML private TextField voucherTextField;
    @FXML private Label voucherValueLabel;
    @FXML private Label finalMoneyLabel;
    @FXML private Label finalMoneyValueLabel;

    // Action components
    @FXML private Button confirmButton;

    /**
     * Initializes the controller after FXML loading.
     * Sets up UI components, loads initial data, and configures event handlers.
     *
     * @param url The location used to resolve relative paths for the root object
     * @param resourceBundle The resources used to localize the root object
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up UI components
        setupItemDisplayArea();
        setupOrderDisplayArea();
        setupFilterComponents();
        setupSearchComponents();
        setupDiscountHandling();
        setupButtonActions();

        // Make sure the search text field is properly initialized
        Platform.runLater(() -> {
            // Ensure the search text field is clickable
            searchTextField.setEditable(true);
            searchTextField.setFocusTraversable(true);
            searchTextField.setMouseTransparent(false);

            // Make sure the search text field's parent is not blocking mouse events
            Parent parent = searchTextField.getParent();
            while (parent != null) {
                parent.setPickOnBounds(true);
                parent.setMouseTransparent(false);
                parent = parent.getParent();
            }
        });

        // Load initial data
        loadItemsByCategory(DEFAULT_CATEGORY);
        loadOrderFromSession();
    }

    /**
     * Sets up the item display area including the grid layout and scroll pane
     */
    private void setupItemDisplayArea() {
        setupGridLayout();
        configureScrollPane();
    }

    /**
     * Sets up the order display area where selected items are shown
     */
    private void setupOrderDisplayArea() {
        orderVBox.setAlignment(Pos.TOP_CENTER);
        orderVBox.getStyleClass().add("tab-content");
        orderVBox.setPadding(new Insets(10, 3, 10, 20));
        orderScrollPane.setContent(orderVBox);
    }

    /**
     * Sets up filter components including category and sort choice boxes
     */
    private void setupFilterComponents() {
        setupChoiceBox();
    }

    /**
     * Sets up search components and their event handlers
     */
    private void setupSearchComponents() {
        setupClearSearchIcon();

        // Set up search text field with adaptive debouncing
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            clearSearchIcon.setVisible(!newValue.isEmpty());

            // Adjust debounce delay based on search text length
            // Shorter delay for shorter queries for more responsive feedback
            if (newValue.length() <= 2) {
                searchDebouncer.setDuration(Duration.millis(50)); // Very short delay for 1-2 characters
            } else if (newValue.length() <= 4) {
                searchDebouncer.setDuration(Duration.millis(100)); // Short delay for 3-4 characters
            } else {
                searchDebouncer.setDuration(Duration.millis(150)); // Normal delay for longer queries
            }

            // Cancel any running search task immediately when text changes
            if (currentSearchTask != null && currentSearchTask.isRunning()) {
                currentSearchTask.cancel();
            }

            // Always trigger search, even for single character
            searchDebouncer.playFromStart();

            // For very short queries (1-2 chars), start search immediately for instant feedback
            if (newValue.length() <= 2 && !newValue.isEmpty()) {
                handleSearch();
            }
        });

        // We'll handle focus in the setupClearSearchIcon method

        searchDebouncer.setOnFinished(event -> handleSearch());
    }

    /**
     * Sets up action buttons and their event handlers
     */
    private void setupButtonActions() {
        confirmButton.setOnAction(event -> handleConfirmOrder());

        // Add a listener to the scene to clear temporarily removed pets when navigating away
        root.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (oldScene != null && newScene == null) {
                // Scene is being destroyed, clear the temporarily removed pets
                temporarilyRemovedPetIds.clear();
            }
        });
    }

    /**
     * Validates the order and navigates to the payment screen
     * Shows a warning if the order is empty
     */
    private void setupConfirm() {
        if (orderVBox.getChildren().isEmpty()) {
            ControllerUtils.showAlert(Alert.AlertType.WARNING, "Thông báo",
                "Vui lòng tạo đơn hàng trước khi xác nhận!");
            return;
        }
        Stage currentStage = (Stage) root.getScene().getWindow();
        ViewFactory.getInstance().switchContent("payment", currentStage);
    }

    /**
     * Sets up the clear search icon and its click handler
     */
    private void setupClearSearchIcon() {
        clearSearchIcon.setVisible(false);

        // Make the search text field clickable
        searchTextField.setPickOnBounds(true);

        // Add a click handler to the search text field
        searchTextField.setOnMouseClicked(event -> {
            // Check if the click is near the clear icon (right side of the text field)
            double textFieldWidth = searchTextField.getWidth();
            if (event.getX() > textFieldWidth - 30 && !searchTextField.getText().isEmpty()) {
                // Clear the search text
                searchTextField.clear();
                searchTextField.requestFocus();
                clearSearchIcon.setVisible(false);
                // Reset to default category items when search is cleared
                loadItemsByCategory(categoryChoiceBox.getValue());
                event.consume(); // Prevent the text field from getting focus
            } else {
                // Ensure the text field gets focus for normal clicks
                searchTextField.requestFocus();
            }
        });

        // Also keep the click handler for the icon itself
        clearSearchIcon.setOnMouseClicked(event -> {
            searchTextField.clear();
            searchTextField.requestFocus();
            clearSearchIcon.setVisible(false);
            // Reset to default category items when search is cleared
            loadItemsByCategory(categoryChoiceBox.getValue());
            event.consume(); // Prevent event from propagating
        });

        // Make sure the clear icon is above the text field and clickable
        clearSearchIcon.toFront();
        clearSearchIcon.setMouseTransparent(false);
    }

    /**
     * Sets up category and sort choice boxes with their options and event handlers
     */
    private void setupChoiceBox() {
        // Set up category choice box
        categoryChoiceBox.getItems().addAll("Thú cưng", "Phụ kiện", "Đồ chơi", "Thức ăn");
        categoryChoiceBox.setValue(DEFAULT_CATEGORY);
        categoryChoiceBox.setOnAction(event -> {
            // Clear search cache when changing categories
            searchCache.clear();
            // Clear search text
            searchTextField.clear();
            // Load items for the selected category
            loadItemsByCategory(categoryChoiceBox.getValue());
        });

        // Set up sort choice box
        sortChoiceBox.getItems().addAll("Mới nhất", "Giá thấp đến cao", "Giá cao đến thấp");
        sortChoiceBox.setValue("Mới nhất");
        sortChoiceBox.setOnAction(event -> sortItems(sortChoiceBox.getValue()));
    }

    /**
     * Sorts the items in the item list based on the selected sort option
     *
     * @param sortOption The sort option ("Mới nhất", "Giá thấp đến cao", or "Giá cao đến thấp")
     */
    private void sortItems(String sortOption) {
        if (itemList.isEmpty()) return;

        // Create a comparator for sorting by price
        Comparator<Item> priceComparator = (item1, item2) -> {
            double price1 = item1.getPrice();
            double price2 = item2.getPrice();
            return Double.compare(price1, price2);
        };

        // Sort based on the selected option
        switch (sortOption) {
            case "Giá thấp đến cao":
                itemList.sort(priceComparator);
                break;
            case "Giá cao đến thấp":
                itemList.sort(priceComparator.reversed());
                break;
            case "Mới nhất":
                // Default order, no sorting needed
                break;
        }

        // Update the UI with the sorted items
        rearrangeGridItems();
    }

    /**
     * Sets up the grid layout for displaying items
     */
    private void setupGridLayout() {
        // Configure spacing and padding
        flowPane.setHgap(12);
        flowPane.setVgap(15);
        flowPane.setPadding(new Insets(2));
        flowPane.setPrefWrapLength(0);
    }

    /**
     * Configures the scroll pane that contains the item grid
     */
    private void configureScrollPane() {
        // Set content and sizing behavior
        scrollPane.setContent(flowPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefHeight(Region.USE_COMPUTED_SIZE);

        // Configure scrollbar behavior
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPannable(true);
    }

    /**
     * Loads items by category in a background thread
     *
     * @param category The category to load ("Thú cưng", "Thức ăn", "Phụ kiện", etc.)
     */
    private void loadItemsByCategory(String category) {
        // Clear the cache when loading new items to prevent duplicates
        itemPaneCache.clear();

        Task<List<?>> task = new Task<>() {
            @Override
            protected List<?> call() {
                // Load pets or products based on the selected category
                if (category.equals("Thú cưng")) {
                    return PetDAO.getInstance().findAll().stream()
                            .filter(pet -> !pet.getIsSold())
                            // Filter out pets that are temporarily removed
                            .filter(pet -> !temporarilyRemovedPetIds.contains(pet.getPetId()))
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
                ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi",
                    "Không thể tải danh sách sản phẩm. Vui lòng thử lại sau.");
            }
        };

        // Start the task in a background thread
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }




    /**
     * Updates the number of columns in the grid and rearranges items if needed
     *
     * @param newColumnCount The new number of columns
     */
    private void updateGridColumns(int newColumnCount) {
        // Only rearrange if the column count has changed
        if (newColumnCount == currentColumnCount) return;

        currentColumnCount = newColumnCount;
        rearrangeGridItems();
    }

    /**
     * Rearranges the items in the grid
     * Clears the current grid and recreates it with the current items
     */
    private void rearrangeGridItems() {
        // Clear the current grid and cache
        flowPane.getChildren().clear();
        itemPaneCache.clear();

        // Add each item to the grid
        for (Item item : itemList) {
            try {
                // Skip out-of-stock products
                if (item instanceof Product product && product.getStock() <= 0) {
                    continue;
                }

                // Skip temporarily removed pets
                if (item instanceof Pet pet && temporarilyRemovedPetIds.contains(pet.getPetId())) {
                    continue;
                }

                // Create or get the item pane
                AnchorPane itemPane = getOrCreateItemPane(item);

                // Set up the click handler
                itemPane.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        try {
                            // Check if the item is already in the order
                            boolean itemExists = isItemInOrder(item);

                            if (!itemExists) {
                                // Only add to order if not already there
                                AnchorPane newItemPane = createItemOrderPane(item);
                                newItemPane.setUserData(item.getId());
                                orderVBox.getChildren().add(newItemPane);
                                updateAmount();

                                // If it's a pet, remove it from display immediately
                                if (item instanceof Pet) {
                                    flowPane.getChildren().remove(itemPane);
                                    // Also remove from cache to prevent reuse
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

                // Only add if not already present
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

    /**
     * Gets an existing item pane from the cache or creates a new one
     *
     * @param item The item to get or create a pane for
     * @return The AnchorPane for the item
     * @throws IOException If there is an error loading the FXML
     */
    private AnchorPane getOrCreateItemPane(Item item) throws IOException {
        // Check if the item is already in the cache
        if (itemPaneCache.containsKey(item)) {
            return itemPaneCache.get(item);
        }

        // Create a new pane and add it to the cache
        AnchorPane pane = createItemPane(item);
        itemPaneCache.put(item, pane);
        return pane;
    }

    /**
     * Creates a new item pane for the given item
     *
     * @param item The item to create a pane for
     * @return The AnchorPane for the item
     * @throws IOException If there is an error loading the FXML
     */
    private AnchorPane createItemPane(Item item) throws IOException {
        // Load the FXML for the item pane
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Staff/ItemList.fxml"));
        AnchorPane pane = loader.load();

        // Set the item data in the controller
        ItemListController controller = loader.getController();
        controller.setData(item);

        return pane;
    }

    /**
     * Creates a callback for deleting items from the order
     *
     * @return A Consumer that handles removing an item from the order
     */
    private Consumer<AnchorPane> createDeleteCallback() {
        return paneToRemove -> {
            VBox tabContent = orderVBox;
            if (tabContent != null) {
                // Create a fade out animation
                FadeTransition fadeTransition = new FadeTransition(Duration.millis(200), paneToRemove);
                fadeTransition.setFromValue(1.0);
                fadeTransition.setToValue(0.0);

                // When the animation finishes, remove the item and update the UI
                fadeTransition.setOnFinished(e -> {
                    // Remove the item from the order
                    tabContent.getChildren().remove(paneToRemove);
                    updateAmount();

                    // Add the item back to the display if it matches the current category
                    Object controllerObj = paneToRemove.getProperties().get("controller");
                    if (controllerObj instanceof ItemList2Controller controller) {
                        Item item = controller.getItem();
                        String currentCategory = categoryChoiceBox.getValue();

                        if (item instanceof Pet pet) {
                            // Remove the pet ID from the temporarily removed set
                            temporarilyRemovedPetIds.remove(pet.getPetId());

                            // Only add back to display if current category is "Thú cưng"
                            if (currentCategory.equals("Thú cưng")) {
                                try {
                                    AnchorPane itemPane = getOrCreateItemPane(item);
                                    // Set up the click handler for the pet item
                                    itemPane.setOnMouseClicked(event -> {
                                        if (event.getButton() == MouseButton.PRIMARY) {
                                            try {
                                                // Check if the item is already in the order
                                                boolean itemExists = isItemInOrder(item);

                                                if (!itemExists) {
                                                    // Only add to order if not already there
                                                    AnchorPane newItemPane = createItemOrderPane(item);
                                                    newItemPane.setUserData(item.getId());
                                                    orderVBox.getChildren().add(newItemPane);
                                                    updateAmount();

                                                    // Add to temporarily removed set
                                                    temporarilyRemovedPetIds.add(pet.getPetId());

                                                    // Remove from display
                                                    flowPane.getChildren().remove(itemPane);
                                                }
                                            } catch (IOException ex) {
                                                throw new RuntimeException(ex);
                                            }
                                        }
                                    });
                                    // Only add if not already present
                                    if (!flowPane.getChildren().contains(itemPane)) {
                                        flowPane.getChildren().add(itemPane);
                                    }
                                } catch (IOException ex) {
                                    System.err.println("Error adding pet back to display: " + ex.getMessage());
                                }
                            }
                        } else if (item instanceof Product product) {
                            // Only add product back if it matches the current category
                            if (product.getCategory().equals(currentCategory)) {
                                try {
                                    AnchorPane itemPane = getOrCreateItemPane(product);
                                    // Set up the click handler for the product item
                                    itemPane.setOnMouseClicked(event -> {
                                        if (event.getButton() == MouseButton.PRIMARY) {
                                            try {
                                                // Check if the item is already in the order
                                                boolean itemExists = isItemInOrder(product);

                                                if (!itemExists) {
                                                    // Only add to order if not already there
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
                                    // Only add if not already present
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
            }
        };
    }

    /**
     * Creates a pane for an item in the order
     *
     * @param item The item to create a pane for
     * @return The AnchorPane for the item in the order
     * @throws IOException If there is an error loading the FXML
     */
    private AnchorPane createItemOrderPane(Item item) throws IOException {
        // Load the FXML for the order item pane
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Staff/ItemList2.fxml"));
        AnchorPane pane = loader.load();
        ItemList2Controller controller = loader.getController();

        if (controller != null) {
            // Set up the controller
            controller.setParentPane(pane);
            controller.setOnDeleteCallback(createDeleteCallback());
            controller.setData(item);
            controller.setOnQuantityChanged(this::updateAmount);

            // If it's a pet, add it to the temporarily removed set
            if (item instanceof Pet pet) {
                temporarilyRemovedPetIds.add(pet.getPetId());
            }

            // Handle stock changes for products
            controller.setOnStockChanged(() -> {
                if (item instanceof Product product) {
                    int quantity = controller.getQuantity();
                    int remainingStock = product.getStock() - quantity;

                    // Remove product from display if out of stock
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

            // Store references for later use
            pane.setUserData(item.getId());
            pane.getProperties().put("controller", controller);
        }

        return pane;
    }

    /**
     * Checks if an item is already in the order
     *
     * @param item The item to check
     * @return true if the item is already in the order, false otherwise
     */
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

    /**
     * Updates the total amount and final amount in the order summary
     * Calculates the total from all items in the order and applies any discount
     */
    private void updateAmount() {
        // Calculate the total from all items in the order
        double total = 0;
        for (Node node : orderVBox.getChildren()) {
            if (node instanceof AnchorPane pane) {
                Object controllerObj = pane.getProperties().get("controller");

                if (controllerObj instanceof ItemList2Controller controller) {
                    total += controller.getTotal();
                }
            }
        }

        // Update the total amount display
        totalMoneyValueLabel.setText(ControllerUtils.formatCurrency(total));

        // Calculate the final amount after applying any discount
        double discountAmount = ControllerUtils.parseCurrency(voucherValueLabel.getText());
        double finalAmount = total - discountAmount;

        // Ensure final amount is not negative
        finalAmount = Math.max(0, finalAmount);

        // Update the final amount display
        finalMoneyValueLabel.setText(ControllerUtils.formatCurrency(finalAmount));
    }

    /**
     * Updates the UI with incremental search results
     * This method is called during the search process to show partial results
     */
    private void updateIncrementalSearchResults() {
        // Only update if we have results
        if (incrementalSearchResults.isEmpty()) {
            return;
        }

        // Update the item list with current incremental results
        itemList.clear();
        itemList.addAll(incrementalSearchResults);

        // Update the UI
        rearrangeGridItems();
    }

    /**
     * Handles the search functionality
     * Searches for items matching the search text in the selected category
     */
    private void handleSearch() {
        String searchText = searchTextField.getText().trim();

        // If search text is empty, load all items in the category
        if (searchText.isEmpty()) {
            loadItemsByCategory(categoryChoiceBox.getValue());
            return;
        }

        // Clear the item pane cache to prevent duplicates
        itemPaneCache.clear();

        // Create a cache key that includes the category and search text
        String currentCategory = categoryChoiceBox.getValue();
        String cacheKey = currentCategory + "_" + searchText;

        // Check for exact cache match first
        if (searchCache.containsKey(cacheKey)) {
            // Use cached results for exact match
            itemList.clear();
            itemList.addAll((List<Item>) searchCache.get(cacheKey));
            rearrangeGridItems();
            return;
        }

        // For longer searches, check if we have a prefix cache that we can filter
        if (searchText.length() >= 3) {
            // Look for a cached prefix that we can filter
            for (int prefixLength = searchText.length() - 1; prefixLength >= 2; prefixLength--) {
                String prefix = searchText.substring(0, prefixLength);
                String prefixCacheKey = currentCategory + "_" + prefix;

                if (searchCache.containsKey(prefixCacheKey)) {
                    // Get the cached results for the prefix
                    List<Item> prefixResults = (List<Item>) searchCache.get(prefixCacheKey);

                    // Filter the prefix results for the current search text
                    String searchTextLower = searchText.toLowerCase();
                    List<Item> filteredResults = prefixResults.stream()
                            .filter(item -> {
                                if (item instanceof Pet pet) {
                                    // For short queries (1-2 chars), prioritize exact matches at the beginning of names
                                    if (searchTextLower.length() <= 2) {
                                        // First check if name starts with the search text (highest priority)
                                        if (pet.getName().toLowerCase().startsWith(searchTextLower)) {
                                            return true;
                                        }
                                        // Then check if type or breed starts with the search text
                                        return pet.getType().toLowerCase().startsWith(searchTextLower) ||
                                               pet.getBreed().toLowerCase().startsWith(searchTextLower);
                                    } else {
                                        // For longer queries, use the original broader search
                                        return pet.getName().toLowerCase().contains(searchTextLower) ||
                                               pet.getType().toLowerCase().contains(searchTextLower) ||
                                               pet.getBreed().toLowerCase().contains(searchTextLower) ||
                                               pet.getSex().toLowerCase().contains(searchTextLower) ||
                                               pet.getDescription().toLowerCase().contains(searchTextLower) ||
                                               String.valueOf(pet.getPrice()).contains(searchTextLower) ||
                                               String.valueOf(pet.getAge()).contains(searchTextLower);
                                    }
                                } else if (item instanceof Product product) {
                                    // For short queries (1-2 chars), prioritize exact matches at the beginning of names
                                    if (searchTextLower.length() <= 2) {
                                        // First check if name starts with the search text (highest priority)
                                        if (product.getName().toLowerCase().startsWith(searchTextLower)) {
                                            return true;
                                        }
                                        // Then check if category starts with the search text
                                        return product.getCategory().toLowerCase().startsWith(searchTextLower);
                                    } else {
                                        // For longer queries, use the original broader search
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

                    // Cache the filtered results
                    searchCache.put(cacheKey, filteredResults);

                    // Update the UI
                    itemList.clear();
                    itemList.addAll(filteredResults);
                    rearrangeGridItems();
                    return;
                }
            }
        }

        // Cancel any running search task
        if (currentSearchTask != null && currentSearchTask.isRunning()) {
            currentSearchTask.cancel();
        }

        // Clear incremental search results
        incrementalSearchResults.clear();

        // Create a new search task with support for incremental results
        currentSearchTask = new Task<>() {
            @Override
            protected List<?> call() throws Exception {
                // Search for items based on the selected category
                String currentCategory = categoryChoiceBox.getValue();
                String searchTextLower = searchText.toLowerCase();

                // Use parallel streams for faster processing
                if (currentCategory.equals("Thú cưng")) {
                    // Pre-filter pets that are not sold and not temporarily removed
                    List<Pet> availablePets = PetDAO.getInstance().findAll().stream()
                            .filter(pet -> !pet.getIsSold())
                            .filter(pet -> !temporarilyRemovedPetIds.contains(pet.getPetId()))
                            .collect(Collectors.toList());

                    // Create a list to collect results
                    List<Pet> results = new ArrayList<>();

                    // Process pets in batches for incremental updates
                    List<Pet> matchingPets = availablePets.parallelStream()
                            // Optimize search by checking name first (most common search)
                            .filter(pet -> {
                                // For short queries (1-2 chars), prioritize exact matches at the beginning of names
                                if (searchTextLower.length() <= 2) {
                                    // First check if name starts with the search text (highest priority)
                                    if (pet.getName().toLowerCase().startsWith(searchTextLower)) {
                                        return true;
                                    }
                                    // Then check if type or breed starts with the search text
                                    return pet.getType().toLowerCase().startsWith(searchTextLower) ||
                                           pet.getBreed().toLowerCase().startsWith(searchTextLower);
                                } else {
                                    // For longer queries, check name first as it's most common
                                    if (pet.getName().toLowerCase().contains(searchTextLower)) {
                                        return true;
                                    }

                                    // If name doesn't match, check other fields
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

                    // Process results in batches for incremental updates
                    for (int i = 0; i < matchingPets.size(); i++) {
                        Pet pet = matchingPets.get(i);
                        results.add(pet);

                        // Update UI incrementally after each batch
                        if (i % INCREMENTAL_SEARCH_BATCH_SIZE == 0 || i == matchingPets.size() - 1) {
                            // Create a copy of current results to avoid concurrent modification
                            List<Pet> incrementalBatch = new ArrayList<>(results);

                            // Update UI on JavaFX thread
                            Platform.runLater(() -> {
                                if (!isCancelled()) {
                                    // Add to incremental results
                                    incrementalSearchResults.clear();
                                    incrementalSearchResults.addAll(incrementalBatch);

                                    // Update UI with current results
                                    updateIncrementalSearchResults();
                                }
                            });

                            // Small delay to allow UI to update
                            Thread.sleep(10);
                        }
                    }

                    return results;
                } else {
                    // Pre-filter products that are not sold
                    List<Product> availableProducts = ProductDAO.getInstance().findByCategory(currentCategory).stream()
                            .filter(product -> !product.getIsSold())
                            .collect(Collectors.toList());

                    // Create a list to collect results
                    List<Product> results = new ArrayList<>();

                    // Process products in batches for incremental updates
                    List<Product> matchingProducts = availableProducts.parallelStream()
                            // Optimize search by checking name first (most common search)
                            .filter(product -> {
                                // For short queries (1-2 chars), prioritize exact matches at the beginning of names
                                if (searchTextLower.length() <= 2) {
                                    // First check if name starts with the search text (highest priority)
                                    if (product.getName().toLowerCase().startsWith(searchTextLower)) {
                                        return true;
                                    }
                                    // Then check if category starts with the search text
                                    return product.getCategory().toLowerCase().startsWith(searchTextLower);
                                } else {
                                    // For longer queries, check name first as it's most common
                                    if (product.getName().toLowerCase().contains(searchTextLower)) {
                                        return true;
                                    }

                                    // If name doesn't match, check other fields
                                    return product.getCategory().toLowerCase().contains(searchTextLower) ||
                                           product.getDescription().toLowerCase().contains(searchTextLower) ||
                                           String.valueOf(product.getPrice()).contains(searchTextLower) ||
                                           String.valueOf(product.getStock()).contains(searchTextLower);
                                }
                            })
                            .limit(SEARCH_LIMIT)
                            .collect(Collectors.toList());

                    // Process results in batches for incremental updates
                    for (int i = 0; i < matchingProducts.size(); i++) {
                        Product product = matchingProducts.get(i);
                        results.add(product);

                        // Update UI incrementally after each batch
                        if (i % INCREMENTAL_SEARCH_BATCH_SIZE == 0 || i == matchingProducts.size() - 1) {
                            // Create a copy of current results to avoid concurrent modification
                            List<Product> incrementalBatch = new ArrayList<>(results);

                            // Update UI on JavaFX thread
                            Platform.runLater(() -> {
                                if (!isCancelled()) {
                                    // Add to incremental results
                                    incrementalSearchResults.clear();
                                    incrementalSearchResults.addAll(incrementalBatch);

                                    // Update UI with current results
                                    updateIncrementalSearchResults();
                                }
                            });

                            // Small delay to allow UI to update
                            Thread.sleep(10);
                        }
                    }

                    return results;
                }
            }
        };

        // Handle successful search
        currentSearchTask.setOnSucceeded(event -> {
            List<?> results = currentSearchTask.getValue();
            if (results != null) {
                // Cache management strategy
                // Always cache results for better performance
                if (searchCache.size() >= CACHE_SIZE) {
                    // If cache is full, remove oldest entries
                    // This is a simple approach - a more sophisticated LRU cache could be implemented
                    int toRemove = CACHE_SIZE / 4; // Remove 25% of the cache
                    List<String> keys = new ArrayList<>(searchCache.keySet());
                    for (int i = 0; i < Math.min(toRemove, keys.size()); i++) {
                        searchCache.remove(keys.get(i));
                    }
                }

                // Cache the results
                searchCache.put(cacheKey, results);

                // For short queries, also cache prefixes for faster future searches
                if (searchText.length() <= 5) {
                    for (int prefixLength = 2; prefixLength < searchText.length(); prefixLength++) {
                        String prefix = searchText.substring(0, prefixLength);
                        String prefixCacheKey = currentCategory + "_" + prefix;

                        // Only cache if not already present
                        if (!searchCache.containsKey(prefixCacheKey)) {
                            searchCache.put(prefixCacheKey, results);
                        }
                    }
                }

                // Update the UI with search results
                itemList.clear();
                itemList.addAll((List<Item>) results);
                rearrangeGridItems();
            }
        });

        // Handle search failure
        currentSearchTask.setOnFailed(event -> {
            currentSearchTask.getException().printStackTrace();
            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi",
                "Không thể tìm kiếm sản phẩm. Vui lòng thử lại sau.");
        });

        // Start the search in a background thread
        Thread searchThread = new Thread(currentSearchTask);
        searchThread.setDaemon(true);
        searchThread.start();
    }

    /**
     * Sets up discount handling including voucher text field and its event handlers
     */
    private void setupDiscountHandling() {
        // Apply discount when voucher code is entered
        voucherTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() >= 3) {
                handleDiscountCode(newValue);
            } else {
                voucherValueLabel.setText("0");
                updateAmount();
            }
        });
    }

    /**
     * Validates if a discount is currently valid based on its date range
     *
     * @param discount The discount to validate
     * @return true if the discount is valid, false otherwise
     */
    private boolean isValidDiscount(Discount discount) {
        if (discount == null) {
            return false;
        }

        LocalDate today = LocalDate.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Check if the discount has started yet
        if (today.isBefore(discount.getStartDate())) {
            ControllerUtils.showAlert(Alert.AlertType.WARNING, "Thông báo",
                String.format("Mã giảm giá này sẽ có hiệu lực từ ngày %s",
                discount.getStartDate().format(dateFormatter)));
            return false;
        }

        // Check if the discount has expired
        if (today.isAfter(discount.getEndDate())) {
            ControllerUtils.showAlert(Alert.AlertType.WARNING, "Thông báo",
                String.format("Mã giảm giá này đã hết hạn từ ngày %s",
                discount.getEndDate().format(dateFormatter)));
            return false;
        }

        return true;
    }

    /**
     * Handles the application of a discount code
     * Validates and applies the discount if valid
     *
     * @param code The discount code to apply
     */
    private void handleDiscountCode(String code) {
        // Create a task to find the discount in the database
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

        // Handle successful discount lookup
        task.setOnSucceeded(event -> {
            try {
                Discount discount = task.getValue();

                // Check if discount exists
                if (discount == null) {
                    voucherValueLabel.setText("0");
                    ControllerUtils.showAlert(Alert.AlertType.WARNING, "Thông báo",
                        "Mã giảm giá không tồn tại!");
                    updateAmount();
                    return;
                }

                // Validate the discount amount against the order total
                double totalAmount = ControllerUtils.parseCurrency(totalMoneyValueLabel.getText());
                String validationMessage = DiscountDAO.getInstance().validateDiscount(discount, totalAmount);

                if (validationMessage == null) {
                    // Apply the discount if valid
                    applyDiscount(discount);
                } else {
                    // Show validation error
                    voucherValueLabel.setText("0");
                    ControllerUtils.showAlert(Alert.AlertType.WARNING, "Thông báo", validationMessage);
                }
                updateAmount();
            } catch (Exception e) {
                // Handle any exceptions
                e.printStackTrace();
                voucherValueLabel.setText("0");
                ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi",
                    "Có lỗi xảy ra khi xử lý mã giảm giá!");
                updateAmount();
            }
        });

        // Handle failed discount lookup
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

        // Start the task in a background thread
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Applies a discount to the order
     * Calculates the discount amount based on the discount type and value
     *
     * @param discount The discount to apply
     */
    private void applyDiscount(Discount discount) {
        // Get the current total amount
        double totalAmount = ControllerUtils.parseCurrency(totalMoneyValueLabel.getText());
        double discountAmount;

        // Calculate the discount amount based on the discount type
        if (discount.getDiscountType().equals("percent")) {
            // Percentage discount
            discountAmount = totalAmount * (discount.getValue() / 100);

            // Apply maximum discount limit if specified
            if (discount.getMaxDiscountValue() > 0) {
                discountAmount = Math.min(discountAmount, discount.getMaxDiscountValue());
            }
        } else {
            // Fixed amount discount
            discountAmount = discount.getValue();
        }

        // Ensure discount doesn't exceed the total amount
        discountAmount = Math.min(discountAmount, totalAmount);

        // Update the discount amount display
        voucherValueLabel.setText(ControllerUtils.formatCurrency(discountAmount));
    }

    @FXML
    private void handleConfirmOrder() {
        if (orderVBox.getChildren().isEmpty()) {
            ControllerUtils.showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn ít nhất một sản phẩm!");
            return;
        }

        // Lấy thông tin đơn hàng từ orderVBox
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

        // Tạo đơn hàng mới
        Order order = new Order();
        order.setTotalPrice(totalAmount);
        order.setOrderDate(LocalDateTime.now());
        order.setStaffId(sessionManager.getCurrentStaff().getStaffId());

        // Lấy thông tin giảm giá nếu có
        final Discount discount;
        String voucherCode = voucherTextField.getText().trim();
        if (!voucherCode.isEmpty()) {
            discount = DiscountDAO.getInstance().findByCode(voucherCode);
        } else {
            discount = null;
        }

        // Lưu dữ liệu vào SessionManager
        SessionManager.setCurrentOrder(order);
        SessionManager.setCurrentOrderDetails(orderDetails);
        SessionManager.setCurrentOrderProducts(products);
        SessionManager.setCurrentOrderPets(pets);
        SessionManager.setCurrentDiscount(discount);

        // Clear the temporarily removed pets set since they will be marked as sold in the database
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
                            // Create the item pane and add it to the order
                            AnchorPane itemPane = createItemOrderPane(item);
                            // Set the quantity
                            Object controllerObj = itemPane.getProperties().get("controller");
                            if (controllerObj instanceof ItemList2Controller controller) {
                                // Set the quantity directly instead of incrementing
                                // First reset to 0, then set to the desired quantity
                                controller.addItem(-1); // Reset to 0
                                controller.addItem(detail.getQuantity()); // Set to the desired quantity
                            }
                            itemPane.setUserData(item.getId());
                            tabContent.getChildren().add(itemPane);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            // Set lại voucher nếu có
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

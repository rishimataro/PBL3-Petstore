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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class OrderController implements Initializable {
    private SessionManager sessionManager = new SessionManager();
    
    @FXML
    private ChoiceBox<String> categoryChoiceBox;

    @FXML
    private ChoiceBox<String> sortChoiceBox;

    @FXML
    private FontAwesomeIconView clearSearchIcon;

    @FXML
    private Button confirmButton;

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
    private Label totalMoneyLabel;

    @FXML
    private Label totalMoneyValueLabel;

    @FXML
    private Label voucherLabel;

    @FXML
    private TextField voucherTextField;

    @FXML
    private Label voucherValueLabel;

    @FXML
    private AnchorPane orderContentPane;
    private VBox orderVBox = new VBox(10);

    @FXML
    private ScrollPane orderScrollPane;

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
        setupClearSearchIcon();
        setupDiscountHandling();
        setupButtonActions();
        orderVBox.setAlignment(Pos.TOP_CENTER);
        orderVBox.getStyleClass().add("tab-content");
        orderVBox.setPadding(new Insets(10,3,10,20));
        orderScrollPane.setContent(orderVBox);
        loadOrderFromSession();
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
            handleConfirmOrder();
        });
    }

    private void setupConfirm() {
        if (orderVBox.getChildren().isEmpty()) {
            ControllerUtils.showAlert(Alert.AlertType.WARNING, "Thông báo", "Vui lòng tạo đơn hàng trước khi xác nhận!");
            return;
        }
        Stage currentStage = (Stage) root.getScene().getWindow();
        ViewFactory.getInstance().switchContent("payment", currentStage);
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
                if (item instanceof Product product && product.getStock() <= 0) {
                    continue;
                }
                
                AnchorPane itemPane = getOrCreateItemPane(item);
                itemPane.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        try {
                            addItemToOrder(item);
                            // If it's a pet, remove it from display immediately
                            if (item instanceof Pet) {
                                flowPane.getChildren().remove(itemPane);
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                // Only add if not already present
                if (!flowPane.getChildren().contains(itemPane)) {
                    flowPane.getChildren().add(itemPane);
                }
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

    private Consumer<AnchorPane> createDeleteCallback() {
        return paneToRemove -> {
            VBox tabContent = orderVBox;
            if (tabContent != null) {
                FadeTransition fadeTransition = new FadeTransition(Duration.millis(200), paneToRemove);
                fadeTransition.setFromValue(1.0);
                fadeTransition.setToValue(0.0);
                fadeTransition.setOnFinished(e -> {
                    tabContent.getChildren().remove(paneToRemove);
                    updateAmount();
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
                                            addItemToOrder(item);
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
                        } else if (item instanceof Product product) {
                            try {
                                AnchorPane itemPane = getOrCreateItemPane(product);
                                itemPane.setOnMouseClicked(event -> {
                                    if (event.getButton() == MouseButton.PRIMARY) {
                                        try {
                                            addItemToOrder(product);
                                        } catch (IOException ex) {
                                            throw new RuntimeException(ex);
                                        }
                                    }
                                });
                                flowPane.getChildren().add(itemPane);
                            } catch (IOException ex) {
                                System.err.println("Error adding product back to display: " + ex.getMessage());
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
            controller.setOnQuantityChanged(() -> updateAmount());
            controller.setOnStockChanged(() -> {
                if (item instanceof Product product) {
                    int quantity = controller.getQuantity();
                    int remainingStock = product.getStock() - quantity;
                    if (remainingStock <= 0) {
                        // Remove item from display if out of stock
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

    private void addItemToOrder(Item item) throws IOException {
        if (item == null) return;
        // Check if item already exists in orderVBox
        for (Node node : orderVBox.getChildren()) {
            if (node instanceof AnchorPane pane) {
                Object controllerObj = pane.getProperties().get("controller");
                if (controllerObj instanceof ItemList2Controller controller) {
                    if (controller.getItem().getId() == item.getId()) {
                        controller.AddItem(1);
                        updateAmount();
                        return;
                    }
                }
            }
        }
        AnchorPane itemPane = createItemOrderPane(item);
        itemPane.setUserData(item.getId());
        orderVBox.getChildren().add(itemPane);
        updateAmount();
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

        double finalAmountNumber = ControllerUtils.parseCurrency(totalMoneyValueLabel.getText()) - 
                                 ControllerUtils.parseCurrency(voucherValueLabel.getText());

        finalMoneyValueLabel.setText(ControllerUtils.formatCurrency(finalAmountNumber));
    }

    private void calcAmount() {
        // This method is no longer used
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
                updateAmount();
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
                updateAmount();
            } catch (Exception e) {
                e.printStackTrace();
                voucherValueLabel.setText("0");
                ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Có lỗi xảy ra khi xử lý mã giảm giá!");
                updateAmount();
            }
        });

        task.setOnFailed(event -> {
            Throwable exception = task.getException();
            if (exception != null) {
                exception.printStackTrace();
            }
            voucherValueLabel.setText("0");
            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể kiểm tra mã giảm giá! Vui lòng thử lại sau.");
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
                            // Set lại số lượng
                            Object controllerObj = itemPane.getProperties().get("controller");
                            if (controllerObj instanceof ItemList2Controller controller) {
                                controller.AddItem(detail.getQuantity() - 1); // vì mặc định là 1
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

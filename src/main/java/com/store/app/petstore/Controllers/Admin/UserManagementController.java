package com.store.app.petstore.Controllers.Admin;

import com.store.app.petstore.Controllers.ControllerUtils;
import com.store.app.petstore.DAO.UserDAO;
import com.store.app.petstore.Models.Entities.User;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;

public class UserManagementController implements Initializable {

    @FXML
    private TableView<User> accountTableView;

    @FXML
    private TableColumn<User, Integer> colID;

    @FXML
    private TableColumn<User, String> colPassword;

    @FXML
    private TableColumn<User, String> colRole;

    @FXML
    private TableColumn<User, String> colStatus;

    @FXML
    private TableColumn<User, String> colUsername;

    @FXML
    private Button addAccountButton;

    @FXML
    private FontAwesomeIconView closeIcon;

    @FXML
    private ChoiceBox<String> roleChoiceBox;

    @FXML
    private ChoiceBox<String> statusChoiceBox;

    @FXML
    private AnchorPane root;

    @FXML
    private FontAwesomeIconView searchIcon;

    @FXML
    private TextField searchTextField;

    @FXML
    private Button viewDetailsButton;

    private static Map<Integer, User> userMap;
    private UserDAO userDAO;
    private ObservableList<User> userList;
    private FilteredList<User> filteredUserList;
    private User selectedUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userDAO = new UserDAO();
        setupTableView();
        loadUsers();
        setupChoiceBoxes();
        setupEventHandlers();
        setupSearch();
    }

    private void setupTableView() {
        colID.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));

        // Hide actual password and show asterisks
        colPassword.setCellValueFactory(cellData -> {
            return new SimpleStringProperty("********");
        });

        colRole.setCellValueFactory(cellData -> {
            String role = cellData.getValue().getRole();
            String displayRole = User.ROLE_ADMIN.equals(role) ? "Quản trị viên" : "Nhân viên";
            return new SimpleStringProperty(displayRole);
        });

        colStatus.setCellValueFactory(cellData -> {
            boolean isActive = cellData.getValue().isActive();
            String status = isActive ? "Hiệu lực" : "Đã khóa";
            return new SimpleStringProperty(status);
        });

        // Add row click listener
        accountTableView.setRowFactory(tv -> {
            TableRow<User> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 1) {
                    selectedUser = row.getItem();
                    viewDetailsButton.setDisable(false);
                }
            });
            return row;
        });
    }

    private void loadUsers() {
        ArrayList<User> users = userDAO.findAll();
        userList = FXCollections.observableArrayList(users);
        filteredUserList = new FilteredList<>(userList, p -> true);
        accountTableView.setItems(filteredUserList);
    }

    private void setupChoiceBoxes() {
        roleChoiceBox.getItems().addAll("Tất cả", "Nhân viên", "Quản trị viên");
        roleChoiceBox.setValue("Tất cả");

        statusChoiceBox.getItems().addAll("Tất cả", "Hiệu lực", "Đã khóa");
        statusChoiceBox.setValue("Tất cả");

        // Add listeners for filtering
        roleChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            applyFilters();
        });

        statusChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            applyFilters();
        });
    }

    private void setupEventHandlers() {
        // Add account button
        addAccountButton.setOnAction(event -> {
            openUserInfoPopup(null);
        });

        // View details button
        viewDetailsButton.setOnAction(event -> {
            if (selectedUser != null) {
                openUserInfoPopup(selectedUser);
            }
        });

        // Close button
        closeIcon.setOnMouseClicked(event -> {
            Stage stage = (Stage) root.getScene().getWindow();
            stage.close();
        });

        // Search icon
        searchIcon.setOnMouseClicked(event -> {
            applyFilters();
        });

        // Search text field enter key
        searchTextField.setOnAction(event -> {
            applyFilters();
        });
    }

    private void setupSearch() {
        // Initialize with disabled view details button
        viewDetailsButton.setDisable(true);
    }

    private void applyFilters() {
        String searchText = searchTextField.getText().toLowerCase().trim();
        String roleFilter = roleChoiceBox.getValue();
        String statusFilter = statusChoiceBox.getValue();

        filteredUserList.setPredicate(user -> {
            // Filter by search text (username)
            boolean matchesSearch = searchText.isEmpty() ||
                    user.getUsername().toLowerCase().contains(searchText);

            // Filter by role
            boolean matchesRole = roleFilter.equals("Tất cả") ||
                    (roleFilter.equals("Quản trị viên") && User.ROLE_ADMIN.equals(user.getRole())) ||
                    (roleFilter.equals("Nhân viên") && User.ROLE_USER.equals(user.getRole()));

            // Filter by status
            boolean matchesStatus = statusFilter.equals("Tất cả") ||
                    (statusFilter.equals("Hiệu lực") && user.isActive()) ||
                    (statusFilter.equals("Đã khóa") && !user.isActive());

            return matchesSearch && matchesRole && matchesStatus;
        });
    }

    private void openUserInfoPopup(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Admin/UserInfor.fxml"));
            Parent root = loader.load();

            UserInforController controller = loader.getController();
            if (user != null) {
                controller.setUser(user);
            }

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Refresh the table after the popup is closed
            loadUsers();
            applyFilters();

        } catch (IOException e) {
            e.printStackTrace();
            ControllerUtils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể mở cửa sổ thông tin người dùng!");
        }
    }

    // Method to refresh the table from outside
    public void refreshTable() {
        loadUsers();
        applyFilters();
    }
}

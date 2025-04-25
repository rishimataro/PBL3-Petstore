package com.store.app.petstore.Controllers.Staff;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class StaffDashboardController implements Initializable {
    @FXML
    private Label staffNameLabel;
    
    @FXML
    private Label todayOrdersLabel;
    
    @FXML
    private Label totalRevenueLabel;
    
    @FXML
    private Label activeCustomersLabel;
    
    @FXML
    private Label pendingTasksLabel;
    
    @FXML
    private VBox recentActivitiesContainer;
    
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize dashboard with staff name
        // TODO: Get staff name from session or database
        staffNameLabel.setText("Staff Name");
        
        // Initialize dashboard values
        initializeDashboardValues();
        
        // Initialize recent activities
        initializeRecentActivities();
    }
    
    private void initializeDashboardValues() {
        // Set initial values
        todayOrdersLabel.setText("0");
        totalRevenueLabel.setText(currencyFormat.format(0.00));
        activeCustomersLabel.setText("0");
        pendingTasksLabel.setText("0");
        
        // TODO: Load actual values from database
    }
    
    private void initializeRecentActivities() {
        // TODO: Load recent activities from database
        // This is a placeholder for demonstration
        Label noActivitiesLabel = new Label("No recent activities");
        noActivitiesLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14px;");
        recentActivitiesContainer.getChildren().add(noActivitiesLabel);
    }
    
    // TODO: Add methods for handling quick action buttons
    // TODO: Add methods for updating statistics
    // TODO: Add methods for loading recent activities
} 
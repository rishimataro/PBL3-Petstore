package com.store.app.petstore.Utils;

import java.util.Map;
import java.util.HashMap;
import java.util.function.Supplier;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

public class TabManager {
    private final Map<String, Tab> openTabs = new HashMap<>();
    private final TabPane tabPane;

    public TabManager(TabPane tabPane) {
        this.tabPane = tabPane;
    }

    /**
     * Opens a new tab with the given ID, title, and content.
     * If the tab already exists, it is selected (switched to).
     *
     * @param tabId         The unique identifier for the tab.
     * @param tabTitle      The title of the tab.
     * @param contentSupplier A supplier for the tab's content (AnchorPane).
     */
    public void openTab(String tabId, String tabTitle, Supplier<AnchorPane> contentSupplier) {
        if (openTabs.containsKey(tabId)) {
            // Tab already exists, switch to it
            Tab existingTab = openTabs.get(tabId);
            existingTab.selectedProperty();
        } else {
            Tab newTab = new Tab(tabTitle);
            newTab.setContent(contentSupplier.get());
            openTabs.put(tabId, newTab);
            tabPane.getTabs().add(newTab);
        }
    }

    /**
     * Closes the tab with the given ID, if it exists.
     *
     * @param tabId The unique identifier of the tab to close.
     */
    public void closeTab(String tabId) {
        if (openTabs.containsKey(tabId)) {
            Tab tab = openTabs.remove(tabId);
            tabPane.getTabs().remove(tab);
        }
    }

    /**
     * Closes all open tabs.
     */
    public void closeAllTabs() {
        openTabs.clear();
        tabPane.getTabs().clear();
    }
}

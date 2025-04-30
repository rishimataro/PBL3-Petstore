import java.util.Map;
import java.util.HashMap;
import java.util.function.Supplier;
import javafx.scene.control.Tab;

public class TabManager {
    private final Map<String, Tab> openTabs = new HashMap<>();

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
            // Tab already exists, switch to it (implementation of switching depends on TabPane)
            Tab existingTab = openTabs.get(tabId);
            // Note: Actual switching logic (e.g., selecting the tab in a TabPane) is not included here
            // and should be handled externally or extended in a subclass.
        } else {
            Tab newTab = new Tab(tabTitle);
            newTab.setContent(contentSupplier.get());
            openTabs.put(tabId, newTab);
        }
    }

    /**
     * Closes the tab with the given ID, if it exists.
     *
     * @param tabId The unique identifier of the tab to close.
     */
    public void closeTab(String tabId) {
        openTabs.remove(tabId);
    }

    /**
     * Closes all open tabs.
     */
    public void closeAllTabs() {
        openTabs.clear();
    }
}

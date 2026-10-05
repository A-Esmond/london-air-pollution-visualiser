package menus;

import panels.Panel;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores and manages the collection of panels used for navigation.
 *
 * Provides access to panels by name and allows configuration of a
 * default panel. Acts as a central registry for UI navigation.
 *
 * @author E. N.
 * @version 26/3/26
 */
public class TopMenuBarInfo {

    private final Map<String, Panel> panels;
    private Panel defaultPanel;

    /**
     * Constructs an empty TopMenuBarInfo.
     */
    public TopMenuBarInfo() {
        panels = new HashMap<>();
    }

    /**
     * Adds a panel to the collection.
     *
     * @param panel
     */
    public void addPanel(Panel panel) {
        panels.put(panel.getName(), panel);
    }

    /**
     * Retrieves a panel by its name.
     *
     * @param str
     * @return The corresponding Panel, or null if not found
     */
    public Panel getPanel(String str) {
        return panels.get(str);
    }

    /**
     * Returns all registered panels.
     *
     * @return A map of panel names to Panel objects
     */
    public Map<String, Panel> getPanels() {
        return panels;
    }

    /**
     * Sets the default panel by name.
     *
     * @param name
     */
    public void setDefaultPanel(String name) {
        defaultPanel = getPanel(name);
    }

    /**
     * Returns the default panel.
     *
     * @return The default Panel
     */
    public Panel getDefaultPanel() {
        return defaultPanel;
    }

    /**
     * Adds multiple panels to the collection.
     *
     * @param panels
     */
    public void addPanels(Panel... panels) {
        for (Panel panel : panels) {
            addPanel(panel);
        }
    }
}
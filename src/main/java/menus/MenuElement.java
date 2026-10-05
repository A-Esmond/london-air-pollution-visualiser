package menus;

import javafx.scene.control.MenuItem;
import panels.Panel;

/**
 * Represents a pairing between a MenuItem and its corresponding Panel.
 *
 * Used to link UI menu selections to the panel that should be displayed
 * when the item is selected.
 *
 * @param item
 * @param panel
 */
public record MenuElement(MenuItem item, Panel panel) {
}
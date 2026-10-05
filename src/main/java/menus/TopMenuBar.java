package menus;

import core.Display;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;

import java.util.List;

/**
 * Utility class for creating the application's top navigation menu bar.
 *
 * Generates a MenuBar containing menu items that allow the user to
 * switch between different panels in the application.
 *
 * @author E. N.
 * @version 26/3/26
 */
public class TopMenuBar {

    /**
     * Creates a MenuBar with navigation items linked to panels.
     *
     * @param display
     * @param menuElements
     * @return A configured MenuBar for panel navigation
     */
    public static MenuBar create(Display display, List<MenuElement> menuElements) {
        Menu navigationMenu = new Menu("Navigate");

        for (MenuElement element : menuElements) {
            element.item().setOnAction(
                    e -> display.changeCurrentPanel(element.panel().getName())
            );
            navigationMenu.getItems().add(element.item());
        }

        return new MenuBar(navigationMenu);
    }
}
package core;

import content.MapGraphic;
import content.MenuBarContent;
import javafx.animation.FadeTransition;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import menus.MenuElement;
import menus.TopMenuBar;
import menus.TopMenuBarInfo;
import panels.Panel;
import src.resources.FilePathLoader;
import src.resources.LondonMapData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages the main application window and panel navigation.
 *
 * Responsible for setting up the primary stage, handling panel switching,
 * and applying UI transitions. Acts as the central controller for the
 * application's visual layout.
 *
 * @author E. N.
 * @version 26/3/26
 */
public class Display {

    private final BorderPane root;
    private Map<String, Panel> panels;

    /**
     * Constructs the main display container.
     */
    public Display() {
        root = new BorderPane();
        panels = new HashMap<>();
    }

    /**
     * Initialises and shows the main application window.
     *
     * @param stage
     * @param dataHandler
     */
    public void showDisplay(Stage stage, DataHandler dataHandler) {
        TopMenuBarInfo info = MenuBarContent.navigationInfo(this, dataHandler);
        panels = info.getPanels();

        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(FilePathLoader.load("styles.css"));

        changeCurrentPanel(info.getDefaultPanel().getName());

        root.setTop(TopMenuBar.create(this, createMenuElements(panels)));

        stage.setScene(scene);
        stage.setTitle("London Air Pollution");
        stage.show();
    }

    /**
     * Creates menu elements for all registered panels.
     *
     * @param panels
     * @return A list of MenuElement objects for navigation
     */
    private List<MenuElement> createMenuElements(Map<String, Panel> panels) {
        List<MenuElement> menuElements = new ArrayList<>();
        for (String key : panels.keySet()) {
            menuElements.add(
                    new MenuElement(new MenuItem(key), panels.get(key))
            );
        }
        return menuElements;
    }

    /**
     * Switches the currently displayed panel.
     *
     * Applies a fade-out transition to the current panel and a fade-in
     * transition to the new panel for a smoother user experience.
     *
     * @param panelName
     */
    public void changeCurrentPanel(String panelName) {
        Pane panel = panels.get(panelName);

        if (panel != null) {
            FadeTransition fadeOut = new FadeTransition(
                    Duration.millis(250), root.getCenter()
            );
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            fadeOut.setOnFinished(e -> {
                root.setCenter(panel);

                FadeTransition fadeIn = new FadeTransition(
                        Duration.millis(250), panel
                );
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });

            fadeOut.play();
        } else {
            System.out.println("Panel not found: " + panelName);
        }
    }

    /**
     * Creates a new MapGraphic instance for displaying the map.
     *
     * @return A MapGraphic object
     */
    public MapGraphic getMapGraphics() {
        return new MapGraphic(LondonMapData.getImagePath());
    }
}
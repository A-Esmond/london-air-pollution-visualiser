package panels;

import core.DataHandler;
import core.Display;
import javafx.scene.layout.BorderPane;

/**
 * Abstract base class for all panels in the application.
 * Extends BorderPane to provide a structured layout, and holds
 * shared references to the display and data handler used across panels.
 *
 * @author E. N.
 * @version 26/3/26
 */
public abstract class Panel extends BorderPane {
    private final String name;
    protected final Display display;
    protected final DataHandler dataHandler;

    /**
     * Constructs a Panel with the given name, display, and data handler.
     *
     * @param name
     * @param display
     * @param dataHandler
     */
    protected Panel(String name, Display display, DataHandler dataHandler) {
        this.name = name;
        this.display = display;
        this.dataHandler = dataHandler;
        //add styling option here
    }

    /**
     * Returns the name of this panel.
     *
     * @return
     */
    public String getName() {
        return name;
    }
}
package panels;

import javafx.scene.layout.BorderPane;
import model.DataSet;
import java.util.List;

/**
 * Abstract class that represents a window in the statistics panel.
 * A window is a border pane that shows statistical data.
 * It must be built, updated and viewed.
 *
 * @author Esmond Atiemo
 * @version 2026.03.22
 */
public abstract class StatisticsPanelWindow {
    protected BorderPane viewPane;

    public StatisticsPanelWindow(BorderPane viewPane) {
        this.viewPane = viewPane;
    }

    public abstract void build();

    /**
     * Updates the statistical data displayed based on the latest user input query.
     *
     * @param queryData Filtered query data.
     */
    public abstract void update(List<List<DataSet>> queryData);

    public abstract BorderPane view();
}

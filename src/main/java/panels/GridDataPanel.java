/**
 * @author P. P.
 * GridDataPanel displays air pollution data for individual grid points.
 * It allows users to search for a specific grid point by grid code,
 * and displays its easting, northing, and pollution level.
 * The panel also supports filtering by pollutant type (NO2, PM10, PM2.5).
 */

package panels;

import core.DataHandler;
import core.Display;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.DataPoint;
import model.DataSet;

public class GridDataPanel extends Panel {
    // Search
    private final TextField searchBox = new TextField();
    private final Label searchCode = new Label("-");
    // The full dataset to search through
    private DataSet currentDataSet;

    private final Label gridCodeVal = new Label("—");
    private final Label xVal = new Label("—");
    private final Label yVal = new Label("—");
    private final Label valueVal = new Label("—");
    private final Label cursorGridCodeVal = new Label("-");

    public GridDataPanel(String name, Display display, DataHandler dataHandler) {
        super(name, display, dataHandler);
        setPrefWidth(210);

        setPadding(new Insets(16));

        VBox content = new VBox(12);
        content.getChildren().addAll(
                buildSearchSection(),
                buildCursorSection(),
                buildDetailSection()
        );

        setCenter(content);
    }

    private VBox buildSearchSection() {
        Label title = new Label("Grid Data");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        searchBox.setPromptText("Search by grid code...");
        searchBox.setMaxWidth(Double.MAX_VALUE);

        searchBox.textProperty().addListener((obs, oldVal, newVal) -> {
            if (currentDataSet == null) return;

            String query = newVal.trim();
            if (query.isEmpty()) {
                clearDetail();
                return;
            }

            DataPoint found = null;
            for (DataPoint point : currentDataSet.getData()) {
                if (String.valueOf(point.gridCode()).contains(query)) {
                    found = point;
                    break;
                }
            }

            if (found != null) {
                showDetail(found);
            } else {
                clearDetail();
            }
        });

        return new VBox(8, title,
                new Label("Search"),
                searchBox
        );
    }

    private VBox buildDetailSection() {
        Label sectionLabel = new Label("Selected point");
        sectionLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: grey;");

        VBox card = new VBox(8,
                sectionLabel,
                detailRow("Grid code", gridCodeVal),
                detailRow("Easting", xVal),
                detailRow("Northing", yVal),
                detailRow("Level", valueVal)
        );

        card.setPadding(new Insets(12));
        card.setStyle(
                "-fx-border-color: #e0e0e0;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-background-color: #fafafa;"
        );

        return new VBox(8, card);
    }

    private VBox buildCursorSection() {
        cursorGridCodeVal.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(cursorGridCodeVal, Priority.ALWAYS);

        VBox section = new VBox(8, new Label("Current Grid Code"), cursorGridCodeVal);
        section.setMaxWidth(Double.MAX_VALUE);
        return section;
    }

    private HBox detailRow(String labelText, Label valueLabel) {
        Label lbl = new Label(labelText);
        lbl.setStyle("-fx-text-fill: grey; -fx-font-size: 12px;");
        lbl.setMinWidth(60);

        valueLabel.setStyle("-fx-font-size: 12px;");

        HBox row = new HBox(lbl, valueLabel);
        row.setPadding(new Insets(4, 0, 4, 0));
        row.setStyle("-fx-border-color: transparent transparent #e8e8e8 transparent;");

        return row;
    }

    public void showDetail(DataPoint point) {
        gridCodeVal.setText(String.valueOf(point.gridCode()));
        xVal.setText(String.format("%,d", point.x()));
        yVal.setText(String.format("%,d", point.y()));
        valueVal.setText(String.format("%.2f µg/m³", point.value()));
    }

    public void clearDetail() {
        gridCodeVal.setText("—");
        xVal.setText("—");
        yVal.setText("—");
        valueVal.setText("—");
    }


    public void loadDataSet(DataSet data) {
        currentDataSet = data;
    }
}
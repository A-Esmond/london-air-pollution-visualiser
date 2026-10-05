package panels;

import content.MapGraphic;
import core.DataHandler;
import core.Display;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import model.DataPoint;
import model.DataSet;
import src.resources.LondonMapData;
import util.DistanceCalculator;
import util.Tuple;

import java.util.Set;

/**
 * Displays an interactive map of London overlaid with pollution data points.
 * Users can select a pollutant and year to visualise, then click on the map
 * to inspect the nearest data point in detail via the GridDataPanel.
 *
 * @author E. N.
 * @version 26/3/26
 */
public class MapPanel extends Panel {
    private final MapGraphic mapGraphic;

    private Label clickedCoordinate;
    private Canvas markerLayer;
    private final GridDataPanel gridDataPanel;

    private DataSet dataSet;
    private DataPoint highlightedDatapoint;

    private final ComboBox<String> pollutantBox = new ComboBox<>();
    private final ComboBox<String> yearBox = new ComboBox<>();
    private final Button goButton = new Button("GO");

    /**
     * Constructs the MapPanel, initialising the map graphic, data panel,
     * and all UI components.
     *
     * @param name
     * @param display
     * @param dataHandler
     */
    public MapPanel(String name, Display display, DataHandler dataHandler) {
        super(name, display, dataHandler);

        this.gridDataPanel = new GridDataPanel("Grid Data", display, dataHandler);
        this.mapGraphic = display.getMapGraphics();

        setCenter(buildScrollPane());
        setTop(buildOverlay());
        setRight(gridDataPanel);
    }

    /**
     * Builds the scrollable map area containing the London image and the
     * marker layer canvas stacked on top.
     *
     * @return
     */
    private ScrollPane buildScrollPane() {
        StackPane content = new StackPane();
        content.setAlignment(Pos.CENTER);

        mapGraphic.bindSizeTo(content);
        markerLayer = mapGraphic.getMarkerLayer();

        setUpMouseListeners();

        content.getChildren().addAll(mapGraphic.getImageView(), markerLayer);
        return new ScrollPane(content);
    }

    /**
     * Builds the overlay bar anchored to the top of the panel.
     * Contains the coordinate display, dataset selectors, and clicked coordinate label.
     *
     * @return
     */
    private AnchorPane buildOverlay() {
        AnchorPane overlay = mapGraphic.buildOverlay();

        buildDatasetSelector();
        overlay.setPadding(new Insets(10));
        overlay.getChildren().addAll(pollutantBox, yearBox, goButton);

        AnchorPane.setTopAnchor(pollutantBox, 5.0);
        AnchorPane.setRightAnchor(pollutantBox, 400.0);

        AnchorPane.setTopAnchor(yearBox, 5.0);
        AnchorPane.setRightAnchor(yearBox, 300.0);

        AnchorPane.setTopAnchor(goButton, 5.0);
        AnchorPane.setRightAnchor(goButton, 200.0);

        clickedCoordinate = new Label("Clicked Coords: ");
        overlay.getChildren().add(clickedCoordinate);
        AnchorPane.setTopAnchor(clickedCoordinate, 10.0);
        AnchorPane.setLeftAnchor(clickedCoordinate, 10.0);

        return overlay;
    }

    /**
     * Populates the pollutant and year dropdowns from the pollutant registry,
     * and wires the GO button to trigger dataset selection.
     */
    private void buildDatasetSelector() {
        Set<String> names = dataHandler.getPollutantRegistry().getPollutantNames();
        pollutantBox.getItems().add("Default");
        pollutantBox.setValue("Default");
        pollutantBox.getItems().addAll(names);
        pollutantBox.setMaxWidth(Double.MAX_VALUE);
        pollutantBox.setPromptText("Pollutant");

        Set<String> years = dataHandler.getPollutantRegistry().getYearsCovered();
        yearBox.getItems().add("Default");
        yearBox.setValue("Default");
        yearBox.getItems().addAll(years);
        yearBox.setMaxWidth(Double.MAX_VALUE);
        yearBox.setPromptText("Years");

        goButton.setOnAction(this::registerDatasetSelectionValues);
    }

    /**
     * Reads the selected pollutant and year from the dropdowns and loads
     * the corresponding dataset. Clears the map if either selection is "Default".
     *
     * @param event
     */
    private void registerDatasetSelectionValues(ActionEvent event) {
        String yearSelected = yearBox.getValue();
        String pollutantSelected = pollutantBox.getValue();

        if ("Default".equals(yearSelected) || "Default".equals(pollutantSelected)) {
            gridDataPanel.clearDetail();
            renderDataPoints(null);
            return;
        }

        DataSet selectedDataset = dataHandler.getPollutantRegistry()
                .lookForDataset(pollutantSelected, yearSelected);

        changeDataset(selectedDataset);
    }

    /**
     * Registers click and move listeners on the map graphic.
     * On click, finds and displays the nearest data point.
     * On move, highlights the nearest data point to the cursor.
     */
    private void setUpMouseListeners() {
        mapGraphic.addClickListener((easting, northing) -> {
            if (dataSet == null) {
                System.err.println("Warning: Click registered but no dataset loaded.");
                return;
            }

            highlightedDatapoint = DistanceCalculator.getClosestDatapoint(dataSet, new Tuple<>(easting, northing));
            gridDataPanel.showDetail(highlightedDatapoint);
            clickedCoordinate.setText("Clicked coords: " + easting + ", " + northing);
        });

        mapGraphic.addMoveListener((easting, northing) -> {
            if (dataSet == null) return;
            DataPoint nearest = DistanceCalculator.getClosestDatapoint(dataSet, new Tuple<>(easting, northing));
            if (nearest != highlightedDatapoint) {
                highlightedDatapoint = nearest;
                renderDataPoints(dataSet);
                renderHoverHighlight(nearest);
            }
        });
    }

    /**
     * Switches the active dataset, updating the grid panel and re-rendering
     * all data points on the marker layer.
     *
     * @param dataSet
     */
    public void changeDataset(DataSet dataSet) {
        this.dataSet = dataSet;
        gridDataPanel.loadDataSet(dataSet);
        renderDataPoints(dataSet);
    }

    /**
     * Clears the marker layer and re-renders all data points for the given dataset.
     * Passing null clears the map without rendering anything.
     *
     * @param dataSet
     */
    private void renderDataPoints(DataSet dataSet) {
        GraphicsContext gc = markerLayer.getGraphicsContext2D();
        gc.clearRect(0, 0, markerLayer.getWidth(), markerLayer.getHeight());

        if (dataSet == null) {
            return;
        }

        for (DataPoint dp : dataSet.getData()) {
            renderDataPoint(gc, dp, dataSet);
        }
    }

    /**
     * Renders a single data point on the marker layer.
     * Each point is drawn with a white border ring for contrast against the map,
     * with a colour-coded fill indicating its pollution level.
     *
     * @param gc
     * @param dp
     * @param dataSet
     */
    private void renderDataPoint(GraphicsContext gc, DataPoint dp, DataSet dataSet) {
        double pixelX = LondonMapData.mapToImageWidth(dp.x());
        double pixelY = LondonMapData.mapToImageHeight(dp.y());
        double value = dp.value();

        Color fill = getColorForValue(value, dataSet.getMinValue(), dataSet.getMaxValue());

        // White border drawn slightly larger to create a contrast ring against the map
        gc.setFill(Color.WHITE);
        gc.setGlobalAlpha(0.9);
        gc.fillOval(pixelX - 5, pixelY - 5, 10, 10);

        // Colour-coded fill drawn on top
        gc.setFill(fill);
        gc.setGlobalAlpha(0.85);
        gc.fillOval(pixelX - 4, pixelY - 4, 8, 8);
    }

    /**
     * Maps a normalised pollution value to a display colour.
     * The scale runs from blue (low) through green, yellow, orange, to red (high).
     *
     * @param value
     * @param min
     * @param max
     * @return
     */
    private Color getColorForValue(double value, double min, double max) {
        double normalised = (value - min) / (max - min); // 0.0 to 1.0

        if (normalised < 0.2) {
            return Color.web("#00C2FF"); // blue — low pollution
        } else if (normalised < 0.4) {
            return Color.web("#00E676"); // green
        } else if (normalised < 0.6) {
            return Color.web("#FFD600"); // yellow
        } else if (normalised < 0.8) {
            return Color.web("#FF6D00"); // orange
        } else {
            return Color.web("#D50000"); // red — high pollution
        }
    }

    /**
     * Draws a highlight ring around the given data point on the marker layer,
     * indicating it is the nearest point to the cursor.
     *
     * @param dp
     */
    private void renderHoverHighlight(DataPoint dp) {
        GraphicsContext gc = markerLayer.getGraphicsContext2D();
        double pixelX = LondonMapData.mapToImageWidth(dp.x());
        double pixelY = LondonMapData.mapToImageHeight(dp.y());

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.setGlobalAlpha(1.0);
        gc.strokeOval(pixelX - 8, pixelY - 8, 16, 16);
    }
}
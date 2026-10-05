package panels;

import content.MapGraphic;
import core.Display;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import model.DataPoint;
import model.DataSet;
import src.resources.LondonMapData;
import util.StatisticsPanelCalculator;
import util.Tuple;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Window used for graphing peak pollution levels as a heat map.
 *
 * @author Esmond Atiemo
 * @version 2026.10.01
 */
public class HeatMapWindow extends StatisticsPanelWindow {
    private final MapGraphic mapGraphics;

    public HeatMapWindow(Display display) {
        super(new BorderPane());
        this.mapGraphics = display.getMapGraphics();
        build();
    }

    @Override
    public void build() {
        viewPane.setPadding(new Insets(15));
        viewPane.setCenter(buildMapDisplay());
    }

    /**
     * Wrapper for the scroll pane containing the map graphic.
     *
     * @return UI element.
     */
    private VBox buildMapDisplay() {
        VBox mapDisplay = new VBox();
        mapDisplay.setPadding(new Insets(10, 0, 0, 0));
        mapDisplay.getChildren().add(buildScrollPane());
        return mapDisplay;
    }

    /**
     * Boilerplate code from MapPanel.
     * Scroll pane containing the map image and marker layer.
     *
     * @return UI element.
     */
    private ScrollPane buildScrollPane() {
        StackPane content = new StackPane();
        content.setAlignment(Pos.CENTER);
        mapGraphics.bindSizeTo(content);
        content.getChildren().addAll(mapGraphics.getImageView(), mapGraphics.getMarkerLayer());
        ScrollPane scrollPane = new ScrollPane(content);
        String TILE_STYLE = "-fx-border-color: #cccccc; -fx-border-radius: 6; -fx-background-radius: 6; -fx-background-color: #f9f9f9;";
        scrollPane.setStyle(TILE_STYLE);
        return scrollPane;
    }

    /**
     * Flattens all datapoints in a query to a list of the highest recorded value for each x, y position.
     * This is then normalised and translated to a color value from green - red.
     * Each datapoint has a blurred circle of their corresponding color crated.
     *
     * @param queryData Filtered query data.
     */
    @Override
    public void update(List<List<DataSet>> queryData) {
        List<DataPoint> allPoints = queryData.stream()
                .flatMap(lst -> lst.stream()
                        .flatMap(ds -> ds.getData().stream()))
                .toList();

        GraphicsContext graphicsContext = mapGraphics.getMarkerLayer().getGraphicsContext2D();
        graphicsContext.clearRect(0, 0, mapGraphics.getMarkerLayer().getWidth(), mapGraphics.getMarkerLayer().getHeight());

        if (allPoints.isEmpty()) {
            return;
        }

        Map<Tuple<Integer, Integer>, Double> maxValues = new HashMap<>();
        for (DataPoint dataPoint : allPoints) {
            Tuple<Integer, Integer> coords = new Tuple<>(dataPoint.x(), dataPoint.y());
            maxValues.merge(coords, dataPoint.value(), Math::max);
        }

        double min = maxValues.values().stream().mapToDouble(Double::doubleValue).min().orElse(0);
        double max = maxValues.values().stream().mapToDouble(Double::doubleValue).max().orElse(1);

        for (Tuple<Integer, Integer> coords : maxValues.keySet()) {
            double normalised = StatisticsPanelCalculator.normalise(maxValues.get(coords), min, max);
            Color color = mapValueToColor(normalised);
            double pixelX = LondonMapData.mapToImageWidth(coords.val1());
            double pixelY = LondonMapData.mapToImageHeight(coords.val2());
            drawBlurredCircle(graphicsContext, pixelX, pixelY, color);
        }
    }

    /**
     * Maps a normalised value (0.0 to 1.0) to a colour on a scale of green to red.
     * To change the colour scheme, only this method needs updating.
     *
     * @param normalised Value between 0.0 and 1.0.
     * @return Corresponding colour.
     */
    private Color mapValueToColor(double normalised) {
        return Color.color(normalised, 1.0 - normalised, 0.0, 0.6);
    }

    /**
     * Draws a blurred circle at the given coordinates by layering
     * concentric circles with decreasing opacity.
     *
     * @param graphicsContext Graphics context to draw on.
     * @param x               X coordinate in canvas pixel space.
     * @param y               Y coordinate in canvas pixel space.
     * @param color           Base colour.
     */
    private void drawBlurredCircle(GraphicsContext graphicsContext, double x, double y, Color color) {
        for (int i = 14; i >= 1; i--) {
            double layerRadius = 45.0 * i / 5.0;
            double opacity = 0.002 * (15 - i);
            graphicsContext.setFill(new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity));
            graphicsContext.fillOval(x - layerRadius, y - layerRadius, layerRadius * 2, layerRadius * 2);
        }
    }


    @Override
    public BorderPane view() {
        return viewPane;
    }
}
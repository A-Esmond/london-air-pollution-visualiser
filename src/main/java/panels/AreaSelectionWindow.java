package panels;

import content.MapGraphic;
import core.Display;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import model.DataSet;
import src.resources.LondonMapData;
import util.Query;
import util.QueryError;
import util.Tuple;
import java.util.List;
import java.util.function.Consumer;

/**
 * Window used for displaying the map and collecting coord inputs for a query.
 *
 * @author Esmond Atiemo
 * @version 2026.10.01
 */
public class AreaSelectionWindow extends StatisticsPanelWindow {
    private final MapGraphic mapGraphics;
    private final Query.Builder queryBuilder;
    private boolean awaitingSecondClick;
    private final String TILE_STYLE = "-fx-border-color: #cccccc; -fx-border-radius: 6; -fx-background-radius: 6; -fx-background-color: #f9f9f9;";
    private final Consumer<QueryError> errorAlert;

    public AreaSelectionWindow(Display display, Query.Builder queryBuilder, Consumer<QueryError> errorAlert) {
        super(new BorderPane());
        this.mapGraphics = display.getMapGraphics();
        this.queryBuilder = queryBuilder;
        this.errorAlert = errorAlert;
        build();
    }

    @Override
    public void build() {
        viewPane.setPadding(new Insets(15));
        viewPane.setTop(buildMapOverlay());
        viewPane.setCenter(buildMapDisplay());
    }

    /**
     * Dedicated UI overlay with instructions and coord value of the mouse pointers
     * current location.
     *
     * @return UI element.
     */
    private VBox buildMapOverlay() {
        VBox mapOverlay = new VBox();
        mapOverlay.setAlignment(Pos.TOP_CENTER);
        mapOverlay.setPadding(new Insets(10));
        mapOverlay.setStyle(TILE_STYLE);

        Label heading = new Label("Select two points with your mouse below.");
        mapOverlay.getChildren().addAll(heading, mapGraphics.buildOverlay());
        return mapOverlay;
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
        mapGraphics.addClickListener((easting, northing) -> {
            if (!awaitingSecondClick) {
                if (invalidCoord(easting, northing, queryBuilder.getSecondCoords())) {
                    errorAlert.accept(QueryError.AREA_TOO_SMALL);
                    return;
                }
                clearCross(queryBuilder.getFirstCoords());
                Tuple<Integer, Integer> firstCoords = new Tuple<>(easting, northing);
                queryBuilder.firstCoords(firstCoords);
                drawCross(firstCoords);
                awaitingSecondClick = true;
            } else {
                if (invalidCoord(easting, northing, queryBuilder.getFirstCoords())) {
                    errorAlert.accept(QueryError.AREA_TOO_SMALL);
                    return;
                }
                clearCross(queryBuilder.getSecondCoords());
                Tuple<Integer, Integer> secondCoords = new Tuple<>(easting, northing);
                queryBuilder.secondCoords(secondCoords);
                drawCross(secondCoords);
                awaitingSecondClick = false;
            }
        });

        content.getChildren().addAll(mapGraphics.getImageView(), mapGraphics.getMarkerLayer());
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setStyle(TILE_STYLE);
        return scrollPane;
    }

    /**
     * A new point is rejected if it is within 1 km (one grid square) of the other
     * corner in either direction, because the box would contain almost no data.
     */
    private boolean invalidCoord(int easting, int northing, Tuple<Integer, Integer> coords) {
        if (coords == null) {
            return false;
        }
        return Math.abs(easting - coords.val1()) < 1000 || Math.abs(northing - coords.val2()) < 1000;
    }

    private void clearCross(Tuple<Integer, Integer> coords) {
        if (coords == null) {
            return;
        }
        GraphicsContext graphicsContext = mapGraphics.getMarkerLayer().getGraphicsContext2D();
        double x = LondonMapData.mapToImageWidth(coords.val1());
        double y = LondonMapData.mapToImageHeight(coords.val2());
        graphicsContext.clearRect(x - 6, y - 6, 12, 12);
    }

    /**
     * Draws cell shaded red cross.
     *
     * @param coords Drawing location.
     */
    private void drawCross(Tuple<Integer, Integer> coords) {
        GraphicsContext graphicsContext = mapGraphics.getMarkerLayer().getGraphicsContext2D();
        double x = LondonMapData.mapToImageWidth(coords.val1());
        double y = LondonMapData.mapToImageHeight(coords.val2());
        graphicsContext.setStroke(Color.BLACK);
        graphicsContext.setLineWidth(3);
        graphicsContext.strokeLine(x - 4, y - 4, x + 4, y + 4);
        graphicsContext.strokeLine(x + 4, y - 4, x - 4, y + 4);
        graphicsContext.setStroke(Color.RED);
        graphicsContext.setLineWidth(1);
        graphicsContext.strokeLine(x - 4, y - 4, x + 4, y + 4);
        graphicsContext.strokeLine(x + 4, y - 4, x - 4, y + 4);
    }

    @Override
    public void update(List<List<DataSet>> queryData) {
    }

    @Override
    public BorderPane view() {
        return viewPane;
    }

}
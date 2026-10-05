package panels;

import core.DataHandler;
import core.Display;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.DataFilter;
import model.DataSet;
import util.Query;
import util.QueryError;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Provides summary statistics including:
 * - Average pollution level over the selected period or area.
 * - Highest pollution levels recorded and their locations.
 * - Trends over time with a simple graph.
 * Additional functionality:
 * - The user can switch between different statistics.
 * - The user can query multiple pollutants at once.
 * - Bar chart comparing annual pollutant peaks
 * - Heat map showing pollutant concentration.
 *
 * @author Esmond Atiemo
 * @version 2026.10.01
 */
public class StatisticsPanel extends Panel {
    private final Query.Builder queryBuilder = new Query.Builder();
    private final FiltersColumn filtersColumn;
    private final AreaSelectionWindow areaSelectionWindow;
    private final TrendWindow trendWindow;
    private final HeatMapWindow heatMapWindow;
    private final ComparisonWindow comparisonWindow;

    /**
     * Main display for the statistical information.
     */
    public StatisticsPanel(String name, Display display, DataHandler dataHandler) {
        super(name, display, dataHandler);
        filtersColumn = new FiltersColumn(this::statCardQuery);

        areaSelectionWindow = new AreaSelectionWindow(display, queryBuilder, this::invalidQueryAlert);
        addDisplayBar(areaSelectionWindow);

        trendWindow = new TrendWindow();
        addDisplayBar(trendWindow);

        heatMapWindow = new HeatMapWindow(display);
        addDisplayBar(heatMapWindow);

        comparisonWindow = new ComparisonWindow();
        addDisplayBar(comparisonWindow);

        setCenter(areaSelectionWindow.view());
        setRight(filtersColumn.view());
    }

    /**
     * Display bar has a heading and four buttons for navigating between
     * different windows. Sits at the top of every window.
     *
     * @return UI element.
     */
    private VBox buildDisplayBar() {
        VBox displayBar = new VBox();
        displayBar.setAlignment(Pos.CENTER);
        displayBar.setSpacing(10);
        displayBar.setPadding(new Insets(15));

        Label heading = new Label("Statistics");
        heading.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        HBox buttons = new HBox();
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(10);

        Button areaButton = new Button("Area Selection");
        areaButton.setOnAction(this::goToAreaSelection);
        Button trendButton = new Button("Trend over time");
        trendButton.setOnAction(this::trendsQuery);
        Button heatButton = new Button("Heat map");
        heatButton.setOnAction(this::heatMapQuery);
        Button comparisonButton = new Button("Year comparison");
        comparisonButton.setOnAction(this::comparisonsQuery);

        buttons.getChildren().addAll(areaButton, trendButton, heatButton, comparisonButton);
        displayBar.getChildren().addAll(heading, buttons);
        return displayBar;
    }

    private void addDisplayBar(StatisticsPanelWindow window) {
        VBox displayBar = buildDisplayBar();
        if (window instanceof AreaSelectionWindow) {
            ((VBox) window.view().getTop()).getChildren().addFirst(displayBar);
        } else {
            displayBar.setAlignment(Pos.CENTER);
            displayBar.setPadding(new Insets(15));
            String TILE_STYLE = "-fx-border-color: #cccccc; -fx-border-radius: 6; -fx-background-radius: 6; -fx-background-color: #f9f9f9;";
            displayBar.setStyle(TILE_STYLE);
            window.view().setTop(displayBar);
        }
    }

    private void goToAreaSelection(ActionEvent event) {
        this.setCenter(areaSelectionWindow.view());
    }

    /**
     * Processes a query if it is valid.
     * Returns a list of filtered datasets, or null if the query is invalid.
     *
     * @return Query data, or null.
     */
    private List<DataSet> processQuery() {
        Optional<QueryError> error = queryBuilder.validate();
        if (error.isPresent()) {
            invalidQueryAlert(error.get());
            return null;
        }
        Query query = queryBuilder.build();
        return DataFilter.processQueryData(query, dataHandler.getPollutantRegistry());
    }

    private List<List<DataSet>> processSelectedPollutants() {
        queryBuilder.startYear(filtersColumn.getStartYear());
        queryBuilder.endYear(filtersColumn.getEndYear());
        List<String> selectedPollutants = filtersColumn.getSelectedPollutants();
        if (selectedPollutants.isEmpty()) {
            invalidQueryAlert(QueryError.NO_POLLUTANT);
            return null;
        }
        List<List<DataSet>> queryData = new ArrayList<>();
        for (String pollutant : selectedPollutants) {
            queryBuilder.pollutant(pollutant);
            List<DataSet> pollutantQueryData = processQuery();
            if (pollutantQueryData != null) {
                queryData.add(pollutantQueryData);
            } else {
                return null;
            }
        }
        return queryData;
    }

    /**
     * Processes a query, updates and then displays them in the filters column.
     */
    private void statCardQuery() {
        List<List<DataSet>> queryData = processSelectedPollutants();
        if (queryData != null) {
            filtersColumn.update(queryData);
        }
    }

    /**
     * Processes a query, updates and then views the trendWindow with the results.
     *
     * @param event User has requested a stat card update.
     */
    private void trendsQuery(ActionEvent event) {
        List<List<DataSet>> queryData = processSelectedPollutants();
        if (queryData != null) {
            trendWindow.update(queryData);
            this.setCenter(trendWindow.view());
        }
    }

    /**
     * Processes a query, updates and then views the heatMapWindow with the results.
     *
     * @param event User has requested a stat card update.
     */
    private void heatMapQuery(ActionEvent event) {
        List<List<DataSet>> queryData = processSelectedPollutants();
        if (queryData != null) {
            heatMapWindow.update(queryData);
            this.setCenter(heatMapWindow.view());
        }
    }

    /**
     * Processes a query, updates and then views the comparisonWindow with the results.
     *
     * @param event User has requested a stat card update.
     */
    private void comparisonsQuery(ActionEvent event) {
        List<List<DataSet>> queryData = processSelectedPollutants();
        if (queryData != null) {
            comparisonWindow.update(queryData);
            this.setCenter(comparisonWindow.view());
        }
    }

    /**
     * Alerts the user when they have attempted to process an invalid query.
     */
    private void invalidQueryAlert(QueryError error) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Invalid Query");
        alert.setHeaderText("Please check your inputs");
        alert.setContentText(error.getMessage());
        alert.showAndWait();
    }
}
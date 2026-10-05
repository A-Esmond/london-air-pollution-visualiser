package panels;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import model.DataSet;
import util.StatisticsPanelCalculator;
import util.Tuple;
import java.util.ArrayList;
import java.util.List;

/**
 * UI element in the statistics panel that the user inputs filters into and
 * has a stat card display for data.
 *
 * @author Esmond Atiemo
 * @version 2026.10.01
 */
public class FiltersColumn {
    private VBox node;
    private Label currentPollutantLabel;
    private Button nextButton;
    private final Runnable statCardQuery;
    private ComboBox<String> startYearDropdown;
    private ComboBox<String> endYearDropdown;
    private MenuButton pollutantsDropdown;
    private Label averageLevelLabel;
    private Label peakLevelLabel;
    private Label peakLocationLabel;
    private final List<String> selectedPollutants;
    private int currentPollutantIndex;
    private final List<String> averageLevels;
    private final List<String> peakLevels;
    private final List<String> peakLocations;
    private final List<String> displayedPollutants;     // pollutants the shown results belong to


    public FiltersColumn(Runnable statCardQuery) {
        build();
        this.statCardQuery = statCardQuery;
        selectedPollutants = new ArrayList<>();
        averageLevels = new ArrayList<>();
        peakLevels = new ArrayList<>();
        peakLocations = new ArrayList<>();
        displayedPollutants = new ArrayList<>();
    }

    /**
     * Filters column containing dropdowns, stat cards, and the update button.
     * Lives on the right node of the panel.
     */
    private void build() {
        VBox filtersColumn = new VBox();
        filtersColumn.setAlignment(Pos.TOP_CENTER);
        filtersColumn.setSpacing(10);
        filtersColumn.setPadding(new Insets(15));

        Label heading = new Label("Filters");
        heading.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        buildStartYearDropdown();
        buildEndYearDropdown();
        buildPollutantsDropdown();

        Label subHeading = new Label("At a glance...");

        currentPollutantLabel = new Label();
        currentPollutantLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        currentPollutantLabel.setVisible(false);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        nextButton = new Button("Next");
        nextButton.setVisible(false);
        nextButton.setOnAction(this::cycleStats);

        Button updateButton = new Button("Update Stats");
        updateButton.setOnAction(e -> statCardQuery.run());

        filtersColumn.getChildren().addAll(
                heading,
                startYearDropdown,
                endYearDropdown,
                pollutantsDropdown,
                subHeading,
                currentPollutantLabel,
                buildStatCard("Average level", averageLevelLabel = new Label("—"), "μg/m³"),
                buildStatCard("Peak level", peakLevelLabel = new Label("—"), "μg/m³"),
                buildStatCard("Peak location", peakLocationLabel = new Label("—"), "(easting, northing)"),
                spacer,
                nextButton,
                updateButton
        );
        node = filtersColumn;
    }

    private void buildStartYearDropdown() {
        startYearDropdown = new ComboBox<>();
        startYearDropdown.setValue("Start year:");
        startYearDropdown.getItems().addAll("2018", "2019", "2020", "2021", "2022", "2023");
        startYearDropdown.setEditable(false);
    }

    private void buildEndYearDropdown() {
        endYearDropdown = new ComboBox<>();
        endYearDropdown.setValue("End year:");
        endYearDropdown.getItems().addAll("2018", "2019", "2020", "2021", "2022", "2023");
        endYearDropdown.setEditable(false);
    }

    private void buildPollutantsDropdown() {
        MenuButton dropdown = new MenuButton("Pollutants");
        for (String pollutant : List.of("NO2", "PM10", "PM2.5")) {
            CheckBox checkBox = new CheckBox(pollutant);
            checkBox.setOnAction(e -> updatePollutantInput(checkBox));
            dropdown.getItems().add(new CustomMenuItem(checkBox, false));
        }
        this.pollutantsDropdown = dropdown;
    }

    /**
     * Builds a stat card with a title, value label, and units label.
     *
     * @param title      The card heading.
     * @param valueLabel The label to display the value in.
     * @param units      The units of the measurement.
     * @return UI element.
     */
    private VBox buildStatCard(String title, Label valueLabel, String units) {
        VBox statCard = new VBox();
        statCard.setSpacing(4);
        statCard.setPadding(new Insets(10));
        String TILE_STYLE = "-fx-border-color: #cccccc; -fx-border-radius: 6; -fx-background-radius: 6; -fx-background-color: #f9f9f9;";
        statCard.setStyle(TILE_STYLE);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #888888;");

        Label unitsLabel = new Label(units);
        unitsLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #888888;");

        valueLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        statCard.getChildren().addAll(titleLabel, valueLabel, unitsLabel);
        return statCard;
    }

    /**
     * Cycles the stat cards to display the next pollutant's statistics.
     *
     * @param event User has clicked the next button.
     */
    private void cycleStats(ActionEvent event) {
        if (averageLevels.isEmpty()) {
            return;
        }
        currentPollutantIndex = (currentPollutantIndex + 1) % averageLevels.size();
        currentPollutantLabel.setText(displayedPollutants.get(currentPollutantIndex));
        updateStatLabels();
    }

    /**
     * Updates the stat card labels to reflect the current pollutant.
     */
    private void updateStatLabels() {
        averageLevelLabel.setText(averageLevels.get(currentPollutantIndex));
        peakLevelLabel.setText(peakLevels.get(currentPollutantIndex));
        peakLocationLabel.setText(peakLocations.get(currentPollutantIndex));
    }

    /**
     * Updates the selectedPollutants list when a pollutant checkbox is toggled.
     *
     * @param checkBox The checkbox that was toggled.
     */
    private void updatePollutantInput(CheckBox checkBox) {
        if (checkBox.isSelected()) {
            selectedPollutants.add(checkBox.getText());
        } else {
            selectedPollutants.remove(checkBox.getText());
        }
    }

    public void update(List<List<DataSet>> queryData) {
        ArrayList<String> averageLevels = new ArrayList<>();
        ArrayList<String> peakLevels = new ArrayList<>();
        ArrayList<String> peakLocations = new ArrayList<>();

        for (List<DataSet> pollutantQueryData : queryData) {
            double averageValue = StatisticsPanelCalculator.calculateAverage(pollutantQueryData);
            double peakValue = StatisticsPanelCalculator.calculatePeakLevel(pollutantQueryData);
            Tuple<Integer, Integer> peakLocationValue = StatisticsPanelCalculator.calculatePeakLocation(pollutantQueryData);

            if (peakLocationValue == null) {
                // The selected area contains no grid points with data.
                averageLevels.add("No data");
                peakLevels.add("No data");
                peakLocations.add("—");
                continue;
            }
            averageLevels.add(String.format("%.2f", averageValue));
            peakLevels.add(String.format("%.2f", peakValue));
            peakLocations.add("(" + peakLocationValue.val1() + ", " + peakLocationValue.val2() + ")");
        }
        displayResults(averageLevels, peakLevels, peakLocations);
    }

    public void displayResults(List<String> averageLevels, List<String> peakLevels, List<String> peakLocations) {
        this.averageLevels.clear();
        this.peakLevels.clear();
        this.peakLocations.clear();

        this.averageLevels.addAll(averageLevels);
        this.peakLevels.addAll(peakLevels);
        this.peakLocations.addAll(peakLocations);

        this.displayedPollutants.clear();
        this.displayedPollutants.addAll(selectedPollutants);

        currentPollutantIndex = 0;

        boolean hasResults = !averageLevels.isEmpty();
        currentPollutantLabel.setVisible(hasResults);
        nextButton.setVisible(averageLevels.size() > 1);

        if (hasResults) {
            currentPollutantLabel.setText(displayedPollutants.getFirst());
            updateStatLabels();
        }
    }

    public List<String> getSelectedPollutants() {
        return List.copyOf(selectedPollutants);
    }

    public String getStartYear() {
        return startYearDropdown.getValue();
    }

    public String getEndYear() {
        return endYearDropdown.getValue();
    }

    public VBox view() {
        return node;
    }
}

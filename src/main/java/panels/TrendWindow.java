package panels;

import javafx.geometry.Insets;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import model.DataSet;
import util.StatisticsPanelCalculator;

import java.util.ArrayList;
import java.util.List;

/**
 * Window used for graphing average pollution level by year.
 *
 * @author Esmond Atiemo
 * @version 2026.10.01
 */
public class TrendWindow extends StatisticsPanelWindow {
    private LineChart<String, Number> lineChart;

    public TrendWindow() {
        super(new BorderPane());
        build();
    }

    @Override
    public void build() {
        viewPane.setPadding(new Insets(15));

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setLegendVisible(true);
        String TILE_STYLE = "-fx-border-color: #cccccc; -fx-border-radius: 6; -fx-background-radius: 6; -fx-background-color: #f9f9f9;";
        lineChart.setStyle(TILE_STYLE);
        lineChart.setTitle("Average pollution over time");
        xAxis.setLabel("Year");
        yAxis.setLabel("Concentration (μg/m³)");

        StackPane lineChartDisplay = new StackPane(lineChart);
        lineChartDisplay.setPadding(new Insets(10, 0, 0, 0));

        viewPane.setCenter(lineChartDisplay);
    }

    @Override
    public void update(List<List<DataSet>> queryData) {
        List<XYChart.Series<String, Number>> seriesList = new ArrayList<>();
        for (List<DataSet> pollutantQueryData : queryData) {
            if (pollutantQueryData.isEmpty()) {
                continue;
            }
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(pollutantQueryData.getFirst().getPollutant().toUpperCase());
            for (DataSet dataSet : pollutantQueryData) {
                series.getData().add(new XYChart.Data<>(dataSet.getYear(), StatisticsPanelCalculator.calculateAverage(dataSet)));
            }
            seriesList.add(series);
        }
        lineChart.getData().setAll(seriesList);
    }

    @Override
    public BorderPane view() {
        return viewPane;
    }
}
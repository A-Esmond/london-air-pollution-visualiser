package panels;

import javafx.geometry.Insets;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import model.DataSet;
import util.StatisticsPanelCalculator;
import java.util.ArrayList;
import java.util.List;

/**
 * Window used for graphing peak pollution level by year.
 *
 * @author Esmond Atiemo
 * @version 2026.10.01
 */
public class ComparisonWindow extends StatisticsPanelWindow {
    private BarChart<String, Number> barChart;

    public ComparisonWindow() {
        super(new BorderPane());
        build();
    }

    @Override
    public void build() {
        viewPane.setPadding(new Insets(15));

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        barChart = new BarChart<>(xAxis, yAxis);
        barChart.setLegendVisible(true);
        String TILE_STYLE = "-fx-border-color: #cccccc; -fx-border-radius: 6; -fx-background-radius: 6; -fx-background-color: #f9f9f9;";
        barChart.setStyle(TILE_STYLE);
        barChart.setTitle("Peak pollution level");
        xAxis.setLabel("Year");
        yAxis.setLabel("Concentration (μg/m³)");

        StackPane barChartDisplay = new StackPane(barChart);
        barChartDisplay.setPadding(new Insets(10, 0, 0, 0));

        viewPane.setCenter(barChartDisplay);
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
                // One bar per year: the highest single grid reading in the selected area.
                series.getData().add(new XYChart.Data<>(dataSet.getYear(), StatisticsPanelCalculator.calculatePeakLevel(dataSet)));
            }
            seriesList.add(series);
        }
        barChart.getData().setAll(seriesList);
    }

    @Override
    public BorderPane view() {
        return viewPane;
    }
}

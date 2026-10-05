package util;

import model.DataPoint;
import model.DataSet;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Esmond Atiemo
 */
class StatisticsPanelCalculatorTest {

    private static DataSet dataSet(String year, double... values) {
        DataSet dataSet = new DataSet("no2", year, "annual mean", "ug m-3");
        for (int i = 0; i < values.length; i++) {
            dataSet.addData(new DataPoint(i, 520000 + i * 1000, 180000, values[i]));
        }
        return dataSet;
    }

    @Test
    void averageOfOneDataset() {
        assertEquals(20.0, StatisticsPanelCalculator.calculateAverage(dataSet("2023", 10, 20, 30)), 1e-9);
    }

    @Test
    void averageAcrossYearsUsesEveryPoint() {
        List<DataSet> years = List.of(dataSet("2022", 10, 20), dataSet("2023", 30, 40));
        assertEquals(25.0, StatisticsPanelCalculator.calculateAverage(years), 1e-9);
    }

    @Test
    void peakOfOneDatasetIsItsLargestValue() {
        assertEquals(30.0, StatisticsPanelCalculator.calculatePeakLevel(dataSet("2023", 10, 30, 20)), 1e-9);
    }

    @Test
    void peakAcrossYearsIsTheLargestOfAll() {
        List<DataSet> years = List.of(dataSet("2022", 10, 50), dataSet("2023", 30, 40));
        assertEquals(50.0, StatisticsPanelCalculator.calculatePeakLevel(years), 1e-9);
    }

    @Test
    void peakIsHigherThanAverageForVariedData() {
        // Guards the old bug where the "peak" chart plotted averages.
        DataSet year = dataSet("2023", 10, 20, 60);
        assertTrue(StatisticsPanelCalculator.calculatePeakLevel(year)
                > StatisticsPanelCalculator.calculateAverage(year));
    }

    @Test
    void peakLocationIsWhereTheMaximumIs() {
        Tuple<Integer, Integer> location = StatisticsPanelCalculator.calculatePeakLocation(List.of(dataSet("2023", 5, 99, 7)));
        assertEquals(new Tuple<>(521000, 180000), location);
    }

    @Test
    void emptyDataGivesSentinelValues() {
        DataSet empty = dataSet("2023");
        assertEquals(-1, StatisticsPanelCalculator.calculateAverage(empty));
        assertEquals(-1, StatisticsPanelCalculator.calculatePeakLevel(empty));
        assertNull(StatisticsPanelCalculator.calculatePeakLocation(List.of(empty)));
    }

    @Test
    void normaliseMapsMinToZeroAndMaxToOne() {
        assertEquals(0.0, StatisticsPanelCalculator.normalise(10, 10, 30), 1e-9);
        assertEquals(0.5, StatisticsPanelCalculator.normalise(20, 10, 30), 1e-9);
        assertEquals(1.0, StatisticsPanelCalculator.normalise(30, 10, 30), 1e-9);
    }

    @Test
    void normaliseDoesNotDivideByZero() {
        double result = StatisticsPanelCalculator.normalise(12, 12, 12);
        assertFalse(Double.isNaN(result));
        assertEquals(0.5, result, 1e-9);
    }
}

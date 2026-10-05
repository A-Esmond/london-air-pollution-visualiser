/**
 * @author P. P.
 * DataFilter filters raw air pollution data before it is displayed.
 * It removes data points that fall outside the London map boundaries,
 * and excludes any data points with missing values (indicated by -1.0).
 */
package model;

import src.resources.LondonMapData;
import util.Query;
import util.Tuple;

import java.util.List;

public class DataFilter {
    public static DataSet filterData(DataSet unfilteredData) {
        String pollutant = unfilteredData.getPollutant();
        String year = unfilteredData.getYear();
        String metric = unfilteredData.getMetric();
        String units = unfilteredData.getUnits();
        DataSet filteredData = new DataSet(pollutant, year, metric, units);
        for (DataPoint dp : unfilteredData.getData()) {
            if (!LondonMapData.isOnMap(dp.x(), dp.y())) {
                continue;
            }
            if (isMissingValue(dp)) {
                continue;
            }
            String[] value = {
                    String.valueOf(dp.gridCode()),
                    String.valueOf(dp.x()),
                    String.valueOf(dp.y()),
                    String.valueOf(dp.value())};
            filteredData.addData(value);
        }
        return filteredData;
    }

    /**
     * Filters data according to user input query.
     * Filters the set of all available datasets by pollutant and year.
     * Then creates new DataSet objects with filtered DataPoints.
     *
     * @param query Users input query.
     */
    public static List<DataSet> processQueryData(Query query, PollutantRegistry pollutantRegistry) {
        int startYear = Integer.parseInt(query.getStartYear());
        int endYear = Integer.parseInt(query.getEndYear());
        List<DataSet> res = pollutantRegistry.getPollutant(query.getPollutant(), startYear, endYear);

        if (res == null) {
            return res;
        }

        return res.stream()
                .map(DataFilter::filterData)
                .map(dataSet -> filterByArea(dataSet, query.getFirstAreaBound(), query.getSecondAreaBound()))
                .toList();
    }

    /**
     * Returns a new DataSet containing only the DataPoints within the boundary box
     * defined by firstBound and secondBound.
     * Order of bounds does not matter.
     *
     * @param dataSet     The dataset to filter.
     * @param firstBound  The first corner of the boundary (x, y).
     * @param secondBound The opposite corner of the boundary (x, y).
     */
    private static DataSet filterByArea(DataSet dataSet, Tuple<Integer, Integer> firstBound, Tuple<Integer, Integer> secondBound) {
        DataSet filtered = new DataSet(dataSet.getPollutant(), dataSet.getYear(), dataSet.getMetric(), dataSet.getUnits());

        List<DataPoint> filteredPoints = dataSet.getData().stream()
                .filter(dp ->
                        dp.x() >= Math.min(firstBound.val1(), secondBound.val1()) &&
                                dp.x() <= Math.max(firstBound.val1(), secondBound.val1()) &&
                                dp.y() >= Math.min(firstBound.val2(), secondBound.val2()) &&
                                dp.y() <= Math.max(firstBound.val2(), secondBound.val2()))
                .toList();

        filtered.addData(filteredPoints);
        return filtered;
    }

    private static boolean isMissingValue(DataPoint dp) {
        return dp.value() == -1.0;
    }
}

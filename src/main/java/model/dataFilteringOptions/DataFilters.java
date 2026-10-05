package model.dataFilteringOptions;

import model.DataPoint;
import model.DataSet;
import src.resources.LondonMapData;

import java.util.List;
import java.util.function.Predicate;

/**
 * Utility class providing reusable filtering operations for DataSet objects.
 *
 * Filters are implemented using functional programming, allowing flexible
 * composition and reuse. Each filter returns a new DataSet containing only
 * the filtered data points, leaving the original dataset unchanged.
 *
 * @author E. N.
 * @version 26/3/26
 */
public class DataFilters {

    /**
     * Creates a filtering option based on a given condition.
     * Applies the predicate to each DataPoint and returns a new filtered DataSet.
     *
     * @param condition
     * @return A DataFilteringOption based on the condition
     */
    public static DataFilteringOption byCondition(Predicate<DataPoint> condition) {
        return unfilteredDataSet -> {
            List<DataPoint> filteredData = unfilteredDataSet.getData()
                    .stream()
                    .filter(condition)
                    .toList();

            DataSet filteredDataset = new DataSet(
                    unfilteredDataSet.getPollutant(),
                    unfilteredDataSet.getYear(),
                    unfilteredDataSet.getMetric(),
                    unfilteredDataSet.getUnits()
            );

            filteredDataset.addData(filteredData);
            return filteredDataset;
        };
    }

    /**
     * Filters out data points that fall outside the map boundaries.
     *
     * @return A DataFilteringOption that keeps only points within the map
     */
    public static DataFilteringOption isOnMap() {
        return byCondition(dataPoint ->
                LondonMapData.isOnMap(dataPoint.x(), dataPoint.y())
        );
    }

    /**
     * Filters out invalid data points.
     * A value of -1 represents missing or invalid data.
     *
     * @return A DataFilteringOption that removes invalid data
     */
    public static DataFilteringOption isValidValue() {
        return byCondition(dataPoint -> dataPoint.value() != -1);
    }

    /**
     * Combines multiple filters into a single filtering operation.
     * Filters are applied sequentially in the order provided.
     *
     * @param filters
     * @return A combined DataFilteringOption
     */
    public static DataFilteringOption combine(DataFilteringOption... filters) {
        return dataSet -> {
            DataSet result = dataSet;
            for (DataFilteringOption f : filters) {
                result = f.filterData(result);
            }
            return result;
        };
    }
}
package model.dataFilteringOptions;

import model.DataSet;

/**
 * Functional interface representing a filtering operation on a DataSet.
 *
 * Implementations take a DataSet and return a new DataSet containing
 * only the data points that satisfy the filtering criteria.
 * Designed to support functional composition of filters.
 *
 * @author E. N.
 * @version 26/3/26
 */
@FunctionalInterface
public interface DataFilteringOption {

    /**
     * Applies a filtering operation to the given dataset.
     *
     * @param dataSet
     * @return A filtered DataSet
     */
    DataSet filterData(DataSet dataSet);
}
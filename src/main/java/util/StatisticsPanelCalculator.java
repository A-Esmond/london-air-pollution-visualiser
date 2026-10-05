package util;

import model.DataPoint;
import model.DataSet;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalDouble;

/**
 * Utility class for calculating summary statistics from a filtered list of datasets.
 * Input datasets are assumed to be filtered by pollutant, year range, area boundary,
 * and have MISSING data points removed.
 *
 * @author Esmond Atiemo
 * @version 2026.10.01
 */
public class StatisticsPanelCalculator {

	/**
	 * Calculates the average pollution level across all data points in all datasets.
	 *
	 * @param datasets Filtered list of datasets.
	 * @return Average pollution level, or -1 if no data points exist.
	 */
	public static double calculateAverage(List<DataSet> datasets) {
		OptionalDouble average = datasets.stream()
				.flatMap(ds -> ds.getData().stream())
				.mapToDouble(DataPoint::value)
				.average();
		return average.orElse(-1);
	}


	/**
	 * Calculates the average pollution level across all data points in a dataset.
	 *
	 * @param dataset Filtered dataset.
	 * @return Average pollution level, or -1 if no data points exist.
	 */
	public static double calculateAverage(DataSet dataset) {
		OptionalDouble average = dataset.getData().stream()
				.mapToDouble(DataPoint::value)
				.average();
		return average.orElse(-1);
	}

	/**
	 * Calculates the peak pollution level across all data points in all datasets.
	 *
	 * @param datasets Filtered list of datasets.
	 * @return Highest pollution value recorded, or -1 if no data points exist.
	 */
	public static double calculatePeakLevel(List<DataSet> datasets) {
		return datasets.stream()
				.flatMap(ds -> ds.getData().stream())
				.mapToDouble(DataPoint::value)
				.max()
				.orElse(-1);
	}

	/**
	 * Calculates the peak pollution level across all data points in a single dataset
	 * (for example, one pollutant in one year).
	 *
	 * @param dataset Filtered dataset.
	 * @return Highest pollution value recorded, or -1 if no data points exist.
	 */
	public static double calculatePeakLevel(DataSet dataset) {
		return dataset.getData().stream()
				.mapToDouble(DataPoint::value)
				.max()
				.orElse(-1);
	}

	/**
	 * Rescales a value to the range 0.0 - 1.0, where min becomes 0 and max becomes 1.
	 * Used to colour the heat map. If every value is the same (max == min) there is
	 * no range to scale by, so 0.5 is returned instead of dividing by zero.
	 *
	 * @param value The value to rescale.
	 * @param min   The smallest value in the data.
	 * @param max   The largest value in the data.
	 * @return The value as a fraction between min and max.
	 */
	public static double normalise(double value, double min, double max) {
		if (max == min) {
			return 0.5;
		}
		return (value - min) / (max - min);
	}

	/**
	 * Finds the location of the data point with the highest pollution concentration
	 * across all datasets.
	 *
	 * @param datasets Filtered list of datasets.
	 * @return The location of the data point as a tuple, or null if there are no data points.
	 */
	public static Tuple<Integer, Integer> calculatePeakLocation(List<DataSet> datasets) {
		return datasets.stream()
				.flatMap(ds -> ds.getData().stream())
				.max(Comparator.comparingDouble(DataPoint::value))
				.map(dataPoint -> new Tuple<>(dataPoint.x(), dataPoint.y()))
				.orElse(null);
	}
}
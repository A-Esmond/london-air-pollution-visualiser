package util;

import model.DataPoint;
import model.DataSet;

import java.util.Comparator;

/**
 * Utility class for calculating distances between coordinates.
 * Used primarily to identify which data point on the map is nearest
 * to the user's cursor position.
 *
 * @author E. N.
 * @version 26/3/26
 */
public class DistanceCalculator {

    /**
     * Finds the closest data point in a dataset to a given coordinate.
     * Comparison is done in real-world easting/northing space.
     *
     * @param dataSet
     * @param coord
     * @return The nearest Datapoint to the coordinate
     */
    public static DataPoint getClosestDatapoint(DataSet dataSet, Tuple<Integer, Integer> coord) {
        return dataSet.getData()
                .stream()
                .min(Comparator.comparingDouble(dataPoint ->
                        distanceSquared(coord, new Tuple<>(dataPoint.x(), dataPoint.y()))
                ))
                .orElseThrow(() -> new IllegalArgumentException("DataSet is empty: " + dataSet));
    }

    /**
     * Computes the squared distance between two coordinates.
     * Squared distance is used instead of true distance to avoid the
     * computational cost of a square root, which adds no value when
     * only relative comparisons are needed.
     *
     * @param mouseCoord
     * @param dataPointCoord
     * @return The squared distance between the two coordinates.
     */
    private static double distanceSquared(Tuple<Integer, Integer> mouseCoord, Tuple<Integer, Integer> dataPointCoord) {
        Integer deltaX = mouseCoord.val1() - dataPointCoord.val1();
        Integer deltaY = mouseCoord.val2() - dataPointCoord.val2();
        return deltaX * deltaX + deltaY * deltaY;
    }
}
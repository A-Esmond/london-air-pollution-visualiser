package util;

/**
 * A generic immutable container for a pair of related values.
 * Useful for returning or passing two associated values together
 * without creating a dedicated class.
 *
 * <p>Examples of use in this project include representing coordinate
 * pairs (easting, northing) and min/max ranges.</p>
 *
 * @param <T1> The type of the first value.
 * @param <T2> The type of the second value.
 * @param val1 The first value.
 * @param val2 The second value.
 *
 * @author E. N.
 * @version 26/3/26
 */
public record Tuple<T1, T2>(T1 val1, T2 val2) {
}
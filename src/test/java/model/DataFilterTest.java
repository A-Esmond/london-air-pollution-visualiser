package model;

import core.DataHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import util.Query;
import util.Tuple;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the query pipeline against the real DEFRA data shipped with the app.
 *
 * @author Esmond Atiemo
 */
class DataFilterTest {

    private static PollutantRegistry registry;

    @BeforeAll
    static void loadData() {
        registry = new DataHandler().getPollutantRegistry();
    }

    private static Query query(int startYear, int endYear, Tuple<Integer, Integer> a, Tuple<Integer, Integer> b) {
        return new Query.Builder().startYear(String.valueOf(startYear)).endYear(String.valueOf(endYear))
                .pollutant("NO2").firstCoords(a).secondCoords(b).build();
    }

    @Test
    void returnsOneDatasetPerYearInRange() {
        List<DataSet> result = DataFilter.processQueryData(
                query(2019, 2021, new Tuple<>(520000, 175000), new Tuple<>(530000, 185000)), registry);
        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(ds -> {
            int year = Integer.parseInt(ds.getYear());
            return year >= 2019 && year <= 2021;
        }));
    }

    @Test
    void keepsOnlyPointsInsideTheBox() {
        List<DataSet> result = DataFilter.processQueryData(
                query(2023, 2023, new Tuple<>(520000, 175000), new Tuple<>(530000, 185000)), registry);
        List<DataPoint> points = result.getFirst().getData();
        assertFalse(points.isEmpty());
        assertTrue(points.stream().allMatch(p ->
                p.x() >= 520000 && p.x() <= 530000 && p.y() >= 175000 && p.y() <= 185000));
    }

    @Test
    void cornerOrderDoesNotMatter() {
        Tuple<Integer, Integer> a = new Tuple<>(520000, 175000);
        Tuple<Integer, Integer> b = new Tuple<>(530000, 185000);
        int forwards = DataFilter.processQueryData(query(2023, 2023, a, b), registry).getFirst().getData().size();
        int backwards = DataFilter.processQueryData(query(2023, 2023, b, a), registry).getFirst().getData().size();
        assertEquals(forwards, backwards);
    }

    @Test
    void tenByTenKilometreBoxHoldsAboutOneHundredGridSquares() {
        // DEFRA data is on a 1 km grid, so a 10 km x 10 km box should hold ~100 points.
        int count = DataFilter.processQueryData(
                query(2023, 2023, new Tuple<>(520000, 175000), new Tuple<>(530000, 185000)), registry)
                .getFirst().getData().size();
        assertTrue(count >= 90 && count <= 121, "got " + count);
    }

    @Test
    void boxOutsideLondonIsEmpty() {
        List<DataSet> result = DataFilter.processQueryData(
                query(2023, 2023, new Tuple<>(400000, 100000), new Tuple<>(401000, 101000)), registry);
        assertTrue(result.getFirst().getData().isEmpty());
    }
}

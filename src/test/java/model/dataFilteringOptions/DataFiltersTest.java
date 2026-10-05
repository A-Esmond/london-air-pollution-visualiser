package model.dataFilteringOptions;

import core.DataHandler;
import model.DataSet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for verifying the behaviour of DataFilters.
 *
 * Ensures that filtering operations correctly reduce or maintain
 * dataset size based on the applied conditions.
 *
 * @author E. N.
 * @version 26/3/26
 */
public class DataFiltersTest {

    private DataSet originalDataSet;

    /**
     * Sets up the test fixture before each test.
     * Loads a fresh dataset to ensure tests are independent.
     */
    @BeforeEach
    public void setUp() {
        DataHandler dataHandler = new DataHandler();

        originalDataSet = dataHandler.getPollutantRegistry().getPollutant("pm2.5").getFirst();
        assertNotNull(originalDataSet);
    }

    /**
     * Cleans up after each test.
     */
    @AfterEach
    public void tearDown() {
        originalDataSet = null;
    }

    /**
     * Tests that the isOnMap filter removes data points
     * that fall outside the map boundaries.
     */
    @Test
    public void isOnMapTest() {
        int originalSize = originalDataSet.getData().size();

        DataSet filtered = DataFilters.isOnMap().filterData(originalDataSet);
        int filteredSize = filtered.getData().size();

        assertTrue(filteredSize <= originalSize);
        assertTrue(filteredSize > 0);
    }

    /**
     * Tests that the isValidValue filter removes invalid data points.
     */
    @Test
    public void isValidTest() {
        int originalSize = originalDataSet.getData().size();

        DataSet filtered = DataFilters.isValidValue().filterData(originalDataSet);
        int filteredSize = filtered.getData().size();

        assertTrue(filteredSize <= originalSize);
        assertTrue(filteredSize > 0);
    }
}
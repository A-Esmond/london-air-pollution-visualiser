package model;

import core.DataHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Esmond Atiemo
 */
class PollutantRegistryTest {

    private static PollutantRegistry registry;

    @BeforeAll
    static void loadData() {
        registry = new DataHandler().getPollutantRegistry();
    }

    @Test
    void loadsAllThreePollutants() {
        assertEquals(Set.of("no2", "pm10", "pm2.5"), registry.getPollutantNames());
    }

    @Test
    void coversSixYearsForEachPollutant() {
        for (String pollutant : registry.getPollutantNames()) {
            assertEquals(6, registry.getPollutant(pollutant).size(), pollutant);
            assertEquals(2018, registry.getPollutantMinYear(pollutant));
            assertEquals(2023, registry.getPollutantMaxYear(pollutant));
        }
        assertEquals("2018-2023", registry.getTotalYears());
    }

    @Test
    void yearRangeLookupIsInclusive() {
        assertEquals(2, registry.getPollutant("NO2", 2022, 2023).size());
    }

    @Test
    void loadedDataIsOnTheMapAndValid() {
        for (DataSet dataSet : registry.getAllDatasets()) {
            assertFalse(dataSet.getData().isEmpty());
            assertTrue(dataSet.getData().stream().allMatch(p -> p.value() >= 0));
        }
    }

    @Test
    void lookForDatasetFindsTheRightYear() {
        assertEquals("2020", registry.lookForDataset("pm10", "2020").getYear());
        assertNull(registry.lookForDataset("pm10", "1999"));
        assertThrows(IllegalArgumentException.class, () -> registry.lookForDataset("ozone", "2020"));
    }
}

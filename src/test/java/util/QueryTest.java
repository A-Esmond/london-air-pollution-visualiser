package util;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Esmond Atiemo
 */
class QueryTest {

    private Query.Builder validBuilder() {
        return new Query.Builder()
                .startYear("2019").endYear("2022").pollutant("NO2")
                .firstCoords(new Tuple<>(520000, 175000))
                .secondCoords(new Tuple<>(530000, 185000));
    }

    @Test
    void completeQueryIsValid() {
        assertEquals(Optional.empty(), validBuilder().validate());
    }

    @Test
    void placeholderYearIsRejected() {
        assertEquals(Optional.of(QueryError.MISSING_YEAR), validBuilder().startYear("Start year:").validate());
        assertEquals(Optional.of(QueryError.MISSING_YEAR), validBuilder().endYear(null).validate());
    }

    @Test
    void startAfterEndIsRejected() {
        assertEquals(Optional.of(QueryError.START_AFTER_END), validBuilder().startYear("2023").validate());
    }

    @Test
    void sameStartAndEndYearIsAllowed() {
        assertTrue(validBuilder().startYear("2022").endYear("2022").validate().isEmpty());
    }

    @Test
    void missingCornerIsRejected() {
        assertEquals(Optional.of(QueryError.MISSING_AREA), validBuilder().secondCoords(null).validate());
    }

    @Test
    void missingPollutantIsRejected() {
        assertEquals(Optional.of(QueryError.NO_POLLUTANT), validBuilder().pollutant(null).validate());
    }

    @Test
    void builtQueryKeepsItsInputs() {
        Query query = validBuilder().build();
        assertEquals("2019", query.getStartYear());
        assertEquals("2022", query.getEndYear());
        assertEquals("NO2", query.getPollutant());
        assertEquals(new Tuple<>(520000, 175000), query.getFirstAreaBound());
    }
}

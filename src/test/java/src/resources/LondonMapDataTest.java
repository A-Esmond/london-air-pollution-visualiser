package src.resources;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The test class LondonMapDataTest.
 *
 * @author E. N. & P. P.
 * @version 26/3/26
 */
public class LondonMapDataTest {
    /**
     * Default constructor for test class LondonMapDataTest
     */
    public LondonMapDataTest() {
    }

    /**
     * Sets up the test fixture.
     * <p>
     * Called before every test case method.
     */
    @BeforeEach
    public void setUp() {
    }

    /**
     * Tears down the test fixture.
     * <p>
     * Called after every test case method.
     */
    @AfterEach
    public void tearDown() {
    }

    @Test
    public void isOnMapTest() {
        assertTrue(LondonMapData.isOnMap(553297, 193305));
        assertFalse(LondonMapData.isOnMap(100000000, 100000000));
    }

    @Test
    public void TestLeftEdge() {
        assertEquals(510394.0, LondonMapData.mapToRealWidth(0));
    }

    @Test
    public void TestRightEdge() {
        assertEquals(553297.0, LondonMapData.mapToRealWidth(1781));
    }

    @Test
    public void TestTopEdge() {
        assertEquals(193305.0, LondonMapData.mapToRealHeight(0));
    }

    @Test
    public void TestBottomEdge() {
        assertEquals(168504.0, LondonMapData.mapToRealHeight(1100));
    }

    @Test
    public void TestMidHeight() {
        assertEquals(180904.5, LondonMapData.mapToRealHeight(550));
    }

}

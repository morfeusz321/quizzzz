package server.game.questions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuestionGeneratorUtilsTest {

    private QuestionGeneratorUtils utils;

    @BeforeEach
    public void setup() {

        this.utils = new QuestionGeneratorUtils();

    }

    @Test
    public void testCreateConsumptionStringNoPrefix() {

        String s = utils.createConsumptionString(4);
        assertEquals("4 Wh", s);

        s = utils.createConsumptionString(0);
        assertEquals("0 Wh", s);

        s = utils.createConsumptionString(36);
        assertEquals("36 Wh", s);

        s = utils.createConsumptionString(68);
        assertEquals("68 Wh", s);

        s = utils.createConsumptionString(215);
        assertEquals("215 Wh", s);

        s = utils.createConsumptionString(999);
        assertEquals("999 Wh", s);

    }

    @Test
    public void testCreateConsumptionStringPrefix() {

        String s = utils.createConsumptionString(1000);
        assertEquals("1.00 kWh", s);

        s = utils.createConsumptionString(1895);
        assertEquals("1.90 kWh", s);

        s = utils.createConsumptionString(3750);
        assertEquals("3.75 kWh", s);

        s = utils.createConsumptionString(31856);
        assertEquals("31.86 kWh", s);

        s = utils.createConsumptionString(788190);
        assertEquals("788.19 kWh", s);

        s = utils.createConsumptionString(4895013);
        assertEquals("4.90 MWh", s);

        s = utils.createConsumptionString(3750661);
        assertEquals("3.75 MWh", s);

        s = utils.createConsumptionString(3456013524L);
        assertEquals("3.46 GWh", s);

        s = utils.createConsumptionString(6910838019923L);
        assertEquals("6.91 TWh", s);

        assertThrows(IllegalArgumentException.class, () -> utils.createConsumptionString(-50, 1));
        assertThrows(IllegalArgumentException.class, () -> utils.createConsumptionString(-50000, 1));
        assertThrows(IllegalArgumentException.class, () -> utils.createConsumptionString(-50, -1));
        assertThrows(IllegalArgumentException.class, () -> utils.createConsumptionString(-50000, -1));

    }

    @Test
    public void testCreateConsumptionStringDifferentAmountOfDecimals() {

        String s = utils.createConsumptionString(1000, 3);
        assertEquals("1.000 kWh", s);

        s = utils.createConsumptionString(31856, 1);
        assertEquals("31.9 kWh", s);

        s = utils.createConsumptionString(788190, 2);
        assertEquals("788.19 kWh", s);

        s = utils.createConsumptionString(4895013, 5);
        assertEquals("4.89501 MWh", s);

        s = utils.createConsumptionString(3750661, 0);
        assertEquals("4 MWh", s);

        assertThrows(IllegalArgumentException.class, () -> utils.createConsumptionString(123456, -1));

    }

    @Test
    public void testGetSIPrefix() {

        String s = utils.getSIPrefix(0);
        assertEquals("", s);

        s = utils.getSIPrefix(1);
        assertEquals("k", s);

        s = utils.getSIPrefix(2);
        assertEquals("M", s);

        s = utils.getSIPrefix(3);
        assertEquals("G", s);

        s = utils.getSIPrefix(4);
        assertEquals("T", s);

        assertThrows(IllegalArgumentException.class, () -> utils.getSIPrefix(5));

    }

    @Test
    public void testUpperLowerBoundSmall(){
        // Tests the utility method getLowerUpperBoundSmall().

        long[] bounds = utils.getLowerUpperBoundSmall(10);
        assertTrue(bounds[0] == 0 && bounds[1] == 500);
        bounds = utils.getLowerUpperBoundSmall(800);
        assertTrue(bounds[0] == 500 && bounds[1] == 1000);
        bounds = utils.getLowerUpperBoundSmall(1200);
        assertTrue(bounds[0] == 1000 && bounds[1] == 10000);
        bounds = utils.getLowerUpperBoundSmall(18000);
        assertTrue(bounds[0] == 10000 && bounds[1] == 10000000L);
        bounds = utils.getLowerUpperBoundSmall(150000L);
        assertTrue(bounds[0] == 100000 && bounds[1] == 1000000000L);
        bounds = utils.getLowerUpperBoundSmall(12300000L);
        assertTrue(bounds[0] == 10000000L && bounds[1] == 100000000000L);
        bounds = utils.getLowerUpperBoundSmall(18970000000L);
        assertTrue(bounds[0] == 1000000000L && bounds[1] == 100000000000L);
        bounds = utils.getLowerUpperBoundSmall(112000000000L);
        assertTrue(bounds[0] == 100000000000L && bounds[1] == Long.MAX_VALUE);

    }

}
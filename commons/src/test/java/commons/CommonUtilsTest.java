package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class CommonUtilsTest {

    private Random random;
    private CommonUtils utils;

    @BeforeEach
    public void setup() {

        this.random = new NotSoRandom();
        this.utils = new CommonUtils();

    }

    @Test
    void prependingZeroTestPrepend(){
        assertEquals("01", utils.addPrependingZero(1));
    }

    @Test
    void prependingZeroTestNoPrepend(){
        assertEquals("10", utils.addPrependingZero(10));
        assertEquals("-3", utils.addPrependingZero(-3));
    }

    @RepeatedTest(20)
    void randomIntInRangeTestBothNegative(){
        int r = utils.randomIntInRange(-50, -10, random);
        assertEquals(((NotSoRandom) random).getLastReturned() - 50, r);
    }

    @RepeatedTest(20)
    void randomIntInRangeTestBothPositive(){
        int r = utils.randomIntInRange(10, 50, random);
        assertEquals(((NotSoRandom) random).getLastReturned() + 10, r);
    }

    @RepeatedTest(20)
    void randomIntInRangeTestMixedSignBounds() {
        int r = utils.randomIntInRange(-10, 50, random);
        assertEquals(((NotSoRandom) random).getLastReturned() - 10, r);
    }

    @Test
    void randomIntInRangeTestIllegalBounds() {
        assertThrows(IllegalArgumentException.class, () -> utils.randomIntInRange(10, 0, random));
    }

    @Test
    void randomIntInRangeTestEqualBounds() {
        assertEquals(4, utils.randomIntInRange(4, 4, random));
    }

    @Test
    public void testRandomWithExclusion() {

        double r = utils.getRandomWithExclusion(new NotSoRandomForExclusion(), 0, 1, 0);

        assertNotEquals(0.0, r);

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

    private class NotSoRandom extends Random {

        private int lastReturned;

        @Override
        public int nextInt(int bound) {

            int ret = super.nextInt(bound);
            this.lastReturned = ret;

            return ret;

        }

        public int getLastReturned() {

            return lastReturned;

        }

    }

    private class NotSoRandomForExclusion extends Random {

        private double lastReturned = -1;

        @Override
        public double nextDouble() {

            if(lastReturned == 0) {

                double ret = super.nextDouble();
                lastReturned = ret;
                return ret;

            } else {

                lastReturned = 0;
                return 0;

            }

        }

    }

}

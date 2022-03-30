package server.game.questions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

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

    @Test
    public void testGetRandomDoubleInRange() {

        // Test with seed
        double randomDouble = utils.getRandomDoubleInRange(new Random(1234), 0.2, 0.5);
        Random random = new Random(1234);
        double randomHere = 0.2 + 0.3 * random.nextDouble();
        assertEquals(randomHere, randomDouble);
        assertTrue(randomDouble >= 0.2 && randomDouble <= 0.5);

        // Give "no range"
        randomDouble = utils.getRandomDoubleInRange(new Random(1234), 0.3, 0.3);
        assertEquals(0.3, randomDouble);

    }

    @Test
    public void testRandomWithExclusion() {

        double r = utils.getRandomWithExclusion(new NotSoRandomForExclusion(), 0, 1, 0);

        assertNotEquals(0.0, r);

    }

    @Test
    public void testGetBoundsEstimationQuestionSmall() {

        int[] bounds = utils.getBoundsEstimationQuestion(new RandomSameNumber(0.5), 200);
        // Percentage bounds result from: (11.4 - 0.8 * Math.log(200))*0.8, 11.4 - 0.8 * Math.log(200)
        // The bounds are: 5.729076, 7.161346
        // Get the "random" percentage:
        // 5.729076 + (7.161346 - 5.729076) * 0.5 = 6.44521
        // Set the range: (int) (6.44521 * 200) = 1289
        // Round to nearest 10: 1290
        // This already shifts it "to the right" (>= 0 as lower bound):
        int[] expected = new int[]{0, 1290};

        assertArrayEquals(expected, bounds);

    }

    @Test
    public void testGetBoundsEstimationQuestionBig() {

        int[] bounds = utils.getBoundsEstimationQuestion(new RandomSameNumber(0.1), 99999);
        // Percentage bounds result from: (11.4 - 0.8 * Math.log(99999))*0.8, 11.4 - 0.8 * Math.log(99999)
        // The bounds are: 1.75173, 2.18966
        // Get the "random" percentage:
        // 1.75173 + (2.18966 - 1.75173) * 0.1 = 1.795523
        // Set the range: (int) (1.839316 * 99999) = 179550
        // Shifting left: 0.3, shifting right: 0.7
        // Shifted bounds: 46133, 225684
        // Round to 10: 46130, 225680
        int[] expected = new int[]{46130, 225680};

        assertArrayEquals(expected, bounds);

    }

    /**
     * Class used as a non-random Random instance, that only returns a specific number.
     */
    private class RandomSameNumber extends Random {

        private double retVal;

        /**
         * Constructor to set the number it returns.
         */
        public RandomSameNumber(double retVal) {
            this.retVal = retVal;
        }

        @Override
        public double nextDouble() {

            return retVal;

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
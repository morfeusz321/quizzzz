package server.game.questions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
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

    @Test
    public void testGetMaxPercentageGeneral(){

        // Doubles are sometimes not considered equal due to minor errors. Alpha is the max. difference to test
        // the equality of the double values.
        double alpha = 0.00001;

        double generatedPercentage = utils.getMaxPercentageGeneral(0);
        assertTrue(5 - alpha <= generatedPercentage && generatedPercentage <= 5 + alpha);

        generatedPercentage = utils.getMaxPercentageGeneral(100);
        assertTrue(5 - alpha <= generatedPercentage && generatedPercentage <= 5 + alpha);

        generatedPercentage = utils.getMaxPercentageGeneral(10000000000000L);
        assertTrue(4.995117 - alpha <= generatedPercentage && generatedPercentage <= 4.995117 + alpha);

        generatedPercentage = utils.getMaxPercentageGeneral(9999999999999000L);
        assertTrue(1.882054 - alpha <= generatedPercentage && generatedPercentage <= 1.882054 + alpha);

    }

    @Test
    public void testCheckIfGeneratableFalse(){

        // All numbers excluded in range (100% blocked)
        assertFalse(utils.checkIfGeneratable(List.of(10L), 0.5, 5, 15));
        // More than 50% of the range is blocked
        assertFalse(utils.checkIfGeneratable(List.of(10L), 0.5, 0, 18));
        assertFalse(utils.checkIfGeneratable(List.of(10L), 0.5, 0, 15));
        // General tests with multiple exclusions
        assertFalse(utils.checkIfGeneratable(List.of(20L, 50L), 0.5, 0, 75));
        // Overlapping exclusions
        assertFalse(utils.checkIfGeneratable(List.of(100L, 110L), 0.5, 0, 130));
        // Bounds are 0,0
        assertFalse(utils.checkIfGeneratable(List.of(100L, 110L), 0.5, 0, 0));

    }

    @Test
    public void testIfGeneratableTrue(){

        // Only one number in range -> this is generatable, as 100% of the numbers in the range (1) are not excluded
        // (The bounds are inclusive!)
        assertTrue(utils.checkIfGeneratable(List.of(), 0, 1, 1));
        // Only two numbers in range, but none excluded (see above)
        assertTrue(utils.checkIfGeneratable(List.of(), 0, 999, 1000));
        // 10% of the range is blocked
        assertTrue(utils.checkIfGeneratable(List.of(10L), 0.5, 0, 100));
        // 20% of the range is blocked
        assertTrue(utils.checkIfGeneratable(List.of(10L), 0.5, 0, 50));
        // 25% of the range is blocked
        assertTrue(utils.checkIfGeneratable(List.of(10L), 0.5, 0, 40));
        // 40% of the range is blocked
        assertTrue(utils.checkIfGeneratable(List.of(10L), 0.5, 0, 25));
        // 50% of the range is blocked
        assertTrue(utils.checkIfGeneratable(List.of(10L), 0.5, 0, 20));
        // General check with a large value
        assertTrue(utils.checkIfGeneratable(List.of(99999999L), 0.5, 999999L, 99999999999L));
        // General check with multiple exclusions
        assertTrue(utils.checkIfGeneratable(List.of(2000L, 99999L), 0.2, 1800, 9999999L));

    }

    @Test
    public void testIfGeneratableTrueOverlapping() {

        // Having an exclusion zone that falls outside the desired range shouldn't matter
        assertTrue(utils.checkIfGeneratable(List.of(100L), 0.1, 0, 10));

        // Having the same exclusion point multiple times shouldn't matter
        assertTrue(utils.checkIfGeneratable(List.of(10L, 10L), 0.5, 0, 20));

        // Having two independent ranges should give an accurate result
        assertTrue(utils.checkIfGeneratable(List.of(1L, 17L), 0.1, 0, 20));

        // Having two overlapping ranges should give an accurate result
        // This range is effectively [3, 12]
        //                              => (12-3)/20 <= 0.5
        //                              => true
        // If overlap was not accounted for, this would result in (6+8)/20 > 0.5 => false
        assertTrue(utils.checkIfGeneratable(List.of(6L, 8L), 0.5, 0, 20));

    }

    @Test
    public void safeBoundCheckGeneralQuestionNoShift() {

        long[] bounds = new long[]{0,100};
        long[] expected = new long[]{0,100};
        utils.safeBoundCheckGeneralQuestion(bounds, 9999999);
        assertArrayEquals(expected, bounds);

        bounds = new long[]{9999999,9999999999L};
        expected = new long[]{9999999,9999999999L};
        utils.safeBoundCheckGeneralQuestion(bounds, 9999999);
        assertArrayEquals(expected, bounds);

    }

    @Test
    public void safeBoundCheckGeneralQuestionShift() {

        long[] bounds = new long[]{-20,20};
        long[] expected = new long[]{0,40};
        utils.safeBoundCheckGeneralQuestion(bounds, 9999999);
        assertArrayEquals(expected, bounds);

        bounds = new long[]{-15,5};
        expected = new long[]{0,20};
        utils.safeBoundCheckGeneralQuestion(bounds, 9999999);
        assertArrayEquals(expected, bounds);

        // Is theoretically a shift even though the values do not change
        bounds = new long[]{0,5};
        expected = new long[]{0,5};
        utils.safeBoundCheckGeneralQuestion(bounds, 9999999);
        assertArrayEquals(expected, bounds);

    }

    @Test
    public void safeBoundCheckGeneralQuestionCutOff() {

        // It only gets cut off to the negative side, as otherwise the bounds would be 0, 0
        long[] bounds = new long[]{-100,100};
        long[] expected = new long[]{0,100};
        utils.safeBoundCheckGeneralQuestion(bounds, 9999999);
        assertArrayEquals(expected, bounds);

        // Also gets cut off on the positive side
        bounds = new long[]{-99999,99999999L};
        expected = new long[]{0,99900000L};
        utils.safeBoundCheckGeneralQuestion(bounds, 9999999);
        assertArrayEquals(expected, bounds);

    }

    @Test
    public void randomLongInRangeExclThrows() {

        // If lower > upper, it should throw an IllegalArgumentException.
        Random random = new RandomSameNumber(0.5);
        assertThrows(IllegalArgumentException.class, () -> {
            utils.randomLongInRangeExcl(100,0, random, List.of(), List.of(),0.5);
        });
        // If lower = upper, and they are excluded (by consumption and/or consumption string),
        // then no number can be generated
        assertThrows(IllegalArgumentException.class, () -> {
            utils.randomLongInRangeExcl(100,100, random, List.of(), List.of(100L),0.5);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            utils.randomLongInRangeExcl(100,100, random, List.of("100 Wh"), List.of(),0.5);
        });

    }

    @Test
    public void randomLongInRangeExclInSIExcl() {

        // Here we test for cases in which the generated value FIRST is in the SI String exclusion, then on second try
        // it is not anymore.

        // Case where it is the same value that is also in the exclusion list for the long values
        Random random = new RandomTwoNumbers(0.01, 1);
        // On the first try it should try with 0.01 -> results in 1 as the generated value -> this is not a valid value
        // Then on second try it should try with 1 -> results in 100 which is valid -> so we test for 100
        assertEquals(100,
                utils.randomLongInRangeExcl(0,100, random, List.of("1 Wh"), List.of(1L),0.5));

        // Case where it is NOT the same value that is in the exclusion list for the long values, but the SI
        // string is the same
        random = new RandomTwoNumbers(0.01, 0.5);
        // On the first try it should try with 0.01 -> results in 1001000 as the generated value
        // -> this is not a valid value as it's SI string is 1.00 MWh, but it is not equal to 1000000
        // Then on second try it should try with 0.5 -> results in 1500000 which is valid -> so we test for 1500000
        assertEquals(1500000,
                utils.randomLongInRangeExcl(
                        1000000,2000000, random,
                        List.of("1.00 MWh"), List.of(1000000L),0.1));

    }

    @Test
    public void randomLongInRangeExclInExclRange() {

        // Here we test for cases in which the generated value FIRST is in the excluded ranges, then on second try
        // it is not anymore.

        // Case where it is in the exclusion list directly (not +/- range)
        Random random = new RandomTwoNumbers(0.01, 1);
        // On the first try it should try with 0.01 -> results in 110 as the generated value -> this is not a valid value
        // Then on second try it should try with 1 -> results in 200 which is valid -> so we test for 200
        assertEquals(200,
                utils.randomLongInRangeExcl(100,200, random, List.of(), List.of(110L),0.5));

        // Case where it is NOT the same value that is in the exclusion list for the long values, and
        // only in the range around it
        random = new RandomTwoNumbers(0.01, 0.5);
        // On the first try it should try with 0.01 -> results in 110 as the generated value
        // -> this is not a valid value as it is bigger than 70 and smaller than 130 (range that is excluded)
        // Then on second try it should try with 0.5 -> results in 150 which is valid -> so we test for 150
        assertEquals(150,
                utils.randomLongInRangeExcl(100,200, random, List.of(), List.of(100L),0.3));

    }

    @Test
    public void testGetBoundsGeneralQuestion(){

        long[] bounds = utils.getBoundsGeneralQuestion(100);
        // Max percent should be 5 according to manual calculation of the formula
        // The generated bounds will be: -150 and 350 (100 * (1 - 5/2), 100 * (1 + 5/2))
        // The safe check cuts of the negative part AND subtracts it from the upper
        // => So we expect [0, 200]
        assertArrayEquals(new long[]{0, 200}, bounds);

        bounds = utils.getBoundsGeneralQuestion(10000000000000000L);
        // Max percent should be about 1.88205433652 according to manual calculation of the formula
        // The generated bounds will be: 589728317000000 and 19410271700000000
        // (10000000000000000 * (1 - 1.88205433652/2), 10000000000000000 * (1 + 1.88205433652/2))
        // The safe check does not do anything, as we do not have a negative part
        // => So we expect [589728317000000, 19410271700000000]
        // However we need to consider rounding errors! (here and in code) So we check a range around the values.
        long alpha = 10000000000L; // allowed error for bounds
        assertTrue(589728317000000L - alpha <= bounds[0] && 589728317000000L + alpha >= bounds[0]);
        assertTrue(19410271700000000L - alpha <= bounds[1] && 19410271700000000L + alpha >= bounds[1]);

    }

    /**
     * Class used as a non-random Random instance, that returns a specific number in turn with another specific number.
     */
    private class RandomTwoNumbers extends Random {

        private final double[] retVals; // length should be 2
        private boolean firstsTurn; // if the first number is next (true) or the second (false)

        /**
         * Constructor to set the number it returns.
         */
        public RandomTwoNumbers(double val1, double val2) {

            firstsTurn = true;
            this.retVals = new double[]{val1, val2};

        }

        @Override
        public double nextDouble() {

            if(firstsTurn) {
                firstsTurn = false;
                return retVals[1];
            }
            firstsTurn = true;
            return retVals[0];

        }

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
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

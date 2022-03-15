package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CommonUtilsTest {

    private Random random;

    @BeforeEach
    public void setup() {

        this.random = new NotSoRandom();

    }

    @Test
    void prependingZeroTestPrepend(){
        CommonUtils utils = new CommonUtils();
        assertEquals("01", utils.addPrependingZero(1));
    }

    @Test
    void prependingZeroTestNoPrepend(){
        CommonUtils utils = new CommonUtils();
        assertEquals("10", utils.addPrependingZero(10));
        assertEquals("-3", utils.addPrependingZero(-3));
    }

    @RepeatedTest(20)
    void randomIntInRangeTestBothNegative(){
        CommonUtils utils = new CommonUtils();
        int r = utils.randomIntInRange(-50, -10, random);
        assertEquals(((NotSoRandom) random).getLastReturned() - 50, r);
    }

    @RepeatedTest(20)
    void randomIntInRangeTestBothPositive(){
        CommonUtils utils = new CommonUtils();
        int r = utils.randomIntInRange(10, 50, random);
        assertEquals(((NotSoRandom) random).getLastReturned() + 10, r);
    }

    @RepeatedTest(20)
    void randomIntInRangeTestMixedSignBounds() {
        CommonUtils utils = new CommonUtils();
        int r = utils.randomIntInRange(-10, 50, random);
        assertEquals(((NotSoRandom) random).getLastReturned() - 10, r);
    }

    @Test
    void randomIntInRangeTestIllegalBounds() {
        CommonUtils utils = new CommonUtils();
        assertThrows(IllegalArgumentException.class, () -> utils.randomIntInRange(10, 0, random));
    }

    @Test
    void randomIntInRangeTestEqualBounds() {
        CommonUtils utils = new CommonUtils();
        assertEquals(4, utils.randomIntInRange(4, 4, random));
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

}

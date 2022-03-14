package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    }

    @RepeatedTest(20)
    void randomIntInRangeTestBothNegative(){
        CommonUtils utils = new CommonUtils();
        int randomNegative = utils.randomIntInRange(-50, -10, random);
        assertEquals(((NotSoRandom) random).getLastReturned() - 50, randomNegative);
    }

    @RepeatedTest(20)
    void randomIntInRangeTestBothPositive(){
        CommonUtils utils = new CommonUtils();
        int randomPositive = utils.randomIntInRange(10, 50, random);
        assertEquals(((NotSoRandom) random).getLastReturned() + 10, randomPositive);
    }

    @RepeatedTest(20)
    void randomIntInRangeTestMixedSignBounds() {
        CommonUtils utils = new CommonUtils();
        int randomPositive = utils.randomIntInRange(-10, 50, random);
        assertEquals(((NotSoRandom) random).getLastReturned() - 10, randomPositive);
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

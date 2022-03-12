package commons;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommonUtilsTest {
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

    @Test
    void randomIntInRangeTestNegative(){
        CommonUtils utils = new CommonUtils();
        int randomNegative = utils.randomIntInRange(-50, -10, new Random(12345));
        Random r = new Random(12345);
        assertEquals((r.nextInt(60) - 10) * -1, randomNegative);
    }

    @Test
    void randomIntInRangeTestPositive(){
        CommonUtils utils = new CommonUtils();
        int randomPositive = utils.randomIntInRange(10, 50, new Random(12345));
        Random r = new Random(12345);
        assertEquals(r.nextInt(60) - 10, randomPositive);
    }
}

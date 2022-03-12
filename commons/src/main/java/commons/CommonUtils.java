package commons;

import java.util.Random;

public class CommonUtils {

    /**
     * Creates a CommonUtils object, empty constructor
     */
    public CommonUtils() {}

    /**
     * If necessary, this adds a prepending zero to a given integer. Used for displaying the timer.
     * @param i The integer to display
     * @return A string of the integer (if necessary, with a prepended zero)
     */
    public String addPrependingZero(int i){
        if(i < 10){
            return "0" + i;
        }
        return String.valueOf(i);
    }

    /**
     * Returns a random integer in a given range. The bounds need to be either both negative or both positive.
     * Lower and upper must be different values.
     * @param lower The lower bound (inclusive)
     * @param upper The upper bound (inclusive)
     * @param r A random number generator
     * @return A random integer in the given range.
     */
    public int randomIntInRange(int lower, int upper, Random r){
        if(lower < 0 && upper < 0){
            int tmp = upper;
            upper = -1 * lower;
            lower = -1 * tmp;
            // if both are negative, then the absolute value of the lower bound is higher than
            // that of the upper bound
            return (r.nextInt(upper - lower) + lower) * -1;
        }
        return r.nextInt(upper - lower) + lower;
    }
}

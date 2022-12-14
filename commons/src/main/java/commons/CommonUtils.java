package commons;

import java.util.Random;

public class CommonUtils {

    /**
     * Creates a CommonUtils object, empty constructor
     */
    public CommonUtils() {
    }

    /**
     * If necessary, this adds a prepending zero to a given integer to pad
     * its String representation to a length of 2. Used for displaying the timer.
     *
     * @param i The integer to display
     * @return A string of the integer (if necessary, with a prepended zero)
     */
    public String addPrependingZero(int i) {
        if(i < 0 || i >= 10) return String.valueOf(i);
        return "0" + i;
    }

    /**
     * Returns a random integer in a given range, with both the lower and upper bound inclusive.
     * If the bounds are equal, this method simply returns the lower bound.
     *
     * @param lower The lower bound (inclusive)
     * @param upper The upper bound (inclusive)
     * @param r     A random number generator
     * @return A random integer in the given range (both bounds inclusive).
     */
    public int randomIntInRange(int lower, int upper, Random r) {

        // The lower bound may never be strictly greater than the upper bound
        if(lower > upper) {
            throw new IllegalArgumentException();
        }

        // If the bounds are equal, then we can simply return one of the bounds
        // This is valid, because both bounds are inclusive
        if(lower == upper) {
            return lower;
        }

        // Because both bounds are inclusive, we have to generate an amount of numbers
        // that is equal to the difference between the bounds, plus 1
        // Then, we use the lower bound as an offset for this range to give us the desired range
        int amountOfNumbers = upper - lower + 1;
        return r.nextInt(amountOfNumbers) + lower;

    }

    /**
     * Utility method, used to check if a string is null or empty
     *
     * @param s a string
     * @return true if the string is empty or null, false otherwise
     */
    public static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

}

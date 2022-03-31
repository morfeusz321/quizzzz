package server.game.questions;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Random;

@Component
public class QuestionGeneratorUtils {

    /**
     * Empty constructor, creates an instance of the QuestionGeneratorUtils
     */
    public QuestionGeneratorUtils(){}

    /**
     * Generates a (random) upper/lower bound for a given consumption, which is used to generate the new activities
     * with a close consumption to this one. The bound is dependent on the "scale" of the given consumption. The input
     * should be non-negative.
     * @param consumption the consumption from which to generate a range
     * @return an array with two longs, the lower bound (idx 0) and the upper bound (idx 1)
     */
    public long[] getLowerUpperBoundSmall(long consumption){
        // This is a method that creates a "small" range, that is closer to the initial value.
        // The range does not have to be generated randomly, as the activity itself is chosen randomly
        // within that range.
        // TODO: add method with bigger range, so that different "difficulties" can be generated
        if(consumption <= 500){
            return new long[]{0, 500};
        } else if(consumption <= 1000){
            return new long[]{500, 1000};
        } else if(consumption <= 10000){
            return new long[]{1000,10000};
        } else if(consumption <= 100000){
            return new long[]{10000,10000000L};
        } else if(consumption <= 10000000L){
            return new long[]{100000,1000000000L};
        } else if(consumption <= 1000000000L){
            return new long[]{10000000L,100000000000L};
        } else if(consumption <= 100000000000L){
            return new long[]{1000000000L,100000000000L};
        } else {
            return new long[]{100000000000L,Long.MAX_VALUE};
        }
    }

    /**
     * Creates a string from a given consumption in Wh that is rounded to 2 decimals and uses SI prefixes such
     * as k (kilo, i.e. 1 000) and M (Mega, i.e. 1 000 000) to make it easier to read
     * @param consumption the consumption in Wh
     * @return a string of the consumption using SI prefixes and rounded to 2 decimals
     */
    public String createConsumptionString(long consumption) {

        return createConsumptionString(consumption, 2);

    }

    /**
     * Gives the max. percentage value for the range in which to generate answer options for the general question
     * type, i.e. the answer options are then generated by random((lower percentage), this percentage) * consumption.
     * @param consumption The consumption of the activity for which to generate the percentage.
     * @return The max. percentage value for the range in which to generate answer options for the general question.
     */
    public double getMaxPercentageGeneral(long consumption) {

        // This formula generates a bigger range, i.e. a maximum of 5, and a minimum of 0.4.
        // The exact percentage value depends on the consumption of the activity. This means that, if the
        // consumption is smaller, the range percentage will be higher (so that a sufficiently big/small
        // range is generated).

        // f(x) = 5*e^(-9.77074*10^(-17)*x)
        return 5 * Math.pow(2.71828, -9.77074 * Math.pow(10, -17) * consumption);

    }

    /**
     * Creates a string from a given consumption in Wh that is rounded to the provided amount of decimals
     * and uses SI prefixes such as k (kilo, i.e. 1 000) and M (Mega, i.e. 1 000 000) to make it easier to read
     * @param consumption the consumption in Wh
     * @return a string of the consumption using SI prefixes and rounded to the provided amount of decimals, or an
     * IllegalArgumentException if the amount of decimals or the consumption is negative
     */
    public String createConsumptionString(long consumption, int amountOfDecimals) throws IllegalArgumentException {

        if(amountOfDecimals < 0) throw new IllegalArgumentException("Amount of decimals cannot be negative!");
        if(consumption < 0) throw new IllegalArgumentException("Consumption cannot be negative!");

        if(consumption < 1000) return consumption + " Wh";

        StringBuilder stringBuilder = new StringBuilder();

        double doubleConsumption = (double) consumption;
        int steps = 0;
        while(doubleConsumption >= 1000.0 && steps < 4) {
            doubleConsumption /= 1000.0;
            steps++;
        }

        double roundingFactor = Math.pow(10, amountOfDecimals);
        double rounded = ((double) Math.round(doubleConsumption * roundingFactor)) / roundingFactor;

        String formatString = "%." + amountOfDecimals + "f";
        stringBuilder.append(String.format(Locale.ENGLISH, formatString, rounded)).append(" ").append(getSIPrefix(steps)).append("Wh");
        return stringBuilder.toString();

    }

    /**
     * Gets the SI prefix for a provided division step by 1 000, for example, one division by 1 000 gives the
     * prefix "k" for kilo (1 000), or two division steps give the prefix "M" for Mega (1 000 000)
     * @param step the amount of division steps by 1 000, (at most 4)
     * @return the appropriate prefix for the amount of division steps by 1 000, or an IllegalArgumentException
     * in case the amount of steps is greater than 4
     */
    public String getSIPrefix(int step) throws IllegalArgumentException {

        if(step <= 0) {
            return "";
        } else if(step == 1) {
            return "k";
        } else if(step == 2) {
            return "M";
        } else if(step == 3) {
            return "G";
        } else if(step == 4) {
            return "T";
        } else {
            throw new IllegalArgumentException("The amount of division steps cannot be greater than 4!");
        }

    }

    /**
     * Generates random number from given range excluding those given as the parameter
     * @param rnd Instant of the Random class
     * @param start the start of the range in which the number will be generated
     * @param end the end of the range in which the number will be generated
     * @param exclude numbers that will be excluded from the given range
     * @return random number in given range (number is rounded to first decimal place)
     */
    public double getRandomWithExclusion(Random rnd, double start, double end, int... exclude) {
        double random = start + (end - start) * rnd.nextDouble();
        random = (double) Math.round(random * 10) / 10;
        for (int ex : exclude) {
            if (random == (double) ex) {
                return getRandomWithExclusion(rnd, start, end, exclude);
            }
        }
        return random;
    }

    /**
     * If the probability of generating a value
     * @param excluded The values around which certain percentages should be excluded.
     * @param percentExcl The percentage that should be excluded in +/- direction (PER direction, not total)
     * @return Whether the probability of generating the random value is high enough (true/false)
     */
    public boolean checkIfGeneratable(List<Long> excluded, double percentExcl, long lower, long upper){

        // Starting point is just normal range
        long possibleValues = upper - lower;

        // Subtract the excluded ranges
        for(long l : excluded){
            double min = l - percentExcl * l;
            double max = l + percentExcl * l;
            possibleValues = possibleValues - Math.round(max - min);
        }

        long totalRange = upper - lower;
        // If probability is < 1/3 this might create StackOverFlowErrors
        return possibleValues > 0 && ((double) possibleValues / totalRange > 0.3);

    }

    /**
     * Returns a random long in a given range, with both the lower and upper bound inclusive.
     * If the bounds are equal, this method simply returns the lower bound. Some numbers can be excluded.
     * @param lower The lower bound (inclusive)
     * @param upper The upper bound (inclusive)
     * @param r A random number generator
     * @param excludeSIString SI Strings that should be excluded
     * @param excludedNumbers Values that values should be excluded around (in a certain percentage range)
     * @param percentExcl The percentage range in which values should be excluded
     * @return A random long in the given range (both bounds inclusive) which is not equal to the excluded values.
     */
    public long randomLongInRangeExcl(
            long lower,
            long upper,
            Random r,
            List<String> excludeSIString,
            List<Long> excludedNumbers,
            double percentExcl
    ){

        // The lower bound may never be strictly greater than the upper bound
        if(lower > upper) {
            throw new IllegalArgumentException();
        }

        // If the bounds are equal, then we can simply return one of the bounds
        // This is valid, because both bounds are inclusive
        if(lower == upper) {
            return lower;
        }

        // We use the lower bound as an offset for this range to give us the desired range.
        // Random.nextDouble() has to be used as there is no method for longs that 1. can have a limit and 2. returns
        // all possible long values.
        long random = (long) (r.nextDouble() * (upper - lower) + lower);

        // Check excluded SI Strings
        for (String ex : excludeSIString) {
            if (createConsumptionString(random).equals(ex)) {
                return randomLongInRangeExcl(lower, upper, r, excludeSIString, excludedNumbers, percentExcl);
            }
        }

        // Check excluded values in ranges
        for (Long ex : excludedNumbers) {
            if (random <= (ex + ex * percentExcl) && random >= (ex - ex * percentExcl)) {
                return randomLongInRangeExcl(lower, upper, r, excludeSIString, excludedNumbers, percentExcl);
            }
        }

        return random;

    }

    /**
     * Returns the bounds in which to generate the consumption values (answer options) for the general question.
     * @param consumption The consumption which is the answer to the question.
     * @return the bounds in which to generate the consumption values (answer options) for the general question.
     */
    public long[] getBoundsGeneralQuestion(long consumption) {
        double maxPercentage = getMaxPercentageGeneral(consumption);
        long[] bounds = new long[]{
                (long) (consumption * (1 - maxPercentage / 2)),
                (long) (consumption * (1 + maxPercentage / 2))
        };
        safeBoundCheckGeneralQuestion(bounds);
        return bounds;
    }

    /**
     * Performs a "safety check" for the bounds, i.e. checks for negative bounds, and shifts/cuts it off if necessary.
     * This method is used for the general question generation.
     * @param bounds The bounds to check (two boundaries)
     */
    public void safeBoundCheckGeneralQuestion(long[] bounds){
        // Cut-off to not create too large bounds
        if (bounds[0] < 0) {
            if(bounds[1] <= 20){
                // If it is very small, shift instead of cut-off
                bounds[1] = bounds[1] - bounds[0];
            } else {
                bounds[1] = bounds[1] + bounds[0];
                // Cut off so that it is not more likely to have a larger number.
                // Note that this is + and not - on purpose for the above reason
            }
            bounds[0] = 0;
        }
    }

    /**
     * Generates random number from given range excluding those given as the parameter
     * @param rnd Instant of the Random class
     * @param start the start of the range in which the number will be generated
     * @param end the end of the range in which the number will be generated
     * @return random number in given range (number is not rounded)
     */
    public double getRandomDoubleInRange(Random rnd, double start, double end) {
        return start + (end - start) * rnd.nextDouble();
    }

    /**
     * Generates bounds for the input range for the estimation question.
     * @param consumption The consumption of the activity for which to generate the range.
     * @return The bounds for the estimation question input.
     */
    public int[] getBoundsEstimationQuestion(Random rnd, int consumption) {
        // Find the maximum percentage of the consumption which defines the maximum length of the range.
        // This uses a function which was fitted to certain values, which logically and game-wise make sense,
        // so that there are not only certain threshold values, but a continuous distribution.
        // It is a logarithmic function, so that, as the consumption grows, the actual range does not grow
        // as large.
        double maxPercentage = 11.4 - 0.8 * Math.log(consumption);
        double minPercentage = maxPercentage * 0.8;
        // Generate a random percentage in the range
        double percentage = getRandomDoubleInRange(rnd, minPercentage, maxPercentage);
        // The range is set to a value, however, the start- and end-values will be generated from this range.
        // We can cast to int as range/consumption are expected to not be longs.
        int range = (int) (percentage * consumption);
        // Minimum and maximum range conditions
        if(range < 200){
            range = 200;
        } else if(range > 500000){
            range = 500000;
        }

        // Generate the shift percentage values
        double shiftMinPercentage = rnd.nextDouble() + 0.2;
        if(shiftMinPercentage > 1){
            shiftMinPercentage = 1;
        }
        double shiftMaxPercentage = 1 - shiftMinPercentage;

        // Create min/max bounds
        int min = (int) (consumption - shiftMinPercentage * range);
        int max = (int) (consumption + shiftMaxPercentage * range);

        // First check lower bound as that should not be negative
        if(min < 0) {
            max = max - min;
            min = 0;
        }

        // Round the ranges to 10Wh
        min = (int) (Math.round(min/10.0) * 10);
        max = (int) (Math.round(max/10.0) * 10);

        // Now check upper bound
        if(max > 999999 && (min - max) > 0) {
            min = min - max;
            max = 999999;
        }

        return new int[]{min, max};
    }

}

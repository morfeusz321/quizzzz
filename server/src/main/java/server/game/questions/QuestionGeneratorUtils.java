package server.game.questions;

import org.springframework.stereotype.Component;

import java.util.Locale;

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
}

package commons;

public class Utils {
    /**
     * Utility method, used to check if a string is null or empty
     * @param s a string
     * @return true if the string is empty or null, false otherwise
     */
    public static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}

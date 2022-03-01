package commons;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

public class Question {

    public Activity activity;

    /**
     * Empty constructor used by object mapper
     */
    @SuppressWarnings("unused")
    private Question() {

    }

    /**
     * Creates a question object
     * @param activity object that will be used for this question
     */
    public Question(Activity activity) {
        this.activity = activity;
    }

    /**
     * Display the question to the user
     * @return a formatted string containing the question
     */
    public String displayQuestion() {
        return "How much energy does " + activity.displayActivity() + " take?";
    }

    /**
     * Checks if 2 question objects are equal
     * @param obj the object that will be compared
     * @return true or false, whether the objects are equal or not
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * Generate a hash code for this object
     * @return hash code
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * Creates a formatted string for this object
     * @return a formatted string in multi line style
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }

}

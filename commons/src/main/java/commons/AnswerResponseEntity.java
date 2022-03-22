package commons;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

public class AnswerResponseEntity {

    public boolean correct;
    public long proximity;
    private long answer;

    /**
     * Empty constructor used by object mapper
     */
    @SuppressWarnings("unused")
    private AnswerResponseEntity() {



    }

    /**
     * gets the answer number
     * @return the number of the answer
     */
    public long getAnswer() {
        return answer;
    }

    /**
     * Creates a new answer response entity (used for general and comparison questions)
     * @param correct whether the answer was correct or not
     */
    public AnswerResponseEntity(boolean correct, long answer) {

        this.correct = correct;
        this.proximity = 0;
        this.answer = answer;

    }

    /**
     * Creates a new answer response entity (used for estimation questions)
     * @param correct whether the answer should be displayed as correct or not
     * @param proximity the difference between the answer and the correct answer
     */
    public AnswerResponseEntity(boolean correct, long proximity, long answer) {

        this.correct = correct;
        this.proximity = proximity;
        this.answer = answer;

    }

    /**
     * Checks if 2 answer response entity objects are equal
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

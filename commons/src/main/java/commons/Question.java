package commons;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public abstract class Question {

    public enum QuestionType {
        GENERAL,
        COMPARISON,
        ESTIMATION
    }

    @Id
    public UUID questionId = UUID.randomUUID();
    public QuestionType questionType;

    public String activityTitle;
    public String activityImagePath;

    @ElementCollection
    public List<String> answerOptions;
    @JsonIgnore
    public long answer;

    /**
     * Empty constructor used by object mapper
     */
    @SuppressWarnings("unused")
    Question() {

    }

    /**
     * Creates a human-readable form of this question
     * @return a formatted string of the question, which differs based on question type
     */
    public String displayQuestion() {

        if(questionType == QuestionType.GENERAL || questionType == QuestionType.ESTIMATION) {
            return "How much energy does " + Activity.displayActivity(activityTitle) + " take?";
        } else if(questionType == QuestionType.COMPARISON) {
            return "Instead of " + Activity.displayActivity(activityTitle) + ", you could use the same amount of energy for...";
        }

        return activityTitle;

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

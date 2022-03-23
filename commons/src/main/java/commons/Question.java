package commons;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public abstract class Question {

    @Id
    public UUID questionId = UUID.randomUUID();

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

        if(this instanceof GeneralQuestion || this instanceof EstimationQuestion) {
            return "How much energy does " + Activity.displayActivity(activityTitle) + " take?";
        } else if(this instanceof ComparisonQuestion) {
            return "Instead of " + Activity.displayActivity(activityTitle) + ", you could use the same amount of energy for...";
        }else if(this instanceof WhichIsMoreQuestion){
            return "Which activity consumes more energy?";
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
        if(obj == null){
            return false;
        }
        if(obj instanceof Question){
            Question other = (Question) obj;
            // Check if one of the question lists is null (in that case not equal)
            boolean oneIsNull =
                    (this.answerOptions == null && other.answerOptions != null) ||
                    (this.answerOptions != null && other.answerOptions == null);
            // Check if they are both null (then they are equal). Otherwise, check if the sizes are the same,
            // and the contents, but not necessarily the order.
            boolean answerListsEqual =
                    (this.answerOptions == null && other.answerOptions == null) ||
                    (!oneIsNull &&
                    this.answerOptions.size() == other.answerOptions.size() &&
                    this.answerOptions.containsAll(other.answerOptions));
            // Check all other attributes
            return answerListsEqual &&
                    this.questionId.equals(other.questionId) &&
                    Objects.equals(this.activityTitle, other.activityTitle) &&
                    Objects.equals(this.activityImagePath, other.activityImagePath) &&
                    this.answer == other.answer;

        }
        return false;
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

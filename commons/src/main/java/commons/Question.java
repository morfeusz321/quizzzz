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
    // Note: the answer starts from 1, and not from 0.
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
            if(oneIsNull){
                return false;
            }
            // Check if both of the question lists are null (in that case not equal)
            boolean bothNull = this.answerOptions == null && other.answerOptions == null;
            // TODO: Intellij says that other.answerOptions == null is always true when reached?
            // Check if they are both null (then they are equal). Otherwise, check if the sizes are the same,
            // and the contents, but not necessarily the order.
            boolean answerListsEqual =
                    bothNull ||
                    (this.answerOptions.size() == other.answerOptions.size() &&
                    this.answerOptions.containsAll(other.answerOptions));
            // For safety, it has to be checked whether the answer options are in the correct range
            boolean thisOutOfRange = this.answer > this.answerOptions.size() || this.answer <= 0;
            boolean otherOutOfRange = other.answer > other.answerOptions.size() || other.answer <= 0;
            if(thisOutOfRange || otherOutOfRange){
                if(!thisOutOfRange || !otherOutOfRange){
                    // Only one of the answer options is out of range -> not equal
                    return false;
                }
            }
            // Check if the answer is the same - this cannot be simply done by checking the index, but it has to
            // be the element at the index.
            // For simplicity, two answer options that are both out of range are counted as equal.
            boolean answersEqual =
                    bothNull ||
                    (thisOutOfRange && otherOutOfRange) ||
                    this.answerOptions.get((int) this.answer - 1).equals(other.answerOptions.get((int) other.answer - 1));
            // Check all other attributes
            return answerListsEqual &&
                    this.questionId.equals(other.questionId) &&
                    Objects.equals(this.activityTitle, other.activityTitle) &&
                    Objects.equals(this.activityImagePath, other.activityImagePath) &&
                    answersEqual;

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

package commons;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
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
     * Checks if 2 question objects are equal. They are considered as equal, if they are of the same question type and
     * if they have the same main activity.
     * @param obj the object that will be compared
     * @return true or false, whether the objects are equal or not
     */
    @Override
    public boolean equals(Object obj) {
        if(obj == null){
            return false;
        }
        // First check if it is an instance of class Question the correct sub-class of Question.
        if(obj instanceof Question && this.getClass().equals(obj.getClass())){

            Question other = (Question) obj;
            // Note: We assume that all Question objects have a valid answer, and that they were initialized with
            // non-null parameters. (See comment in Question class constructors). The empty constructor is private,
            // so that cannot be called anyways.

            // We only check whether the main activity is equal, this makes the most sense in the game context. The
            // main activity determines the activityTitle and the activityImagePath.
            // The id should not be checked, because this would make questions appear as not equal, even if they
            // are (according to the other conditions). Reason is that each question has an individual id.
            return Objects.equals(this.activityTitle, other.activityTitle) &&
                    Objects.equals(this.activityImagePath, other.activityImagePath);

        }
        return false;
    }

    /**
     * Generate a hash code for this object. Two objects have the same hash code, if their main activity is the same,
     * and if they are of the same sub-class, so the class is hashed too.
     * @return hash code
     */
    @Override
    public int hashCode() {
        return getClass().hashCode() ^ activityTitle.hashCode() ^ activityImagePath.hashCode();
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

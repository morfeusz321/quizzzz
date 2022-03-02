package commons;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
public class Question {

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
    private Question() {

    }

    /**
     * Creates a question object
     * @param questionType the type of question this object represents
     * @param answer the answer to this question (either the index 1, 2, 3 for multiple choice questions, or the actual answer for open questions)
     * @param activities the activities this question should concern (1 activity for estimation questions, 4 activities for comparison questions).
     *                   The first activity is the main activity of this question (i.e. the title of the question), the other activities are the
     *                   multiple choice answer options
     */
    public Question(QuestionType questionType, long answer, Activity... activities) {

        this.questionType = questionType;

        if(questionType == QuestionType.GENERAL) {
            throw new IllegalArgumentException();
        }

        if(questionType == QuestionType.COMPARISON && activities.length != 4) {
            throw new IllegalArgumentException();
        }

        if(questionType == QuestionType.ESTIMATION && activities.length != 1) {
            throw new IllegalArgumentException();
        }

        this.answer = answer;

        Activity mainActivity = activities[0];
        this.activityTitle = mainActivity.title;
        this.activityImagePath = mainActivity.imagePath;

        this.answerOptions = new ArrayList<>();

        for(int i = 1; i < activities.length; i++) {

            this.answerOptions.add(activities[i].title);

        }

    }

    /**
     * Creates a question object (used for QuestionType.GENERAL)
     * @param questionType should be QuestionType.GENERAL
     * @param activity the main activity of this question (i.e. the title)
     * @param answer the answer to this question (i.e. the index 1, 2, 3 of the multiple choice answer)
     * @param generalQuestionAnswerOptions the answer options to be displayed for this question (e.g. "50 Wh", "100 Wh", etc.)
     */
    public Question(QuestionType questionType, Activity activity, long answer, String... generalQuestionAnswerOptions) {

        this.questionType = questionType;

        if(questionType == QuestionType.ESTIMATION || questionType == QuestionType.COMPARISON) {
            throw new IllegalArgumentException();
        }

        if(generalQuestionAnswerOptions.length != 3) {
            throw new IllegalArgumentException();
        }

        this.answer = answer;

        this.activityTitle = activity.title;
        this.activityImagePath = activity.imagePath;

        this.answerOptions = new ArrayList<>();
        this.answerOptions.addAll(Arrays.asList(generalQuestionAnswerOptions));

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
     * Returns this question's unique ID
     * @return the random UUID associated with this question
     */
    public UUID getQuestionId() {

        return questionId;

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

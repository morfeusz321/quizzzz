package commons;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a multiple choice question in the form of "Instead of {...}, you could use the same amount of energy for {...}"
 */
@Entity
public class ComparisonQuestion extends Question {

    /**
     * Empty constructor used by object mapper
     */
    @SuppressWarnings("unused")
    private ComparisonQuestion() {



    }

    /**
     * Creates a question object (used for QuestionType.COMPARISON)
     * @param mainActivity the main activity of this question (i.e. the title of the question)
     * @param answerOptions the multiple choice answer options for this question
     * @param answer the answer to this question (i.e. the index 1, 2, 3 of the multiple choice answer option)
     */
    public ComparisonQuestion(Activity mainActivity, List<Activity> answerOptions, long answer) {
        // Note: we can assume that the question is initialized with non-null parameters. Questions are only initialized
        // in the QuestionGenerator (server-side), where null-checks are done to make that sure.

        this.activityTitle = mainActivity.title;
        this.activityImagePath = mainActivity.imagePath;

        this.answerOptions = new ArrayList<>();
        for(Activity a : answerOptions) {
            this.answerOptions.add(a.title);
        }

        this.answer = answer;

    }

}

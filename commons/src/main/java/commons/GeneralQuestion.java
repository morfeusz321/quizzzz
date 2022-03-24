package commons;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a multiple choice question in the form of "How much energy does {...} take?"
 */
@Entity
public class GeneralQuestion extends Question {

    /**
     * Empty constructor used by object mapper
     */
    @SuppressWarnings("unused")
    private GeneralQuestion() {



    }

    /**
     * Creates a question object (used for QuestionType.GENERAL)
     * @param mainActivity the main activity of this question (i.e. the title)
     * @param answerOptions the answer options to be displayed for this question (e.g. "50 Wh", "100 Wh", etc.)
     * @param answer the answer to this question (i.e. the index 1, 2, 3 of the multiple choice answer)
     */
    public GeneralQuestion(Activity mainActivity, List<String> answerOptions, long answer) {

        // TODO: it might be safe to add null checks here and in the other question types?

        this.activityTitle = mainActivity.title;
        this.activityImagePath = mainActivity.imagePath;

        this.answerOptions = new ArrayList<>();
        this.answerOptions.addAll(answerOptions);

        this.answer = answer;

    }

}

package commons;

import javax.persistence.Entity;
import java.util.List;

/**
 * Represents an open question in the form of "How much energy does {...} take?"
 */
@Entity
public class EstimationQuestion extends Question {

    /**
     * Empty constructor used by object mapper
     */
    @SuppressWarnings("unused")
    private EstimationQuestion() {



    }

    /**
     * Creates a question object (used for QuestionType.ESTIMATION)
     * @param mainActivity the main activity of this question (i.e. the title of the question)
     * @param answerOptions the answer options (range and actual answer)
     */
    public EstimationQuestion(Activity mainActivity, List<String> answerOptions) {
        // Note: we can assume that the question is initialized with non-null parameters. Questions are only initialized
        // in the QuestionGenerator (server-side), where null-checks are done to make that sure.

        this.activityTitle = mainActivity.title;
        this.activityImagePath = mainActivity.imagePath;

        this.answerOptions = answerOptions;

        this.answer = Long.parseLong(answerOptions.get(2));

    }

}

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
     */
    public EstimationQuestion(Activity mainActivity, List<String> answerOptions) {

        this.activityTitle = mainActivity.title;
        this.activityImagePath = mainActivity.imagePath;

        this.answerOptions = answerOptions;

        this.answer = Long.parseLong(answerOptions.get(2));

    }

}

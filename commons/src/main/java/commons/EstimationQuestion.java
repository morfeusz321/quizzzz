package commons;

import javax.persistence.Entity;
import java.util.ArrayList;

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
    public EstimationQuestion(Activity mainActivity) {

        this.activityTitle = mainActivity.title;
        this.activityImagePath = mainActivity.imagePath;

        this.answerOptions = new ArrayList<>();

        this.answer = mainActivity.consumption;

    }

}

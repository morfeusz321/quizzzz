package commons;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;
@Entity
public class WhichIsMoreQuestion extends Question{


    /**
     * Empty constructor used by object mapper
     */
    @SuppressWarnings("unused")
    private WhichIsMoreQuestion() {



    }

    /**
     * Creates a question object (used for QuestionType.WHICHISMORE)
     * @param answerOptions the answer options to be displayed for this question (e.g. "50 Wh", "100 Wh", etc.)
     * @param answer the answer to this question (i.e. the index 1, 2, 3 of the multiple choice answer)
     */
    public WhichIsMoreQuestion(List<Activity> answerOptions, long answer) {
        // Note: we can assume that the question is initialized with non-null and valid parameters. Questions are only
        // initialized in the QuestionGenerator (server-side), where null-checks are done to make that sure.

        Activity mainActivity = answerOptions.get((int) answer - 1); // starts from 1
        this.activityTitle = mainActivity.title;
        this.activityImagePath = mainActivity.imagePath;

        this.answerOptions = new ArrayList<>();
        for(Activity a : answerOptions) {
            this.answerOptions.add(a.title);
        }

        this.answer = answer;

    }
}

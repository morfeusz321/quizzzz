package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.CommonUtils;
import commons.Question;
import javafx.scene.image.Image;

public class MostExpensiveQuestionCtrl extends MultipleChoiceQuestionCtrl {

    /**
     * Creates a MostExpensiveQuestionCtrl, which controls the display/interaction of the comparison question screen.
     * @param server Utilities for communicating with the server (API endpoint)
     * @param mainCtrl The main control which is used for calling methods to switch scenes
     * @param utils Common utilities (for server- and client-side)
     */
    @Inject
    public MostExpensiveQuestionCtrl(ServerUtils server, MainCtrl mainCtrl, CommonUtils utils) {
        super(server, mainCtrl, utils);
    }

    /**
     * Gets a random question from the server and displays the question to the client. Also, restarts the progress bar.
     */
    public void loadQuestion(Question q) {

        // TODO: add "more expensive" question type, and restructure this afterwards

        questionImg.setImage(new Image("/client/img/question_mark.png"));
        title.setText(q.displayQuestion());
        answerBtn1.setText(q.answerOptions.get(0));
        answerBtn2.setText(q.answerOptions.get(1));
        answerBtn3.setText(q.answerOptions.get(2));
        refreshProgressBar();

    }

    // TODO: when the answer is displayed, the correct image should be shown!

}

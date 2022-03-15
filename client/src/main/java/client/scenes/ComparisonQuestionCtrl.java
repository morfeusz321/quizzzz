package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.CommonUtils;
import commons.Question;
import javafx.scene.image.Image;

public class ComparisonQuestionCtrl extends MultipleChoiceQuestionCtrl {

    /**
     * Creates a ComparisonQuestionCtrl, which controls the display/interaction of the comparison question screen.
     * @param server Utilities for communicating with the server (API endpoint)
     * @param mainCtrl The main control which is used for calling methods to switch scenes
     * @param utils Common utilities (for server- and client-side)
     */
    @Inject
    public ComparisonQuestionCtrl(ServerUtils server, MainCtrl mainCtrl, CommonUtils utils) {
        super(server, mainCtrl, utils);
    }

    /**
     * Gets a random question from the server and displays the question to the client. Also, restarts the progress bar.
     */
    public void loadQuestion(Question q) {

        // TODO: add comparison question type, and restructure this afterwards if needed

        questionImg.setImage(new Image(ServerUtils.getImageURL(q.activityImagePath)));
        title.setText(q.displayQuestion());
        answerBtn1.setText(q.answerOptions.get(0));
        answerBtn2.setText(q.answerOptions.get(1));
        answerBtn3.setText(q.answerOptions.get(2));
        refreshProgressBar();

    }

}
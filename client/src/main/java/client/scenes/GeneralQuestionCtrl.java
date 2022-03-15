package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;

import commons.CommonUtils;
import commons.Question;
import javafx.scene.image.Image;

public class GeneralQuestionCtrl extends MultipleChoiceQuestionCtrl {

    /**
     * Creates a GeneralQuestionCtrl, which controls the display/interaction of the general question screen.
     * @param server Utilities for communicating with the server (API endpoint)
     * @param mainCtrl The main control which is used for calling methods to switch scenes
     * @param utils Common utilities (for server- and client-side)
     */
    @Inject
    public GeneralQuestionCtrl(ServerUtils server, MainCtrl mainCtrl, CommonUtils utils) {
        super(server, mainCtrl, utils);
    }

    /**
     * Gets a random question from the server and displays the question to the client. Also, restarts the progress bar.
     */
    public void loadQuestion(Question q) {

        questionImg.setImage(new Image(ServerUtils.getImageURL(q.activityImagePath)));
        title.setText(q.displayQuestion());
        answerBtn1.setText(q.answerOptions.get(0));
        answerBtn2.setText(q.answerOptions.get(1));
        answerBtn3.setText(q.answerOptions.get(2));
        refreshProgressBar();

    }

}
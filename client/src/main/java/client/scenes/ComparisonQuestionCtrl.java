package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.CommonUtils;
import commons.Question;
import javafx.scene.image.Image;

public class ComparisonQuestionCtrl extends MultipleChoiceQuestionCtrl {

    /**
     * Creates a ComparisonQuestionCtrl, which controls the display/interaction of the comparison question screen.
     *
     * @param server   Utilities for communicating with the server (API endpoint)
     * @param mainCtrl The main control which is used for calling methods to switch scenes
     * @param utils    Common utilities (for server- and client-side)
     */
    @Inject
    public ComparisonQuestionCtrl(ServerUtils server, MainCtrl mainCtrl, CommonUtils utils) {
        super(server, mainCtrl, utils);
    }

    /**
     * Gets a random question from the server and displays the question to the client. Also, restarts the progress bar.
     */
    @Override
    public void loadQuestion(Question q) {

        setPoints();
        enableButtons();
        disableJokers();
        question = q;
        questionImg.setImage(new Image(ServerUtils.getImageURL(q.activityImagePath)));
        title.setText(q.displayQuestion());
        resizeQuestionHandler.setText((int) title.getFont().getSize());

        super.loadQuestion(q);

    }

}

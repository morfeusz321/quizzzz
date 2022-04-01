package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.CommonUtils;
import commons.Question;
import commons.gameupdate.GameUpdateTransitionPeriodEntered;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.Image;

public class MostExpensiveQuestionCtrl extends MultipleChoiceQuestionCtrl {

    private Question currentQuestion;

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
     * Initializes the title
     */
    @FXML
    public void initialize(){
        super.initialize();
        title.setText("What is most expensive?");
        resizeQuestionHandler.setText((int) title.getFont().getSize());
    }

    /**
     * Gets a random question from the server and displays the question to the client. Also, restarts the progress bar.
     */
    @Override
    public void loadQuestion(Question q) {

        enableButtons();
        disableJokers();
        question = q;
        questionImg.setImage(new Image("/client/img/question_mark.png"));
        currentQuestion = q;
        
        super.loadQuestion(q);

    }

    /**
     * Shows transition screen (see method in superclass), and the image corresponding to the answer is displayed.
     * @param gameUpdate contains AnswerResponseEntity with correctness of user's answer
     */
    @Override
    public void enterTransitionScreen(GameUpdateTransitionPeriodEntered gameUpdate) {

        super.enterTransitionScreen(gameUpdate);
        Platform.runLater(() ->
                questionImg.setImage(new Image(ServerUtils.getImageURL(currentQuestion.activityImagePath))));

    }
}

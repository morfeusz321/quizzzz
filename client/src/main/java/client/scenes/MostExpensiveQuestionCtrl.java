package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.CommonUtils;
import commons.Question;
import javafx.fxml.FXML;
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

        // TODO: add "more expensive" question type, and restructure this afterwards

        enableButtons();
        questionImg.setImage(new Image("/client/img/question_mark.png"));

        super.loadQuestion(q);

    }

    // TODO: when the answer is displayed, the correct image should be shown!

}

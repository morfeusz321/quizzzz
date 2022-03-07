package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;

import commons.GeneralQuestion;
import commons.Question;
import javafx.fxml.FXML;
import javafx.scene.image.Image;

public class GeneralQuestionCtrl extends MultipleChoiceQuestionCtrl {

    /**
     * Creates a GeneralQuestionCtrl, which controls the display/interaction of the general question screen.
    * @param server Utilities for communicating with the server (API endpoint)
    * @param mainCtrl The main control which is used for calling methods to switch scenes
     */
    @Inject
    public GeneralQuestionCtrl(ServerUtils server, MainCtrl mainCtrl) {
        super(server, mainCtrl);
    }

    @FXML
    protected void initialize() {
        refresh();
    }

    /**
     * Refreshes the scene, i.e. handles initialization, images and progressbar.
     * Superclass handles everything except loading the question.
     */
    @Override
    public void refresh(){
        super.refresh();
        loadQuestion();
    }

    /**
     * Gets a random question from the server and displays the question to the client
     */
    public void loadQuestion() {

        Question q = server.getRandomQuestion();

        if(!(q instanceof GeneralQuestion)) {
            return; // Other question types not supported yet
        }

        questionImg.setImage(new Image(ServerUtils.getImageURL(q.activityImagePath)));
        title.setText(q.displayQuestion());
        answerBtn1.setText(q.answerOptions.get(0));
        answerBtn2.setText(q.answerOptions.get(1));
        answerBtn3.setText(q.answerOptions.get(2));

    }

}

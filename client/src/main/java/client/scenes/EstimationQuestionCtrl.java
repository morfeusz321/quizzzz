package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.GeneralQuestion;
import commons.Question;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.text.Text;

public class EstimationQuestionCtrl extends QuestionCtrl {

    @FXML
    private Text sliderLabel;
    @FXML
    private Slider slideBar;
    @FXML
    public Text answerDisplay;
    @FXML
    public Label minLabel;
    @FXML
    public Label maxLabel;

    /**
     * Creates a EstimationQuestionCtrl, which controls the display/interaction of the estimation question screen.
     * @param server Utilities for communicating with the server (API endpoint)
     * @param mainCtrl The main control which is used for calling methods to switch scenes
     */
    @Inject
    public EstimationQuestionCtrl(ServerUtils server, MainCtrl mainCtrl) {
        super(server, mainCtrl);
    }

    /**
     * Initializes the scene elements
     */
    @FXML
    public void initialize(){
        super.initialize();
        slideBar.setSnapToTicks(true);
        slideBar.valueProperty().addListener(
                (observableValue, oldValue, newValue) ->
                        answerDisplay.setText("Your answer: " + newValue.intValue())
        );
    }

    /**
     * Gets a random question from the server and displays the question to the client. Also, restarts the progress bar
     * (and TODO the initialization of the slider).
     */
    public void loadQuestion() {

        Question q = server.getRandomQuestion();

        if(!(q instanceof GeneralQuestion)) {
            return; // Other question types not supported yet
        }

        questionImg.setImage(new Image(ServerUtils.getImageURL(q.activityImagePath)));
        title.setText(q.displayQuestion());

        // TODO: handle slider and other question-dependent objects (max/min etc.)
        slideBar.setMax(200);
        maxLabel.setText("200");
        slideBar.setMin(0);
        minLabel.setText("0");
        answerDisplay.setText("Your answer: 0");
        slideBar.setMajorTickUnit(100);
        slideBar.setMinorTickCount(99);
        // must be one less than major tick unit -> one tick per kWh

        refreshProgressBar();

    }

}

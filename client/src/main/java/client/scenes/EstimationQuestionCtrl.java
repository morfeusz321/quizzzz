package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.CommonUtils;
import commons.Question;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
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
    @FXML
    public Button setAnswerBtn;
    @FXML
    public TextField answerTxtField;

    /**
     * Creates a EstimationQuestionCtrl, which controls the display/interaction of the estimation question screen.
     * @param server Utilities for communicating with the server (API endpoint)
     * @param mainCtrl The main control which is used for calling methods to switch scenes
     * @param utils Common utilities (for server- and client-side)
     */
    @Inject
    public EstimationQuestionCtrl(ServerUtils server, MainCtrl mainCtrl, CommonUtils utils) {
        super(server, mainCtrl, utils);
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
                        answerTxtField.setText(String.valueOf(newValue.intValue()))
        );
        answerTxtField.textProperty().addListener(
                (observableValue, oldValue, newValue) -> {
                    // special case of empty string (interpreted as 0)
                    if(newValue.equals("")){
                        answerTxtField.setText("0");
                        slideBar.setValue(0);
                        return;
                    }
                    // check whether it is an integer
                    int newInt;
                    try{
                        newInt = Integer.parseInt(newValue);
                    } catch (NumberFormatException e){
                        answerTxtField.setText(oldValue);
                        return;
                    }
                    // check boundaries
                    if((int) slideBar.getMax() < newInt || (int) slideBar.getMin() > newInt){
                        answerTxtField.setText(oldValue);
                        return;
                    }
                    slideBar.setValue(newInt);
                }
        );
    }

    /**
     * Gets a random question from the server and displays the question to the client. Also, restarts the progress bar
     * (and TODO the initialization of the slider).
     */
    public void loadQuestion(Question q) {

        questionImg.setImage(new Image(ServerUtils.getImageURL(q.activityImagePath)));
        title.setText(q.displayQuestion());

        // TODO: handle slider and other question-dependent objects (max/min etc.)
        slideBar.setMax(200);
        maxLabel.setText("200");
        slideBar.setMin(0);
        minLabel.setText("0");
        answerTxtField.setText("0");
        slideBar.setValue(0);
        slideBar.setMajorTickUnit(100);
        slideBar.setMinorTickCount(99);
        // must be one less than major tick unit -> one tick per kWh

        refreshProgressBar();

    }

}

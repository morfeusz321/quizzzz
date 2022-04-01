package client.scenes;

import client.utils.DynamicText;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.CommonUtils;
import commons.Question;
import commons.gameupdate.GameUpdateTransitionPeriodEntered;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

public class EstimationQuestionCtrl extends QuestionCtrl {

    @FXML
    private Text sliderLabel;
    @FXML
    private Slider slideBar;
    @FXML
    private Text answerDisplay;
    @FXML
    private Label minLabel;
    @FXML
    private Label maxLabel;
    @FXML
    private Button setAnswerBtn;
    @FXML
    private TextField answerTxtField;

    @FXML
    protected Label powersText;

    @FXML
    protected ImageView decreaseTime;

    @FXML
    protected ImageView doublePoints;

    private boolean answerSet;

    /**
     * Creates a EstimationQuestionCtrl, which controls the display/interaction of the estimation question screen.
     * @param server Utilities for communicating with the server (API endpoint)
     * @param mainCtrl The main control which is used for calling methods to switch scenes
     * @param utils Common utilities (for server- and client-side)
     */
    @Inject
    public EstimationQuestionCtrl(ServerUtils server, MainCtrl mainCtrl, CommonUtils utils) {
        super(server, mainCtrl, utils);
        answerSet = false;
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
                    if(newValue.equals("") && 0 <= slideBar.getMin()){
                        answerTxtField.setText(String.valueOf(slideBar.getMin()));
                        slideBar.setValue(slideBar.getMin());
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
        initializeAnswerBtnEventHandlers();
    }

    /**
     * Initializes the event handlers of the "set answer" button
     */
    public void initializeAnswerBtnEventHandlers(){
        setAnswerBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if(answerSet){
                answerSet = false;
                setAnswerBtn.setText("Set as answer");
                slideBar.setDisable(false);
                answerTxtField.setDisable(false);
            } else {
                answerSet = true;
                setAnswerBtn.setText("Edit answer");
                slideBar.setDisable(true);
                answerTxtField.setDisable(true);
                server.sendAnswerToServer((long) slideBar.getValue(), mainCtrl.getSavedUsernamePrefill());
            }
        });
        setAnswerBtn.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            setAnswerBtn.getStyleClass().add("hover-button");
            setAnswerBtn.getStyleClass().add("hover-cursor");

        });
        setAnswerBtn.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            setAnswerBtn.getStyleClass().remove("hover-button");
            setAnswerBtn.getStyleClass().remove("hover-cursor");
        });
    }

    /**
     * Gets a random question from the server and displays the question to the client. Also, restarts the progress bar
     * (and TODO the initialization of the slider).
     */
    public void loadQuestion(Question q) {
        enableButtons();
        disableJokers();
        setPoints();

        questionImg.setImage(new Image(ServerUtils.getImageURL(q.activityImagePath)));
        title.setText(q.displayQuestion());
        resizeQuestionHandler.setText((int) title.getFont().getSize());

        // TODO: handle slider and other question-dependent objects (max/min etc.)

        long min = Long.parseLong(q.answerOptions.get(0));
        long max = Long.parseLong(q.answerOptions.get(1));
        System.out.println(min + " " + max + " " + q.answerOptions.get(2));
        Text maxText = new Text();
        Text minText = new Text();
        maxText.setText(String.valueOf(max));
        minText.setText(String.valueOf(min));
        DynamicText maxTextDynamic = new DynamicText(maxText, 25, 10, "Karla");
        DynamicText minTextDynamic = new DynamicText(minText, 25, 10, "Karla");
        maxTextDynamic.setText((int)maxText.getFont().getSize());
        minTextDynamic.setText((int)minText.getFont().getSize());
        slideBar.setMax(max);
        maxLabel.setText(maxText.getText());
        slideBar.setMin(min);
        minLabel.setText(minText.getText());
        answerTxtField.setText(String.valueOf(min));
        slideBar.setValue(slideBar.getMin());
        answerSet = false;
        setAnswerBtn.setText("Set as answer");
        slideBar.setDisable(false);
        answerTxtField.setDisable(false);
        slideBar.setMajorTickUnit(100);
        slideBar.setMinorTickCount(99);
        // must be one less than major tick unit -> one tick per kWh

        refreshProgressBar();

    }

    /**
     * Disables the answer and power buttons, makes then power buttons invisible
     */
    @Override
    public void disableButtons(){
        powersText.setOpacity(0.2);
        decreaseTime.setOpacity(0.2);
        doublePoints.setOpacity(0.2);
        decreaseTime.setDisable(true);
        doublePoints.setDisable(true);
        slideBar.setDisable(true);
        answerTxtField.setDisable(true);
        setAnswerBtn.setDisable(true);
    }

    /**
     * when the timer counts down the transition screen is entered where user can see if they answered correctly and also can take a break
     * @param gameUpdate contains AnswerResponseEntity with correctness of user's answer
     */
    public void enterTransitionScreen(GameUpdateTransitionPeriodEntered gameUpdate) {
        disableButtons();
    }

    /**
     * Enables the answer and power buttons, makes then power buttons visible
     */
    public void enableButtons(){
        powersText.setOpacity(1);
        decreaseTime.setOpacity(1);
        doublePoints.setOpacity(1);
        decreaseTime.setDisable(false);
        doublePoints.setDisable(false);
        slideBar.setDisable(false);
        answerTxtField.setDisable(false);
        setAnswerBtn.setDisable(false);
    }

    /**
     * Disables joker buttons (if already used)
     */
    public void disableJokers() {
        if(mainCtrl.getJokerStatus(2)) {
            doublePoints.setDisable(true);
            doublePoints.setOpacity(0.3);
        }
        if(mainCtrl.getJokerStatus(3)) {
            decreaseTime.setDisable(true);
            decreaseTime.setOpacity(0.3);
        }
    }
}

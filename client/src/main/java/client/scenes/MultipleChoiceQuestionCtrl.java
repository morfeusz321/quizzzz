package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.AnswerResponseEntity;
import commons.CommonUtils;
import commons.Question;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.List;

public abstract class MultipleChoiceQuestionCtrl extends QuestionCtrl {
    @FXML
    private ImageView removeQuestion;

    @FXML
    protected Button answerBtn1;
    @FXML
    protected Button answerBtn2;
    @FXML
    protected Button answerBtn3;

    @FXML
    protected Label powersText;

    @FXML
    protected ImageView decreaseTime;

    @FXML
    protected ImageView doublePoints;

    @FXML
    protected Text correctAns;

    @FXML
    protected TextFlow fullText;

    Question question;

    /**
     * Creates a MultipleChoiceQuestionCtrl, which controls the display/interaction of all multiple choice question
     * screens. This includes the general question and the comparison question screens.
     * @param server Utilities for communicating with the server (API endpoint)
     * @param mainCtrl The main control which is used for calling methods to switch scenes
     * @param utils Common utilities (for server- and client-side)
     */
    @Inject
    public MultipleChoiceQuestionCtrl(ServerUtils server, MainCtrl mainCtrl, CommonUtils utils) {
        super(server, mainCtrl, utils);
    }

    /**
     * Initializes the scene elements
     */
    @FXML
    protected void initialize(){
        super.initialize();
        initializeAnswerEventHandlers();
    }

    /**
     * Loads all images, i.e. initializes the images of all ImageView objects.
     * Superclass handles everything except the remove-question power (this power does not exist for
     * the estimation question).
     */
    @Override
    protected void showImages(){
        super.showImages();
        removeQuestion.setImage(new Image("/client/img/minus_1_btn.png"));
    }

    /**
     * Initializes the event handlers of the powers
     * Superclass handles everything except the remove-question power (this power does not exist for
     * the estimation question).
     */
    @Override
    protected void initializePowerEventHandlers(){
        // TODO: add communication to server, this is only client-side for now (and only concerning visuals)
        super.initializePowerEventHandlers();

        removeQuestion.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> super.handlePower("remove question"));

        ColorAdjust hover = new ColorAdjust();
        hover.setBrightness(-0.05);
        hover.setSaturation(0.1);
        hover.setHue(-0.02);

        removeQuestion.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> removeQuestion.setEffect(hover));
        removeQuestion.addEventHandler(MouseEvent.MOUSE_EXITED, e -> removeQuestion.setEffect(null));
    }

    /**
     * Initializes the event handlers of all answer buttons
     */
    private void initializeAnswerEventHandlers(){

        List<Button> buttonList = List.of(answerBtn1, answerBtn2, answerBtn3);
        buttonList.forEach(this::addEventHandlersToAnswerButton);

    }

    /**
     * Gives an answer button its event handlers
     * @param btn the answer button to give event handlers to
     */
    private void addEventHandlersToAnswerButton(Button btn) {

        btn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> eventHandlerAnswerButtonMouseClicked(btn));
        btn.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> eventHandlerAnswerButtonMouseEntered(btn));
        btn.addEventHandler(MouseEvent.MOUSE_EXITED, e -> eventHandlerAnswerButtonMouseExited(btn));

    }

    /**
     * The event handler is called when an answer button is clicked
     * @param btn the button that was clicked
     */
    private void eventHandlerAnswerButtonMouseClicked(Button btn) {

        List<Button> buttonList = List.of(answerBtn1, answerBtn2, answerBtn3);
        buttonList.forEach(b -> {
                                    b.getStyleClass().remove("selected-answer");
                                    b.getStyleClass().remove("answerCorrect");
                                    b.getStyleClass().remove("answerIncorrect");
                                } );

        long selectedButton;
        if(btn.equals(answerBtn1)) {
            selectedButton = 1;
        } else if(btn.equals(answerBtn2)) {
            selectedButton = 2;
        } else if(btn.equals(answerBtn3)) {
            selectedButton = 3;
        } else {
            return;
        }

        AnswerResponseEntity answer = server.sendAnswerToServer(question, selectedButton);
        disableButtons();

        if(answer.correct) {
            placingTick(selectedButton);
            correctAns.setText("correctly");
            fullText.setOpacity(1);
            btn.getStyleClass().add("answerCorrect");
        } else {
            placingCross(selectedButton);
            correctAns.setText("incorrectly");
            fullText.setOpacity(1);
            btn.getStyleClass().add("answerIncorrect");
            buttonList.get((int) answer.getAnswer() - 1).getStyleClass().add("answerCorrect");
            placingTick( answer.getAnswer());
        }

    }

    /**
     *  places the tick next to the correct answer
     * @param num the number of the answer
     *            TO DO: change the text to an actual image.
     */
    private void placingTick(long num){
        switch((int) num){
            case 1:
                correctTick.setLayoutX(918);
                correctTick.setLayoutY(345);
                break;
            case 2:
                correctTick.setLayoutX(918);
                correctTick.setLayoutY(505);
                break;
            case 3:
                correctTick.setLayoutX(918);
                correctTick.setLayoutY(665);
                break;
        }
    }

    /**
     *  places the cross next an answer
     * @param num the number of the button
     *            TO DO: change the text to an actual image.
     */
    private void placingCross(long num){

        switch((int) num){
            case 1:
                wrongCross.setLayoutX(918);
                wrongCross.setLayoutY(345);
                break;
            case 2:
                wrongCross.setLayoutX(918);
                wrongCross.setLayoutY(505);
                break;
            case 3:
                wrongCross.setLayoutX(918);
                wrongCross.setLayoutY(665);
                break;
        }
        wrongCross.setOpacity(1);
    }

    /**
     * Disables the answer and power buttons, makes then power buttons invisible
     */
    private void disableButtons(){
        correctTick.setOpacity(1);
        powersText.setOpacity(0);
        decreaseTime.setOpacity(0);
        doublePoints.setOpacity(0);
        removeQuestion.setOpacity(0);
        decreaseTime.setDisable(true);
        doublePoints.setDisable(true);
        removeQuestion.setDisable(true);
        answerBtn1.setDisable(true);
        answerBtn2.setDisable(true);
        answerBtn3.setDisable(true);
    }

    /**
     * Enables the answer and power buttons, makes then power buttons visible
     */
    protected void enableButtons(){
        correctTick.setOpacity(0);
        wrongCross.setOpacity(0);
        fullText.setOpacity(0.0);
        powersText.setOpacity(1);
        decreaseTime.setOpacity(1);
        doublePoints.setOpacity(1);
        removeQuestion.setOpacity(1);
        decreaseTime.setDisable(false);
        doublePoints.setDisable(false);
        removeQuestion.setDisable(false);
        answerBtn1.setDisable(false);
        answerBtn2.setDisable(false);
        answerBtn3.setDisable(false);
        answerBtn1.getStyleClass().clear();
        answerBtn1.getStyleClass().add("text");
        answerBtn1.getStyleClass().add("question-button");
        answerBtn1.getStyleClass().add("button");
        answerBtn2.getStyleClass().clear();
        answerBtn2.getStyleClass().add("text");
        answerBtn2.getStyleClass().add("question-button");
        answerBtn2.getStyleClass().add("button");
        answerBtn3.getStyleClass().clear();
        answerBtn3.getStyleClass().add("text");
        answerBtn3.getStyleClass().add("question-button");
        answerBtn3.getStyleClass().add("button");
    }



    /**
     * The event handler called when the user hovers over an answer button
     * @param btn the button that was hovered over
     */
    private void eventHandlerAnswerButtonMouseEntered(Button btn) {

        btn.getStyleClass().add("hover-button");

    }

    /**
     * The event handler called when the user stops hovering over an answer button
     * @param btn the button that was stopped hovering over
     */
    private void eventHandlerAnswerButtonMouseExited(Button btn) {

        btn.getStyleClass().remove("hover-button");

    }
}

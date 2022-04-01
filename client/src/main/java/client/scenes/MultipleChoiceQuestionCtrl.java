package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.AnswerResponseEntity;
import commons.CommonUtils;
import commons.Question;
import commons.gameupdate.GameUpdateTransitionPeriodEntered;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public abstract class MultipleChoiceQuestionCtrl extends QuestionCtrl {
    @FXML
    protected ImageView removeQuestion;

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

    @FXML
    protected AnchorPane anchorPane;

    Question question;

    List<Button> buttonList;

    protected long lastSelectedButton;


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
     * Initializes the scene elements and the buttonList for answer buttons
     */
    @FXML
    protected void initialize(){
        buttonList = List.of(answerBtn1, answerBtn2, answerBtn3);
        buttonList.forEach(button -> {
            button.getStyleClass().add("button-font-size-1");
        });
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
        if(correctTick!=null && wrongCross!=null) {
            correctTick.setImage(new Image("/client/img/right_answer.png"));
            wrongCross.setImage(new Image("/client/img/wrong_answer.png"));
        }
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
        removeQuestion.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> super.emojiAnimation(doublePoints));

        ColorAdjust hover = new ColorAdjust();
        hover.setBrightness(-0.05);
        hover.setSaturation(0.1);
        hover.setHue(-0.02);

        removeQuestion.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            removeQuestion.setEffect(hover);
            removeQuestion.getStyleClass().add("hover-cursor");
        });
        removeQuestion.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            removeQuestion.setEffect(null);
            removeQuestion.getStyleClass().remove("hover-cursor");
        });
    }

    /**
     * Initializes the event handlers of all answer buttons
     */
    private void initializeAnswerEventHandlers(){

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

        buttonList.forEach(b -> {
                                    b.getStyleClass().remove("selected-answer");
                                    b.getStyleClass().remove("answerCorrect");
                                    b.getStyleClass().remove("answerIncorrect");
                                } );

        if(btn.equals(answerBtn1)) {
            lastSelectedButton = 1;
        } else if(btn.equals(answerBtn2)) {
            lastSelectedButton = 2;
        } else if(btn.equals(answerBtn3)) {
            lastSelectedButton = 3;
        } else {
            lastSelectedButton = 0;
            return;
        }

        btn.getStyleClass().add("selected-answer");
        server.sendAnswerToServer(lastSelectedButton, mainCtrl.getSavedUsernamePrefill());

    }

    /**
     * when the timer counts down the transition screen is entered where user can see if they answered correctly and also can take a break
     * @param gameUpdate contains AnswerResponseEntity with correctness of user's answer
     */
    public void enterTransitionScreen(GameUpdateTransitionPeriodEntered gameUpdate) {

        Platform.runLater(this::disableButtons);

        AnswerResponseEntity answer = gameUpdate.getAnswerResponseEntity();
        long correct = answer.getAnswer();
        fullText.setOpacity(1);
        correctTick.setOpacity(1);

        if(answer.correct) {
            Platform.runLater(() -> {
                placingTick(lastSelectedButton);
                correctAns.setText("You answered correctly!");
                buttonList.get((int) lastSelectedButton - 1).getStyleClass().add("answerCorrect");
                fullText.setLayoutX(anchorPane.getWidth()*0.1543248);
                fullText.setLayoutY(anchorPane.getHeight()*0.754867);
            });
        } else {
            if(lastSelectedButton==0) {
                Platform.runLater(() ->{
                    correctAns.setText("You did not answer. Try to be faster!");
                    placingTick(correct);
                   fullText.setLayoutX(anchorPane.getWidth()*0.1043248);
                   fullText.setLayoutY(anchorPane.getHeight()*0.754867);
                try {
                    buttonList.get((int) answer.getAnswer() - 1).getStyleClass().add("answerCorrect");
                } catch (IndexOutOfBoundsException ignored) {
                    // This shouldn't be here, otherwise the app could crash
                }
            });
            } else {
                Platform.runLater(() -> {
                    fullText.setLayoutX(anchorPane.getWidth() * 0.1543248);
                    fullText.setLayoutY(anchorPane.getHeight() * 0.754867);
                    placingCross(lastSelectedButton);
                    correctAns.setText("You answered incorrectly!");
                    try {
                        buttonList.get((int) lastSelectedButton - 1).getStyleClass().add("answerIncorrect");
                    } catch (IndexOutOfBoundsException ignored) {
                        // This is fine, no button was selected in this case
                    }
                    try {
                        buttonList.get((int) answer.getAnswer() - 1).getStyleClass().add("answerCorrect");
                    } catch (IndexOutOfBoundsException ignored) {
                        // This is very much not fine, probably indicates a bug in the
                        // answer response entity creation, or question generation, but there is hardly a
                        // better option than just continuing, otherwise the application will crash!
                    }
                    placingTick(correct);
                });
            }
        }

        (new Timer()).schedule(new TimerTask() {
            @Override
            public void run() {
                lastSelectedButton = 0;
            }
        }, 1000L);

    }

    /**
     *  places the tick next to the correct answer
     * @param num the number of the answer
     */
    private void placingTick(long num){
        switch ((int) num) {
            case 1 -> {
                correctTick.setLayoutX(anchorPane.getWidth() * 0.508125);
                correctTick.setLayoutY(anchorPane.getHeight() * 0.33250);
            }
            case 2 -> {
                correctTick.setLayoutX(anchorPane.getWidth() * 0.508125);
                correctTick.setLayoutY(anchorPane.getHeight() * 0.49750);
            }
            case 3 -> {
                correctTick.setLayoutX(anchorPane.getWidth() * 0.508125);
                correctTick.setLayoutY(anchorPane.getHeight() * 0.66517);
            }
        }
    }

    /**
     *  places the cross next an answer
     * @param num the number of the button
     */
    private void placingCross(long num){

        switch ((int) num) {
            case 1 -> {
                wrongCross.setLayoutX(anchorPane.getWidth() * 0.508125);
                wrongCross.setLayoutY(anchorPane.getHeight() * 0.33250);
            }
            case 2 -> {
                wrongCross.setLayoutX(anchorPane.getWidth() * 0.508125);
                wrongCross.setLayoutY(anchorPane.getHeight() * 0.49750);
            }
            case 3 -> {
                wrongCross.setLayoutX(anchorPane.getWidth() * 0.508125);
                wrongCross.setLayoutY(anchorPane.getHeight() * 0.66517);
            }
            default -> {
                // This is fine, just return. No button was selected in this case.
                return;
            }
        }
        wrongCross.setOpacity(1);
    }

    /**
     * Disables the answer and power buttons, makes then power buttons invisible
     */
    @Override
    public void disableButtons(){
        for(Button x : buttonList){
            x.setDisable(true);
        }
        powersText.setOpacity(0.3);
        decreaseTime.setOpacity(0.3);
        doublePoints.setOpacity(0.3);
        removeQuestion.setOpacity(0.3);
        decreaseTime.setDisable(true);
        doublePoints.setDisable(true);
        removeQuestion.setDisable(true);
    }

    /**
     * Enables the answer and power buttons, makes then power buttons visible
     */
    protected void enableButtons(){
        correctTick.setOpacity(0);
        fullText.setOpacity(0);
        wrongCross.setOpacity(0);
        powersText.setOpacity(1);
        decreaseTime.setOpacity(1);
        doublePoints.setOpacity(1);
        removeQuestion.setOpacity(1);
        decreaseTime.setDisable(false);
        doublePoints.setDisable(false);
        removeQuestion.setDisable(false);
        for(Button x : buttonList){
            x.setDisable(false);
            x.getStyleClass().clear();
            x.getStyleClass().add("text");
            x.getStyleClass().add("question-button");
            x.getStyleClass().add("button");
        }
    }



    /**
     * The event handler called when the user hovers over an answer button
     * @param btn the button that was hovered over
     */
    private void eventHandlerAnswerButtonMouseEntered(Button btn) {

        btn.getStyleClass().add("hover-button");
        btn.getStyleClass().add("hover-cursor");

    }

    /**
     * The event handler called when the user stops hovering over an answer button
     * @param btn the button that was stopped hovering over
     */
    private void eventHandlerAnswerButtonMouseExited(Button btn) {

        btn.getStyleClass().remove("hover-button");
        btn.getStyleClass().remove("hover-cursor");

    }

    /**
     * Gets a random question from the server and displays the question to the client. Also, restarts the progress bar.
     */
    protected void loadQuestion(Question q) {

        for(int i = 0; i < buttonList.size(); i++) {

            Button button = buttonList.get(i);
            String answer = q.answerOptions.get(i);
            if(answer.length() < 50 && !button.getStyleClass().contains("button-font-size-1")) {
                button.setText("");
                button.getStyleClass().remove("button-font-size-2");
                button.getStyleClass().remove("button-font-size-3");
                button.getStyleClass().add("button-font-size-1");
                button.setText(answer);
            } else if(answer.length() < 80 && !button.getStyleClass().contains("button-font-size-2")) {
                button.setText("");
                button.getStyleClass().remove("button-font-size-1");
                button.getStyleClass().remove("button-font-size-3");
                button.getStyleClass().add("button-font-size-2");
                button.setText(answer);
            } else if(!button.getStyleClass().contains("button-font-size-3")) {
                button.setText("");
                button.getStyleClass().remove("button-font-size-1");
                button.getStyleClass().remove("button-font-size-2");
                button.getStyleClass().add("button-font-size-3");
                button.setText(answer);
            } else {
                button.setText(answer);
            }

        }

        refreshProgressBar();

    }


    /**
     * Method for removing a wrong answer from the answer list
     */
    public void removeQuestion(int buttonNumber) {
        removeQuestion.setDisable(true);
        removeQuestion.setOpacity(0.3);
        mainCtrl.disableJoker(1);
        buttonList.get(buttonNumber-1).setDisable(true);
    }

    /**
     * Disables joker buttons (if already used)
     */
    public void disableJokers() {
        if(mainCtrl.getJokerStatus(1)) {
            removeQuestion.setDisable(true);
            removeQuestion.setOpacity(0.3);
        }
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

package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;

import commons.GeneralQuestion;
import commons.Question;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import java.util.List;

public class BasicQuestionCtrl extends QuestionCtrl {
    @FXML
    public ImageView removeQuestion;

    @FXML
    public Button answerBtn1;
    @FXML
    public Button answerBtn2;
    @FXML
    public Button answerBtn3;

    /**
     * Creates a BasicQuestionCtrl, which controls the display/interaction of the basic question screen.
    * @param server Utilities for communicating with the server (API endpoint)
    * @param mainCtrl The main control which is used for calling methods to switch scenes
     */
    @Inject
    public BasicQuestionCtrl(ServerUtils server, MainCtrl mainCtrl) {
        super(server, mainCtrl);
    }

    @FXML
    protected void initialize() {
        refresh();
    }

    /**
     * Refreshes the scene, i.e. handles initialization, images and progressbar.
     * Superclass handles everything except loading the question and initializing the answer buttons (this is
     * different for the estimation question).
     */
    @Override
    public void refresh(){
        super.refresh();
        initializeAnswerEventHandlers();
        loadQuestion();
    }

    /**
     * Loads all images, i.e. initializes the images of all ImageView objects.
     * Superclass handles everything except the remove-question power (this power does not exist for
     * the estimation question).
     */
    @Override
    public void showImages(){
        super.showImages();
        removeQuestion.setImage(new Image("/client/img/minus_1_btn.png"));
    }

    /**
     * Initializes the event handlers of the powers
     * Superclass handles everything except the remove-question power (this power does not exist for
     * the estimation question).
     */
    @Override
    public void initializePowerEventHandlers(){
        // TODO: add communication to server, this is only client-side for now (and only concerning visuals)
        super.initializePowerEventHandlers();

        removeQuestion.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> super.handlePower(decreaseTime));

        ColorAdjust hover = new ColorAdjust();
        hover.setBrightness(-0.05);
        hover.setSaturation(0.1);
        hover.setHue(-0.02);

        removeQuestion.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> removeQuestion.setEffect(hover));
        removeQuestion.addEventHandler(MouseEvent.MOUSE_EXITED, e -> removeQuestion.setEffect(null));
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

    /**
     * Initializes the event handlers of all answer buttons
     */
    public void initializeAnswerEventHandlers(){

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
     * The event handler called when an answer button is clicked
     * @param btn the button that was clicked
     */
    private void eventHandlerAnswerButtonMouseClicked(Button btn) {

        List<Button> buttonList = List.of(answerBtn1, answerBtn2, answerBtn3);
        buttonList.forEach(b -> b.getStyleClass().remove("selected-answer"));

        btn.getStyleClass().add("selected-answer");

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

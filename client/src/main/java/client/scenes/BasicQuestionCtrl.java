package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;

import commons.Question;
import javafx.animation.FadeTransition;
import javafx.animation.PathTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.List;

public class BasicQuestionCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    public ImageView backBtn;

    @FXML
    public Text title;

    @FXML
    public ImageView bgImage;

    @FXML
    public ImageView decreaseTime;
    @FXML
    public ImageView removeQuestion;
    @FXML
    public ImageView doublePoints;

    @FXML
    public Button answerBtn1;
    @FXML
    public Button answerBtn2;
    @FXML
    public Button answerBtn3;

    @FXML
    public ProgressBar timeBar;

    @FXML
    public Label questionInfo;
    @FXML
    public Label pointsInfo;
    @FXML
    public Label playersInfo;

    @FXML
    public ImageView questionImg;

    @FXML
    public ImageView hoverEmoji;
    @FXML
    public Pane emojiPane;

    @FXML
    public ImageView happyEmoji;
    @FXML
    public ImageView angryEmoji;
    @FXML
    public ImageView sadEmoji;
    @FXML
    public ImageView heartEmoji;
    @FXML
    public ImageView thumbsUpEmoji;

    @FXML
    public AnchorPane anchorPane;

    /**
     * Creates a BasicQuestionCtrl, which controls the display/interaction of the basic question screen.
    * @param server Utilities for communicating with the server (API endpoint)
    * @param mainCtrl The main control which is used for calling methods to switch scenes
     */
    @Inject
    public BasicQuestionCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    @FXML
    protected void initialize() {
         showImages();
         initializeAnswerEventHandlers();
         initializeEmojiEventHandlers();
         loadQuestion();
    }

    /**
     * Gets a random question from the server and displays the question to the client
     */
    public void loadQuestion() {

        Question q = server.getRandomQuestion();

        if(!(q.questionType == Question.QuestionType.GENERAL)) {
            return; // Other question types not supported yet
        }

        questionImg.setImage(new Image(ServerUtils.getImageURL(q.activityImagePath)));
        title.setText(q.displayQuestion());
        answerBtn1.setText(q.answerOptions.get(0));
        answerBtn2.setText(q.answerOptions.get(1));
        answerBtn3.setText(q.answerOptions.get(2));

    }

    /**
     * Loads all images, i.e. initializes the images of all ImageView objects
     */
    public void showImages(){
        backBtn.setImage(new Image("/client/img/back_btn.png"));
        decreaseTime.setImage(new Image("/client/img/clock_btn.png"));
        removeQuestion.setImage(new Image("/client/img/minus_1_btn.png"));
        doublePoints.setImage(new Image("/client/img/2x_btn.png"));
        hoverEmoji.setImage(new Image("/client/img/happy_lightbulb.png"));
        bgImage.setImage(new Image("/client/img/bg_img.png"));
        happyEmoji.setImage(new Image("/client/img/happy_lightbulb.png"));
        sadEmoji.setImage(new Image("/client/img/sad_lightbulb.png"));
        angryEmoji.setImage(new Image("/client/img/angry_lightbulb.png"));
        heartEmoji.setImage(new Image("/client/img/heart_emoji.png"));
        thumbsUpEmoji.setImage(new Image("/client/img/thumbs_up_emoji.png"));

        // TODO: show the real image here or set it in another method
        //  (right now the image is only static and inserted manually)
        questionImg.setImage(new Image("/client/img/dishwasher.jpg"));
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

    /**
     * Initializes the event handlers of all emojis
     */
    public void initializeEmojiEventHandlers(){
        // TODO: add communication to server, this is only client-side for now (and only concerning visuals)
        happyEmoji.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> emojiAnimation(happyEmoji));
        sadEmoji.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> emojiAnimation(sadEmoji));
        angryEmoji.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> emojiAnimation(angryEmoji));
        heartEmoji.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> emojiAnimation(heartEmoji));
        thumbsUpEmoji.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> emojiAnimation(thumbsUpEmoji));
    }

    /**
     * Displays an emoji animation for a specific emoji
     * @param clickedEmoji The image view of the emoji in the emoji pane
     */
    public void emojiAnimation(ImageView clickedEmoji){
        ImageView emoji = new ImageView(clickedEmoji.getImage());
        anchorPane.getChildren().add(emoji);
        emoji.toBack();

        double sizeRatio = 0.6; // should be <= 1
        emoji.setFitWidth(hoverEmoji.getFitWidth() * sizeRatio);
        emoji.setPreserveRatio(true);
        emoji.setLayoutX(hoverEmoji.getLayoutX() + 20);
        emoji.setLayoutY(hoverEmoji.getLayoutY());
        // TODO: when we do dynamic resizing of the window, the hardcoded values have to be changed

        Line line = new Line();
        line.setStartX(emoji.getFitWidth() / 2);
        line.setStartY(0);
        line.setEndX(emoji.getFitWidth() / 2);
        line.setEndY(-490);

        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.millis(900));
        pathTransition.setPath(line);
        pathTransition.setNode(emoji);
        pathTransition.setCycleCount(1);
        pathTransition.setAutoReverse(false);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(1100), emoji);
        fadeIn.setFromValue(0.7);
        fadeIn.setToValue(1.0);
        fadeIn.setCycleCount(1);
        fadeIn.setAutoReverse(false);

        pathTransition.play();
        fadeIn.play();

        fadeIn.setOnFinished(e -> {
            emoji.setVisible(false);
            anchorPane.getChildren().remove(emoji);
        });
    }

    /**
     * Switches back to the main menu
     */
    public void switchToMenu() {
        // TODO: when the menu screen is added, modify this
        mainCtrl.showOverview();
    }

    /**
     * Lets the player use the decrease time power-up
     */
    public void useDecreaseTime() {
        // TODO: implement functionality
        System.out.print("Used decrease time power");
    }

    /**
     * Lets the player use the remove answer power-up
     */
    public void useRemoveQuestion() {
        // TODO: implement functionality
        System.out.print("Used decrease time power");
    }

    /**
     * Lets the player use the double points power-up
     */
    public void useDoublePoints() {
        // TODO: implement functionality
        System.out.print("Used double points power");
    }

    /**
     * Sets visibility of emoji pane to visible and starts a fade- and path-transition (the emoji pane slides in)
     */
    public void emojisSlideIn() {
        displayEmojis();

        Line line = new Line();
        line.setStartX(740); // width of pane
        line.setStartY(160/2.0); // height of pane/2
        line.setEndX(740/2.0); // width of pane/2
        line.setEndY(160/2.0); // height of pane/2

        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.millis(350));
        pathTransition.setPath(line);
        pathTransition.setNode(emojiPane);
        pathTransition.setCycleCount(1);
        pathTransition.setAutoReverse(false);

        FadeTransition ft = new FadeTransition(Duration.millis(100), emojiPane);
        ft.setFromValue(0.3);
        ft.setToValue(1.0);
        ft.setCycleCount(1);
        ft.setAutoReverse(false);

        ft.play();
        pathTransition.play();
    }

    /**
     * Sets visibility of emoji pane to visible
     */
    public void displayEmojis() {
        emojiPane.setVisible(true);
    }

    /**
     * Sets visibility of emoji pane to invisible
     */
    public void hideEmojis() {
        emojiPane.setVisible(false);
    }
}

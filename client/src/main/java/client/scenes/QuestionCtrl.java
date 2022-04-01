package client.scenes;

import client.utils.DynamicText;
import client.utils.ServerUtils;
import com.google.inject.Inject;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.util.Duration;
import commons.CommonUtils;

import java.util.List;
import java.util.Random;

public abstract class QuestionCtrl {
    protected final ServerUtils server; // TODO: not sure if this is good style (using protected) - also next line
    protected final MainCtrl mainCtrl;

    private final CommonUtils utils;

    protected DynamicText resizeQuestionHandler;

    @FXML
    private ImageView backBtn;

    @FXML
    protected Text title;

    @FXML
    private ImageView bgImage;

    @FXML
    protected ImageView decreaseTime;
    @FXML
    protected ImageView doublePoints;

    @FXML
    private ProgressBar timeBar;

    @FXML
    private Label questionInfo;
    @FXML
    private Label pointsInfo;
    @FXML
    private Label playersInfo;

    @FXML
    protected ImageView questionImg;

    @FXML
    private ImageView hoverEmoji;
    @FXML
    private Pane emojiPane;

    @FXML
    private ImageView happyEmoji;
    @FXML
    private ImageView angryEmoji;
    @FXML
    private ImageView sadEmoji;
    @FXML
    private ImageView heartEmoji;
    @FXML
    private ImageView thumbsUpEmoji;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label timeLabel;

    @FXML
    protected ImageView correctTick;

    @FXML
    protected ImageView wrongCross;

    private List<ImageView> emojiList;

    private Timeline changeLabel;
    private Timeline timeAnim;

    /**
     * Creates a QuestionCtrl, which controls the display/interaction of the every question screen. Here, functionality
     * is handled that is shared for all different question types. The controls of those question type screens extend
     * from this class.
     *
     * @param server   Utilities for communicating with the server (API endpoint)
     * @param mainCtrl The main control which is used for calling methods to switch scenes
     * @param utils    Common utilities (for server- and client-side)
     */
    @Inject
    public QuestionCtrl(ServerUtils server, MainCtrl mainCtrl, CommonUtils utils) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.utils = utils;
    }

    /**
     * Initializes the scene, i.e. handles initialization, images
     */
    protected void initialize(){
        emojiList = List.of(happyEmoji, sadEmoji, angryEmoji, heartEmoji, thumbsUpEmoji);
        showImages();
        initializeEmojiEventHandlers();
        initializePowerEventHandlers();
        initializeBackButtonHandlers();
        initializEmojiHoverHandlers();
        dynamicTextQuestion();

    }

    /**
     * Refreshes the progressbar
     */
    protected void refreshProgressBar() {
        startProgressbar(15000);
    }

    /**
     * Loads all images, i.e. initializes the images of all ImageView objects
     */
    protected void showImages() {
        backBtn.setImage(new Image("/client/img/back_btn.png"));
        decreaseTime.setImage(new Image("/client/img/clock_btn.png"));
        doublePoints.setImage(new Image("/client/img/2x_btn.png"));
        hoverEmoji.setImage(new Image("/client/img/happy_lightbulb.png"));
        bgImage.setImage(new Image("/client/img/bg_img.png"));
        happyEmoji.setImage(new Image("/client/img/happy_lightbulb.png"));
        sadEmoji.setImage(new Image("/client/img/sad_lightbulb.png"));
        angryEmoji.setImage(new Image("/client/img/angry_lightbulb.png"));
        heartEmoji.setImage(new Image("/client/img/heart_emoji.png"));
        thumbsUpEmoji.setImage(new Image("/client/img/thumbs_up_emoji.png"));
    }

    /**
     * Initializes the event handlers of all emojis
     */
    private void initializeEmojiEventHandlers() {
        // TODO: add communication to server, this is only client-side for now (and only concerning visuals)
        happyEmoji.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> emojiAnimation(happyEmoji));
        sadEmoji.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> emojiAnimation(sadEmoji));
        angryEmoji.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> emojiAnimation(angryEmoji));
        heartEmoji.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> emojiAnimation(heartEmoji));
        thumbsUpEmoji.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> emojiAnimation(thumbsUpEmoji));
    }

    /**
     * Initializes the event handlers of the powers
     */
    protected void initializePowerEventHandlers() {
        // TODO: add communication to server, this is only client-side for now (and only concerning visuals)
        decreaseTime.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> handlePower("decrease time"));
        doublePoints.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> handlePower("double points"));

        ColorAdjust hover = new ColorAdjust();
        hover.setBrightness(-0.05);
        hover.setSaturation(0.1);
        hover.setHue(-0.02);

        decreaseTime.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            decreaseTime.setEffect(hover);
            decreaseTime.getStyleClass().add("hover-cursor");
        });
        doublePoints.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            doublePoints.setEffect(hover);
            doublePoints.getStyleClass().add("hover-cursor");
        });

        decreaseTime.addEventHandler(MouseEvent.MOUSE_EXITED, e ->{
            decreaseTime.setEffect(null);
            decreaseTime.getStyleClass().remove("hover-cursor");
        });
        doublePoints.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            doublePoints.setEffect(null);
            doublePoints.getStyleClass().remove("hover-cursor");
        });
    }

    /**
     * Initializes the event handlers of the back button
     */
    private void initializeBackButtonHandlers() {
        ColorAdjust hover = new ColorAdjust();
        hover.setBrightness(-0.05);
        hover.setSaturation(0.1);
        hover.setHue(-0.02);

        backBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            mainCtrl.exitWhileInTheGame();
        });
        // TODO: when the menu screen is added, modify this
        backBtn.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            backBtn.setEffect(hover);
            backBtn.getStyleClass().add("hover-cursor");
        });
        backBtn.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            backBtn.setEffect(null);
            backBtn.getStyleClass().remove("hover-cursor");
        });
    }

    /**
     * This handles the animation of the time-bar and the setting of the time-label ("time left: ", "time ran out").
     *
     * @param timeInMillis The time in milliseconds which the time-bar should take until the "time runs out"
     */
    private void startProgressbar(int timeInMillis) {
        timeBar.setProgress(1);
        int remainingTime = timeInMillis / 1000; // in seconds
        updateQuestionNumber();

        timeAnim = new Timeline(
                new KeyFrame(Duration.millis(timeInMillis), new KeyValue(timeBar.progressProperty(), 0))
        );
        changeLabel = new Timeline();
        for(int i = 0; i <= remainingTime; i++){
            int finalI = i;
            changeLabel.getKeyFrames().add(
                    new KeyFrame(Duration.seconds(remainingTime - i),
                            finished -> timeLabel.setText("Time left: 00:" + utils.addPrependingZero(finalI))));
        }
        changeLabel.setOnFinished(finished -> {
            timeLabel.setText("Time ran out!");
        });

        timeAnim.play();
        changeLabel.play();
    }

    /**
     * This handles the animation of the time-bar and the setting of the time-label after a time joker has been used
     */
    public void handleTimeJoker(long time) {
        timeAnim.getKeyFrames().clear();
        changeLabel.getKeyFrames().clear();
        int remainingTime = ((int) time)/1000;

        timeAnim = new Timeline(
                new KeyFrame(Duration.millis(time), new KeyValue(timeBar.progressProperty(), 0))
        );
        changeLabel = new Timeline();
        for(int i = 0; i <= remainingTime; i++){
            int finalI = i;
            changeLabel.getKeyFrames().add(
                    new KeyFrame(Duration.seconds(remainingTime - i),
                            finished -> timeLabel.setText("Time left: 00:" + utils.addPrependingZero(finalI))));
        }
        changeLabel.setOnFinished(finished -> {
            timeLabel.setText("Question is over!");
            Platform.runLater(this::disableButtons);
        });

        timeAnim.play();
        changeLabel.play();
    }

    /**
     * Disables the answer and power buttons, makes then power buttons invisible (overridden by child classes
     */
    public abstract void disableButtons();

    /**
     * Displays an emoji animation for a specific emoji
     *
     * @param clickedEmoji The image view which was clicked (emoji in the emoji pane)
     */
    public void emojiAnimation(ImageView clickedEmoji) {

        ImageView emoji = new ImageView(clickedEmoji.getImage());
        mainCtrl.sendEmoji(clickedEmoji.getId());
        anchorPane.getChildren().add(emoji);
        emoji.toBack();

        double sizeRatio = 0.78; // should be <= 1
        emoji.setFitWidth(hoverEmoji.getFitWidth() * sizeRatio);
        emoji.setPreserveRatio(true);
        emoji.setLayoutX(hoverEmoji.getLayoutX() + 20);
        emoji.setLayoutY(hoverEmoji.getLayoutY());

        Random r = new Random();
        CubicCurve cubic = new CubicCurve();
        cubic.setStartX(emoji.getFitWidth() / 2);
        cubic.setStartY(0);
        cubic.setControlX1(emoji.getFitWidth() / 2 + utils.randomIntInRange(-40, -20, r));
        cubic.setControlY1(utils.randomIntInRange(-125, -50, r));
        cubic.setControlX2(emoji.getFitWidth() / 2 + utils.randomIntInRange(20, 40, r));
        cubic.setControlY2(utils.randomIntInRange(-275, -175, r));
        cubic.setEndX(emoji.getFitWidth() / 2);
        cubic.setEndY(-300);

        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.millis(600));
        pathTransition.setPath(cubic);
        pathTransition.setNode(emoji);
        pathTransition.setCycleCount(1);
        pathTransition.setAutoReverse(false);

        emoji.opacityProperty().setValue(0.5);
        Timeline fade = new Timeline(
                new KeyFrame(Duration.millis(600), new KeyValue(emoji.opacityProperty(), 1)),
                new KeyFrame(Duration.millis(1050), new KeyValue(emoji.opacityProperty(), 1)),
                new KeyFrame(Duration.millis(1350), new KeyValue(emoji.opacityProperty(), 0))
        );
        fade.setAutoReverse(false);
        fade.setCycleCount(1);
        fade.setOnFinished(e -> {
            emoji.setVisible(false);
            anchorPane.getChildren().remove(emoji);
        });

        fade.play();
        pathTransition.play();
    }

    /**
     * TODO: handle powers here
     *
     * @param power A string describing the power that was clicked
     */
    public void handlePower(String power) {
        System.out.println(power + " was clicked.");
        // TODO: add (private) methods here for handling powers (which are for example called in a switch-statement).
        //  Alternatively we can also create classes for handling the powers and call methods of those classes here.

        switch (power) {
            case "remove question" -> removeQuestionJoker();
            case "double points" -> doublePoints();
            case "decrease time" -> decreaseTimeJoker();
        }
    }

    /**
     * Handling method for double points joker TODO: implement this once scores are working
     */
    private void doublePoints() {
        doublePoints.setDisable(true);
        doublePoints.setOpacity(0.3);
        mainCtrl.disableJoker(2);
    }

    private void removeQuestionJoker() {
        server.useQuestionJoker(mainCtrl.getSavedUsernamePrefill(),mainCtrl.getGameUUID());
    }

    /**
     * Handling method for decrease time joker
     */
    private void decreaseTimeJoker() {
        decreaseTime.setDisable(true);
        decreaseTime.setOpacity(0.3);
        server.useTimeJoker(mainCtrl.getSavedUsernamePrefill(), mainCtrl.getGameUUID());
        mainCtrl.disableJoker(3);
    }

    /**
     * Sets visibility of emoji pane to visible and starts a fade- and path-transition (the emoji pane slides in)
     */
    public void emojisSlideIn() {
        displayEmojis();

        Line line = new Line();
        line.setStartX(605); // width of pane
        line.setStartY(132 / 2.0); // height of pane/2
        line.setEndX(605 / 2.0); // width of pane/2
        line.setEndY(132 / 2.0); // height of pane/2

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
     * Updates the current number of question label
     */
    private void updateQuestionNumber() {
        questionInfo.setText("Question " + (mainCtrl.getGameManager().getQuestions().indexOf(mainCtrl.getGameManager().getCurrentQuestion()) + 1) + "/20");
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

    /**
     * sets the maximum height a question title can have
     */
    private void dynamicTextQuestion() {
        title.setText("");
        resizeQuestionHandler = new DynamicText(title, 170, 35, "System Bold Italic");
    }

    /**
     * Initializes the hover handlers for all emojis
     */
    private void initializEmojiHoverHandlers(){

        emojiList.forEach(this::addEventHandlersEmoji);

    }

    /**
     * Gives emojis its event handlers
     * @param emoji the emoji which is hovered
     */
    private void addEventHandlersEmoji(ImageView emoji) {

        emoji.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> eventHandlerEmojiMouseEntered(emoji));
        emoji.addEventHandler(MouseEvent.MOUSE_EXITED, e -> eventHandlerEmojiMouseExited(emoji));

    }

    /**
     * The event handler called when the user hovers over an emoji
     * @param emoji the emoji which the mouse has entered
     */
    private void eventHandlerEmojiMouseEntered(ImageView emoji) {
        ColorAdjust hover = new ColorAdjust();
        hover.setBrightness(-0.05);
        hover.setSaturation(0.1);
        hover.setHue(-0.02);

        emoji.setEffect(hover);
        emoji.getStyleClass().add("hover-cursor");

    }

    /**
     * The event handler called when the user stops hovering over an emoji
     * @param emoji the emoji that was stopped hovering over
     */
    private void eventHandlerEmojiMouseExited(ImageView emoji) {
        ColorAdjust hover = new ColorAdjust();
        hover.setBrightness(-0.05);
        hover.setSaturation(0.1);
        hover.setHue(-0.02);

        emoji.setEffect(null);
        emoji.getStyleClass().remove("hover-cursor");

    }
}

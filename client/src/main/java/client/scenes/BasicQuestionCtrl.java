package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;

import javafx.animation.*;
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

import java.util.Random;

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

    @FXML
    public Label timeLabel;

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
        refresh();
    }

    public void refresh(){
        showImages();
        initializeAnswerEventHandlers();
        initializeEmojiEventHandlers();
        startProgressbar(15000);
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
        answerBtn1.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> answerBtn1.getStyleClass().add("selected-answer"));
        answerBtn2.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> answerBtn2.getStyleClass().add("selected-answer"));
        answerBtn3.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> answerBtn3.getStyleClass().add("selected-answer"));

        answerBtn1.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> answerBtn1.getStyleClass().add("hover-button"));
        answerBtn2.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> answerBtn2.getStyleClass().add("hover-button"));
        answerBtn3.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> answerBtn3.getStyleClass().add("hover-button"));

        answerBtn1.addEventHandler(MouseEvent.MOUSE_EXITED, e -> answerBtn1.getStyleClass().remove("hover-button"));
        answerBtn2.addEventHandler(MouseEvent.MOUSE_EXITED, e -> answerBtn2.getStyleClass().remove("hover-button"));
        answerBtn3.addEventHandler(MouseEvent.MOUSE_EXITED, e -> answerBtn3.getStyleClass().remove("hover-button"));
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
     * This handles the animation of the time-bar and the setting of the time-label ("time left: ", "time ran out").
     * @param timeInMillis The time in milliseconds which the time-bar should take until the "time runs out"
     */
    public void startProgressbar(int timeInMillis){
        timeBar.setProgress(1);
        int remainingTime = timeInMillis / 1000; // in seconds

        Timeline timeAnim = new Timeline(
                new KeyFrame(Duration.millis(timeInMillis), new KeyValue(timeBar.progressProperty(), 0))
        );
        Timeline changeLabel = new Timeline();
        for(int i = 0; i <= remainingTime; i++){
            int finalI = i;
            changeLabel.getKeyFrames().add(
                    new KeyFrame(Duration.seconds(remainingTime - i),
                    finished -> timeLabel.setText("Time left: 00:" + addPrependingZero(finalI))));
        }
        changeLabel.setOnFinished(finished -> {
            timeLabel.setText("Time ran out!");
        });

        timeAnim.play();
        changeLabel.play();
    }

    /**
     * If necessary, this adds a prepending zero to a given integer. Used for displaying the timer.
     * @param i The integer to display
     * @return A string of the integer (if necessary, with a prepended zero)
     */
    public String addPrependingZero(int i){
        if(i < 10){
            return "0" + i;
        }
        return String.valueOf(i);
    }

    /**
     * Displays an emoji animation for a specific emoji
     * @param clickedEmoji The image view which was clicked (emoji in the emoji pane)
     */
    public void emojiAnimation(ImageView clickedEmoji){
        // TODO: when we do dynamic resizing of the window, the hardcoded values have to be changed

        ImageView emoji = new ImageView(clickedEmoji.getImage());
        anchorPane.getChildren().add(emoji);
        emoji.toBack();

        double sizeRatio = 0.6; // should be <= 1
        emoji.setFitWidth(hoverEmoji.getFitWidth() * sizeRatio);
        emoji.setPreserveRatio(true);
        emoji.setLayoutX(hoverEmoji.getLayoutX() + 20);
        emoji.setLayoutY(hoverEmoji.getLayoutY());

        CubicCurve cubic = new CubicCurve();
        cubic.setStartX(emoji.getFitWidth() / 2);
        cubic.setStartY(0);
        cubic.setControlX1(emoji.getFitWidth() / 2 + randomIntInRange(-20, -40));
        cubic.setControlY1(randomIntInRange(-50, -125));
        cubic.setControlX2(emoji.getFitWidth() / 2 + randomIntInRange(20, 40));
        cubic.setControlY2(randomIntInRange(-175, -275));
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
     * Returns a random integer in a given range. The bounds need to be either both negative or both positive.
     * @param lower The lower bound (inclusive)
     * @param upper The upper bound (inclusive)
     * @return A random integer in the given range.
     */
    public int randomIntInRange(int lower, int upper){
        Random r = new Random();
        if(lower < 0 && upper < 0){
            return r.nextInt(-1 * upper + lower) * -1 + lower;
        }
        return r.nextInt(upper - lower) + lower;
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

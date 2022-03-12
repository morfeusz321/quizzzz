package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
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

import java.util.Random;

public abstract class QuestionCtrl {
    protected final ServerUtils server; // TODO: not sure if this is good style (using protected) - also next line
    protected final MainCtrl mainCtrl;

    private final CommonUtils utils;

    @FXML
    public ImageView backBtn;

    @FXML
    public Text title;

    @FXML
    public ImageView bgImage;

    @FXML
    public ImageView decreaseTime;
    @FXML
    public ImageView doublePoints;

    @FXML
    public ProgressBar timeBar;

    @FXML
    public Label questionInfo;
    @FXML
    public Label pointsInfo;
    @FXML
    public Label playersInfo;

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
     * Creates a QuestionCtrl, which controls the display/interaction of the every question screen. Here, functionality
     * is handled that is shared for all different question types. The controls of those question type screens extend
     * from this class.
     * @param server Utilities for communicating with the server (API endpoint)
     * @param mainCtrl The main control which is used for calling methods to switch scenes
     * @param utils Common utilities (for server- and client-side)
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
    public void initialize(){
        showImages();
        initializeEmojiEventHandlers();
        initializePowerEventHandlers();
        initializeBackButtonHandlers();
    }

    /**
     * Refreshes the progressbar
     */
    public void refreshProgressBar(){
        startProgressbar(15000);
    }

    /**
     * Loads all images, i.e. initializes the images of all ImageView objects
     */
    public void showImages(){
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
    public void initializeEmojiEventHandlers(){
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
    public void initializePowerEventHandlers(){
        // TODO: add communication to server, this is only client-side for now (and only concerning visuals)
        decreaseTime.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> handlePower(decreaseTime));
        doublePoints.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> handlePower(doublePoints));

        ColorAdjust hover = new ColorAdjust();
        hover.setBrightness(-0.05);
        hover.setSaturation(0.1);
        hover.setHue(-0.02);

        decreaseTime.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> decreaseTime.setEffect(hover));
        doublePoints.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> doublePoints.setEffect(hover));

        decreaseTime.addEventHandler(MouseEvent.MOUSE_EXITED, e -> decreaseTime.setEffect(null));
        doublePoints.addEventHandler(MouseEvent.MOUSE_EXITED, e -> doublePoints.setEffect(null));
    }

    /**
     * Initializes the event handlers of the back button
     */
    private void initializeBackButtonHandlers() {
        ColorAdjust hover = new ColorAdjust();
        hover.setBrightness(-0.05);
        hover.setSaturation(0.1);
        hover.setHue(-0.02);

        backBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> mainCtrl.showOverview());
        // TODO: when the menu screen is added, modify this
        backBtn.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> backBtn.setEffect(hover));
        backBtn.addEventHandler(MouseEvent.MOUSE_EXITED, e -> backBtn.setEffect(null));
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
                            finished -> timeLabel.setText("Time left: 00:" + utils.addPrependingZero(finalI))));
        }
        changeLabel.setOnFinished(finished -> {
            timeLabel.setText("Time ran out!");
            mainCtrl.nextQuestion();
        });

        timeAnim.play();
        changeLabel.play();
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
     * @param clickedPower The image view of the power that was clicked
     */
    protected void handlePower(ImageView clickedPower) {
        // TODO: not sure if using protected is good style, this method is called in subclasses
        System.out.println(clickedPower.getImage().getUrl() + " was clicked.");
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

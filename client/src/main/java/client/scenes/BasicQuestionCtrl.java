package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;

import javafx.animation.FadeTransition;
import javafx.animation.PathTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.util.Duration;

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
        line.setStartX(980); // width of pane
        line.setStartY(110); // height of pane/2
        line.setEndX(465); // width of pane/2
        line.setEndY(110); // height of pane/2

        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.millis(450));
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

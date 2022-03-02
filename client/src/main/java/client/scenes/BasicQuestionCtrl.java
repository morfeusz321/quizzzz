package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;

import javafx.animation.FadeTransition;
import javafx.animation.PathTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
        this.backBtn.setImage(new Image("/client/img/back_btn.png"));
        this.decreaseTime.setImage(new Image("/client/img/clock_btn.png"));
        this.removeQuestion.setImage(new Image("/client/img/minus_1_btn.png"));
        this.doublePoints.setImage(new Image("/client/img/2x_btn.png"));
        this.hoverEmoji.setImage(new Image("/client/img/happy_lightbulb.png"));

        //TODO: show the real image here or set it in another method
        // (right now the image is only static and inserted manually)
        this.questionImg.setImage(new Image("/client/img/dishwasher.jpg"));
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
     * Selects answer 1
     * TODO: use mouseEvent to group selecting answers in one method (if that is possible)
     */
    public void selectAnswer1() {
        // TODO: implement functionality
        System.out.print("Clicked answer 1");
    }

    /**
     * Selects answer 2
     */
    public void selectAnswer2() {
        // TODO: implement functionality
        System.out.print("Clicked answer 2");
    }

    /**
     * Selects answer 3
     */
    public void selectAnswer3() {
        // TODO: implement functionality
        System.out.print("Clicked answer 3");
    }

    /**
     * Sets visibility of emoji pane to visible and starts a fade- and path-transition (the emoji pane slides in)
     */
    public void emojisSlideIn() {
        displayEmojis();

        Line line = new Line();
        line.setStartX(600);
        line.setStartY(100);
        line.setEndX(0);
        line.setEndY(100);

        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.millis(450));
        pathTransition.setPath(line);
        pathTransition.setNode(emojiPane);
        pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
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

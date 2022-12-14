package client.scenes;

import client.utils.AnimationUtils;
import client.utils.ModalFactory;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Score;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardCtrl {

    public enum LeaderboardCtrlState {
        MAIN_LEADERBOARD,
        MID_GAME_LEADERBOARD,
        END_GAME_LEADERBOARD
    }

    private final ServerUtils server;
    private final ModalFactory modalFactory;
    private final MainCtrl mainCtrl;
    private final AnimationUtils animation;

    private final List<String> usernameListInternal;
    private LeaderboardCtrlState state;

    @FXML
    private ImageView backBtn;

    @FXML
    private ImageView lightbulb;

    @FXML
    public ImageView speechBubble;

    @FXML
    private Text goodJobText;

    @FXML
    private Text speechBubbleText;

    @FXML
    private Text displayScore;

    @FXML
    private ListView<String> leaderboard;

    @FXML
    private TextField username;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Button find;

    /**
     * Constructor for this controller
     *
     * @param server       Utilities for communicating with the server (API endpoint)
     * @param modalFactory the modal factory to use
     * @param mainCtrl     The main control which is used for calling methods to switch scenes
     */
    @Inject
    public LeaderboardCtrl(ServerUtils server, ModalFactory modalFactory, MainCtrl mainCtrl) {
        this.server = server;
        this.modalFactory = modalFactory;
        this.mainCtrl = mainCtrl;
        this.usernameListInternal = new ArrayList<>();
        this.animation = new AnimationUtils();
    }

    /**
     * Initializes the back button
     */
    public void initialize() {
        backBtn.setImage(new Image("/client/img/back_btn.png"));
        backButtonHandler();
        findHandler();
    }

    /**
     * Back Button functionality
     */
    private void backButtonHandler() {
        ColorAdjust hover = new ColorAdjust();
        hover.setBrightness(-0.05);
        hover.setSaturation(0.1);
        hover.setHue(-0.02);

        backBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if(state == LeaderboardCtrlState.MID_GAME_LEADERBOARD || state == LeaderboardCtrlState.END_GAME_LEADERBOARD) {
                Alert alert = modalFactory.getModal(Alert.AlertType.CONFIRMATION, "Leaving?", "Do you want to leave?");
                alert.showAndWait();

                if(alert.getResult() == ButtonType.OK) {
                    server.disconnect();
                    goBackButton();
                }
            } else {
                server.disconnect();
                goBackButton();
            }
        });

        lightbulb.setImage(new Image("/client/img/animation/1.png"));

        speechBubble.setImage(new Image("/client/img/speech_bubble.png"));
        this.state = LeaderboardCtrlState.MAIN_LEADERBOARD;

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
     * Deletes all entries from the leaderboard display and then populates the list again
     * after a new leaderboard API request to the server
     */
    public void populateLeaderboard() {

        leaderboard.getItems().clear();

        List<Score> scores = server.getLeaderboard();
        for(int i = 0; i < scores.size(); i++) {
            Score s = scores.get(i);
            leaderboard.getItems().add((i + 1) + ". " + s.username + " (" + s.score + " pts)");
            usernameListInternal.add(s.username);
        }

    }

    /**
     * Populates the leaderboard with a given list of scores
     *
     * @param scores the list of scores to display
     */
    public void populateLeaderboard(List<Score> scores) {

        leaderboard.getItems().clear();

        for(int i = 0; i < scores.size(); i++) {
            Score s = scores.get(i);
            leaderboard.getItems().add((i + 1) + ". " + s.username + " (" + s.score + " pts)");
            usernameListInternal.add(s.username);
        }

    }

    /**
     * Scrolls to the username provided in the username input field of the leaderboard if the username can be found
     * in the leaderboard
     */
    public void find() {

        scrollTo(username.getText());

    }

    /**
     * Finds the given username in the leaderboard and scrolls to it if it can be found, or doesn't do anything
     * if it can't
     *
     * @param username the username to scroll to in the leaderboard
     */
    public void scrollTo(String username) {

        int idx = usernameListInternal.indexOf(username);

        if(idx == -1) {
            Alert alert = modalFactory.getModal(Alert.AlertType.INFORMATION, "Error", "", "User could not be found: \"" + username + "\"!");
            alert.showAndWait();
            return;
        }

        leaderboard.scrollTo(idx);

    }

    /**
     * if the game is ended the button for leaving the game should be enabled and the text should be adjusted
     */
    public void initializeButtonsForMainScreen() {
        if(state == LeaderboardCtrlState.END_GAME_LEADERBOARD) {
            lightbulb.setDisable(false);
            lightbulb.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                server.disconnect();
                fadeOutLeaderboard("gameAgain");
            });
            lightbulb.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> lightbulb.setCursor(Cursor.HAND));
            lightbulb.addEventHandler(MouseEvent.MOUSE_EXITED, e -> lightbulb.setCursor(Cursor.DEFAULT));
            goodJobText.setText("Good job!");
            speechBubbleText.setText(" CLICK on me and join another game!");
        } else if(state == LeaderboardCtrlState.MAIN_LEADERBOARD) {
            this.displayScore.setOpacity(0);
        }
    }

    /**
     * if the game is still going the buttons should be disabled
     */
    public void disableButtonsForMainScreen() {
        if(state == LeaderboardCtrlState.MID_GAME_LEADERBOARD) {
            lightbulb.setDisable(true);
            goodJobText.setText("Good job!");
            speechBubbleText.setText(" You're already halfway there!");
        }
    }

    /**
     * if the leaderboard is opened from main screen the text in the bubble should be changed
     */
    public void changeTextMainScreen() {
        if(state == LeaderboardCtrlState.MAIN_LEADERBOARD) {
            goodJobText.setText("");
            speechBubbleText.setText("Can you get to the top of the leaderboard?");
        }
    }

    /**
     * Sets the state of this controller which indicates which leaderboard should be shown (singleplayer or
     * multiplayer)
     *
     * @param state the desired leaderboard to display
     */
    public void setLeaderboardCtrlState(LeaderboardCtrlState state) {
        this.state = state;
    }

    /**
     * when clicking back button the user is redirected to the main page
     */
    public void goBackButton() {
        fadeOutLeaderboard("main");
    }

    /**
     * goes to the given scene and does fading animation
     *
     * @param nextScene
     */
    public void fadeOutLeaderboard(String nextScene) {
        animation.fadeOut(anchorPane, mainCtrl, nextScene);
    }

    /**
     * when entering the scene it does fading animation
     */
    public void fadeInLeaderboard() {
        animation.fadeIn(anchorPane);
    }

    /**
     * find Button hover effects
     */
    private void findHandler() {
        find.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            find.getStyleClass().add("hover-cursor");
            find.getStyleClass().add("hover-buttonDark");
        });
        find.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            find.getStyleClass().remove("hover-cursor");
            find.getStyleClass().remove("hover-buttonDark");
        });
    }

    /**
     * display teh current score of user
     *
     * @param points score of the user
     */
    public void setDisplayScore(int points) {
        this.displayScore.setOpacity(1);
        this.displayScore.setText("Your score is: " + points);
    }

}

package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Score;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Modality;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardCtrl {

    private ServerUtils server;
    private MainCtrl mainCtrl;

    private List<String> usernameListInternal;
    private boolean isEndLeaderboard;

    @FXML
    private ImageView backBtn;

    @FXML
    private ImageView lightbulb;

    @FXML
    public ImageView speechBubble;
    @FXML
    private Text speechBubbleText;

    @FXML
    private ListView<String> leaderboard;

    @FXML
    private TextField username;

    /**
     * Constructor for this controller
     * @param server Utilities for communicating with the server (API endpoint)
     * @param mainCtrl The main control which is used for calling methods to switch scenes
     */
    @Inject
    public LeaderboardCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.usernameListInternal = new ArrayList<>();
    }

    /**
     * Initializes the back button
     */
    public void initialize() {
        backBtn.setImage(new Image("/client/img/back_btn.png"));
        backBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            server.disconnect();
            mainCtrl.showMainScreen();
        });

        lightbulb.setImage(new Image("/client/img/animation/1.png"));

        speechBubble.setImage(new Image("/client/img/speech_bubble.png"));
        this.isEndLeaderboard = false;
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
     * Scrolls to the username provided in the username input field of the leaderboard if the username can be found
     * in the leaderboard
     */
    public void find() {

        scrollTo(username.getText());

    }

    /**
     * Finds the given username in the leaderboard and scrolls to it if it can be found, or doesn't do anything
     * if it can't
     * @param username the username to scroll to in the leaderboard
     */
    public void scrollTo(String username) {

        int idx = usernameListInternal.indexOf(username);

        if(idx == -1) {
            var alert = new Alert(Alert.AlertType.INFORMATION);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("User could not be found: \"" + username +"\"!");
            alert.showAndWait();
            return;
        }

        leaderboard.scrollTo(idx);

    }

    /**
     * if the game is ended the button for leaving the game should be enabled and the text should be adjusted
     */
    public void initializeButtonsForMainScreen(){
        if (isEndLeaderboard){
            lightbulb.setDisable(false);
            lightbulb.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                server.disconnect();
                mainCtrl.showMainScreen();
            });
            lightbulb.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> lightbulb.setCursor(Cursor.HAND));
            lightbulb.addEventHandler(MouseEvent.MOUSE_EXITED, e -> lightbulb.setCursor(Cursor.DEFAULT));
            speechBubbleText.setText(" If you want to join another game, CLICK ON ME!");
        }
    }

    /**
     * if the game is still going the buttons should be disabled
     */
    public void disableButtonsForMainScreen(){
        if (!isEndLeaderboard){
            lightbulb.setDisable(true);
            speechBubbleText.setText(" You're already halfway there!");
        }
    }

    /**
     * sets the variable isEndLeaderboard to true
     * which indicates that the last leaderboard is shown
     */
    public void setIsEndLeaderBoardTrue(boolean isGameFinished) {
        this.isEndLeaderboard = isGameFinished;
    }
}

package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Score;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.util.List;

public class LeaderboardCtrl {

    private ServerUtils server;
    private MainCtrl mainCtrl;

    private String goToScene;

    @FXML
    private ImageView backBtn;

    @FXML
    private ListView<String> leaderboard;

    /**
     * Constructor for this controller
     * @param server Utilities for communicating with the server (API endpoint)
     * @param mainCtrl The main control which is used for calling methods to switch scenes
     */
    @Inject
    public LeaderboardCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Initializes the back button
     */
    public void initialize() {
        backBtn.setImage(new Image("/client/img/back_btn.png"));
        backBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            mainCtrl.showMainScreen();
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
        }

    }

}

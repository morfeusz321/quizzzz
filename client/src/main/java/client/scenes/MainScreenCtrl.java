package client.scenes;

import client.utils.ServerUtils;
import commons.GameType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class MainScreenCtrl implements Initializable {

    protected GameType selectedGameType;

    @FXML
    private ImageView help;

    @FXML
    private ImageView leaderboard;
    @FXML
    private ImageView lightning;
    @FXML
    private ImageView lightbulb;

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    /**
     * Constructor for main screen controller, which controls the interaction of the (main) overview screen
     * @param server Utilities for communicating with the server (API endpoint)
     * @param mainCtrl The main control which is used for calling methods to switch scenes
     */
    @Inject
    public MainScreenCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Initializes the images on the main screen
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        leaderboard.setImage(new Image("/client/img/main_leaderboard.png"));
        lightning.setImage(new Image("/client/img/main_lightning.png"));
        lightbulb.setImage(new Image("/client/img/main_lightbulb.png"));
        help.setImage(new Image("/client/img/question_mark.png"));
    }

    /**
     * Starts the single player mode of the game
     * @param event click on singleplayer button
     */
    @FXML
    private void singlePlayer(ActionEvent event){

        this.selectedGameType = GameType.SINGLEPLAYER;
        mainCtrl.showUsernameInputScreen();

    }

    /**
     * Starts the multi player mode of the game
     * @param event click on multiplayer button
     */
    @FXML
    private void multiPlayer(ActionEvent event) {

        this.selectedGameType = GameType.MULTIPLAYER;
        mainCtrl.showUsernameInputScreen();

    }

    /**
     * Opens the admin screen
     */
    public void showAdmin() {
        mainCtrl.showAdmin();
    }

    /**
     * Opens the help screen
     */
    public void showHelp() {
        mainCtrl.showHelpScreen();
    }

}

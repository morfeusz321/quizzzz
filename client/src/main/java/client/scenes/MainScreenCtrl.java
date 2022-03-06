package client.scenes;

import client.utils.ServerUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class MainScreenCtrl implements Initializable {

    /* @FXML
    private Button singlePlayer;
    @FXML
    private Button multiPlayer; //TODO: multiplayer
    @FXML
    private Button help; */

    @FXML
    private ImageView leaderboard;
    @FXML
    private ImageView lightning;
    @FXML
    private ImageView lightbulb;

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    // TODO: change the size of the screen (1920x1080 required)
    /**
     * Constructor for main screen controller, which controls the interaction of the (main) overview screen
     * @param server Utilities for communicating with the server (API endpoint)
     * @param mainCtrl The main control which is used for calling methods to switch scenes
     */
    @Inject
    public MainScreenCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;

        //loadImages(); // TODO: I tried loading the images directly from the FXML file but it doesn't work, this solution is temporary until I figure out the correct path maybe?
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
    }

    /**
     * Starts the single player mode of the game TODO: should be changed to show another screen when everything is merged
     * @param event click on singleplayer button
     */
    @FXML
    private void singlePlayer(ActionEvent event){
        mainCtrl.showOverview();
    }

    //TODO: add multiplayer and help

}

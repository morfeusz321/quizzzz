package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Player;
import commons.gameupdate.*;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

import java.util.List;

public class WaitingRoomCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private ImageView backBtn;

    @FXML
    private ImageView lightbulb;

    @FXML
    public ImageView speechBubble;
    @FXML
    private TextFlow speechBubbleText;

    @FXML
    private ListView<String> playerList;

    @FXML
    private Text playersJoined;
    @FXML
    private ImageView lightningLeft;
    @FXML
    private ImageView lightningRight;

    private int numPlayers;
    private String thisUser;

    /**
     * Creates a WaitingRoomCtrl, which controls the display/interaction of the waiting room.
     * @param server Utilities for communicating with the server (API endpoint)
     * @param mainCtrl The main control which is used for calling methods to switch scenes
     */
    @Inject
    public WaitingRoomCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        numPlayers = 0;
        thisUser = "undef"; // default value, should only be displayed if something goes wrong
    }

    /**
     * Initializes the scene, i.e. handles initialization, images
     */
    @FXML
    private void initialize(){
        showImages();
        initializeBackButtonHandlers();
        initializeStartGameHandlers();
        initializeLightbulbAnimation();
    }

    /**
     * Loads all images, i.e. initializes the images of all ImageView objects
     */
    private void showImages(){

        backBtn.setImage(new Image("/client/img/back_btn.png"));
        lightbulb.setImage(new Image("/client/img/animation/1.png"));
        speechBubble.setImage(new Image("/client/img/speech_bubble.png"));
        lightningLeft.setImage(new Image("/client/img/back_btn.png"));
        lightningLeft.setScaleX(-1);
        lightningLeft.setRotate(5);
        lightningRight.setImage(new Image("/client/img/back_btn.png"));
        lightningRight.setRotate(-5);

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
                                                                    mainCtrl.sendLeaveMessageToServer();
                                                                    mainCtrl.showUsernameInputScreen();
                                                                });
        backBtn.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            backBtn.setEffect(hover);
            backBtn.setCursor(Cursor.HAND);
        });
        backBtn.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            backBtn.setEffect(null);
            backBtn.setCursor(Cursor.DEFAULT);
        });
    }

    /**
     * Initializes the event handlers for starting the game (and hovering effects)
     */
    private void initializeStartGameHandlers() {

        List<Node> elements = List.of(lightbulb, speechBubble, speechBubbleText);
        for(Node el : elements){
            el.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> server.startGame());
            el.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> el.setCursor(Cursor.HAND));
            el.addEventHandler(MouseEvent.MOUSE_EXITED, e -> el.setCursor(Cursor.DEFAULT));
        }

    }

    /**
     * Initializes the waving animation of the light bulb
     */
    private void initializeLightbulbAnimation() {

        Timeline waving = new Timeline();
        for(int i = 1; i < 24; i++){
            int finalI = i;
            waving.getKeyFrames().add(
                    new KeyFrame(Duration.millis(110 * i),
                            e -> lightbulb.setImage(new Image("/client/img/animation/" + finalI + ".png")))
            );
        }
        // 4 seconds to "starting" the next animation (i.e. displaying the waving)
        waving.getKeyFrames().add(
                new KeyFrame(Duration.millis(110 * 23 + 4000),
                        e -> lightbulb.setImage(new Image("/client/img/animation/23.png")))
        );
        waving.setCycleCount(Animation.INDEFINITE);
        waving.play();

    }

    /**
     * Adds a player to the list of players displayed in the waiting room
     * @param player the player to add to the list
     */
    protected void addPlayerToWaitingRoom(Player player) {

        if(!thisUser.equals(player.getUsername())){
            Platform.runLater(() -> playerList.getItems().add(player.getUsername()));
        } else {
            Platform.runLater(() -> playerList.getItems().add(0, "You: " + player.getUsername()));
        }
        numPlayers++;
        playersJoined.setText(numPlayers + " players joined:");

    }

    /**
     * Removes a player from the list of players displayed in the waiting room
     * @param player the player to remove from the list
     */
    protected void removePlayerFromWaitingRoom(Player player) {

        if(!thisUser.equals(player.getUsername())){
            Platform.runLater(() -> playerList.getItems().remove(player.getUsername()));
        } else {
            Platform.runLater(() -> playerList.getItems().remove("\u2015 You: " + player.getUsername() + " \u2015"));
        }
        numPlayers--;
        playersJoined.setText(numPlayers + " players joined:");

    }

    /**
     * Deletes all entries from the player list of the waiting room and then adds all
     * players in the player list in the game update
     * @param gameUpdateFullPlayerList the game update to load the new player list from
     * @param username The username of this client
     */
    protected void updateWaitingRoomPlayers(GameUpdateFullPlayerList gameUpdateFullPlayerList, String username) {

        playerList.getItems().clear();
        thisUser = username;
        for(Player p : gameUpdateFullPlayerList.getPlayerList()){
            if(!username.equals(p.getUsername())){
               playerList.getItems().add(playerList.getItems().size(), p.getUsername());
            } else {
                playerList.getItems().add(0, "\u2015 You: " + p.getUsername() + " \u2015");
            }
        }
        numPlayers = gameUpdateFullPlayerList.getPlayerList().size();
        playersJoined.setText(numPlayers + " players joined:");

    }
}

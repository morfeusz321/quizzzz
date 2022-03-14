package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.CommonUtils;
import commons.Player;
import commons.gameupdate.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class WaitingRoomCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private final CommonUtils utils;

    @FXML
    private ImageView backBtn;

    @FXML
    private ImageView lightbulb;

    @FXML
    private ListView<String> playerList;

    /**
     * Creates a WaitingRoomCtrl, which controls the display/interaction of the waiting room.
     * @param server Utilities for communicating with the server (API endpoint)
     * @param mainCtrl The main control which is used for calling methods to switch scenes
     * @param utils Common utilities (for server- and client-side)
     */
    @Inject
    public WaitingRoomCtrl(ServerUtils server, MainCtrl mainCtrl, CommonUtils utils) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.utils = utils;
    }

    /**
     * Initializes the scene, i.e. handles initialization, images
     */
    public void initialize(){
        showImages();
        initializeBackButtonHandlers();
        initializeLightbulbHandlers();
    }

    /**
     * Loads all images, i.e. initializes the images of all ImageView objects
     */
    public void showImages(){

        backBtn.setImage(new Image("/client/img/back_btn.png"));
        lightbulb.setImage(new Image("/client/img/happy_lightbulb.png"));

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
     * Initializes the event handlers of the light bulb
     */
    private void initializeLightbulbHandlers() {

        lightbulb.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> server.startGame());

    }

    /**
     * The handler for all incoming game updates via the WebSocket connection, such as players leaving or joining
     * @param gameUpdate the update for this game received from the WebSocket session
     */
    protected void gameUpdateHandler(GameUpdate gameUpdate) {

        System.out.print("Update received...\t");

        if(gameUpdate instanceof GameUpdatePlayerJoined) {
            System.out.print("Player joined: " + ((GameUpdatePlayerJoined) gameUpdate).getPlayer());
            Platform.runLater(() -> playerList.getItems().add(((GameUpdatePlayerJoined) gameUpdate).getPlayer().getUsername()));
        } else if(gameUpdate instanceof GameUpdatePlayerLeft) {
            System.out.print("Player left: " + ((GameUpdatePlayerLeft) gameUpdate).getPlayer());
            Platform.runLater(() -> playerList.getItems().remove(((GameUpdatePlayerLeft) gameUpdate).getPlayer().getUsername()));
        } else if(gameUpdate instanceof GameUpdateGameStarting) {
            System.out.print("GAME STARTING!");
        }

        System.out.println();

    }

    /**
     * Deletes all entries from the player list of the waiting room and then adds all
     * players in the player list in the game update
     * @param gameUpdateFullPlayerList the game update to load the new player list from
     */
    protected void updateWaitingRoomPlayers(GameUpdateFullPlayerList gameUpdateFullPlayerList) {

        playerList.getItems().removeAll();
        playerList.getItems().addAll(gameUpdateFullPlayerList.getPlayerList()
                                                                .stream()
                                                                .map(Player::getUsername)
                                                                .toList());

    }

}

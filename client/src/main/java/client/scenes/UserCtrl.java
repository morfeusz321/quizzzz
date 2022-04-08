/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.scenes;

import client.utils.AnimationUtils;
import client.utils.ModalFactory;
import client.utils.ServerUtils;
import client.utils.TextFieldSizeLimiter;
import com.google.inject.Inject;
import commons.GameType;
import commons.gameupdate.*;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.util.UUID;

public class UserCtrl {

    private final ServerUtils server;
    private final ModalFactory modalFactory;
    private final MainCtrl mainCtrl;
    private final WaitingRoomCtrl waitingRoomCtrl;
    public final AnimationUtils animation;

    private String currentUsername;
    private UUID gameUUID;

    @FXML
    private TextField username;

    @FXML
    private TextField serverAddress;

    @FXML
    private Text gameType;

    @FXML
    private ImageView backBtn;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Button join;

    /**
     * Constructor
     *
     * @param server       Utilities for communicating with the server (API endpoint)
     * @param modalFactory the modal factory to use
     * @param mainCtrl     The main control which is used for calling methods to switch scenes
     */
    @Inject
    public UserCtrl(ServerUtils server, ModalFactory modalFactory, MainCtrl mainCtrl, WaitingRoomCtrl waitingRoomCtrl) {
        this.server = server;
        this.modalFactory = modalFactory;
        this.mainCtrl = mainCtrl;
        this.waitingRoomCtrl = waitingRoomCtrl;
        this.animation = new AnimationUtils();
    }

    /**
     * Initializes the default text for the server address
     * and the back button functionality
     */
    public void initialize() {

        serverAddress.setText(mainCtrl.getSavedServerAddressPrefill());
        backButtonHandler();
        joinHandler();

        username.lengthProperty().addListener(new TextFieldSizeLimiter(username, MainCtrl.MAXIMUM_USERNAME_SIZE));

    }

    /**
     * Updates the server address input field to show the current server address prefill
     */
    public void updateServerAddressPrefill() {

        serverAddress.setText(mainCtrl.getSavedServerAddressPrefill());

    }

    /**
     * Updates the username input field to show the current username prefill
     */
    public void updateUsernamePrefill() {

        username.setText(mainCtrl.getSavedUsernamePrefill());

    }

    /**
     * sends the type of the game for the label to the fxml file
     */

    public void setTextGameType() {
        if(mainCtrl.getSelectedGameType() == GameType.MULTIPLAYER) {
            gameType.setText("MULTIPLAYER");
        } else gameType.setText("SINGLEPLAYER");
    }

    /**
     * Sends the server a request to join the current game with the username specified in the TextField in
     * the GUI, and registers for updates for that game if it can be joined.
     */

    public void join() {

        String un = getUserName();

        if(un.length() > MainCtrl.MAXIMUM_USERNAME_SIZE) {

            // This shouldn't be possible, but who knows what kind of tricks users can try

            Alert alert = modalFactory.getModal(Alert.AlertType.ERROR, "", "Provided username \"" + un + "\" is longer than the maximum username length of " +
                    MainCtrl.MAXIMUM_USERNAME_SIZE + "!");
            alert.showAndWait();
            return;

        }

        mainCtrl.setUsernamePrefill(un);
        mainCtrl.setServerAddressPrefill(getServer());

        GameUpdate gu;
        try {

            server.changeServer(getServer());

            if(mainCtrl.getSelectedGameType() == GameType.SINGLEPLAYER) {
                gu = server.joinSinglePlayerGame(un, true);
            } else if(mainCtrl.getSelectedGameType() == GameType.MULTIPLAYER) {
                gu = server.joinMultiplayerGame(un);
            } else {
                throw new IllegalArgumentException("Invalid game type!");
            }

        } catch(WebApplicationException | IllegalArgumentException e) {
            Alert alert = modalFactory.getModal(Alert.AlertType.ERROR, "", e.getMessage());
            alert.showAndWait();
            return;
        }

        if(!checkReturnedGameUpdateAfterJoinGame(gu, un)) return;

        if(gu instanceof GameUpdateFullPlayerList) {
            waitingRoomCtrl.updateWaitingRoomPlayers(((GameUpdateFullPlayerList) gu), un);
            this.gameUUID = ((GameUpdateFullPlayerList) gu).getGameUUID();
        }

        server.registerForGameUpdates(gameUUID, mainCtrl::gameUpdateHandler);
        server.setGameUUID(gameUUID);

        this.currentUsername = un;

        if(mainCtrl.getSelectedGameType() == GameType.SINGLEPLAYER){
            return;
        } else {
            fadeOutUser("wait");
        }


    }

    /**
     * Checks a game update to see if the client succeeded in joining a game,
     * and shows an error modal if it wasn't. Used by the join method in this controller,
     * but moved into this separate method because of the join method's length.
     *
     * @param gu the GameUpdate that was received from the server after attempting to join a game
     * @param un the username entered by the user in an attempt to join the game
     * @return shows an error modal if the GameUpdate was of the name in use or name too long type,
     * and returns false in that case, and true otherwise
     */
    private boolean checkReturnedGameUpdateAfterJoinGame(GameUpdate gu, String un) {

        if(gu instanceof GameUpdateNameInUse) {
            Alert alert = modalFactory.getModal(Alert.AlertType.ERROR, "Error", "", "Name \"" + un + "\" already in use!");
            alert.showAndWait();
            return false;
        }

        if(gu instanceof GameUpdateNameTooLong) {
            Alert alert = modalFactory.getModal(Alert.AlertType.ERROR, "Error", "", "Provided username \"" + un + "\" is longer than the maximum username length allowed" +
                    "by the server!");
            alert.showAndWait();
            return false;
        }

        return true;

    }

    /**
     * Returns the username entered by the player which has been used to join a game on the server
     *
     * @return the current username of the user registered to the server
     */
    public String getSavedCurrentUsername() {

        return currentUsername;

    }

    /**
     * Returns the UUID of the game that the user is currently in
     *
     * @return the UUID of the current game received from the server
     */
    public UUID getSavedGameUUID() {

        return gameUUID;

    }

    /**
     * gets the username
     *
     * @return String which is the username
     */
    private String getUserName() {
        return username.getText();
    }

    /**
     * gets the new server address
     *
     * @return String which is the new server address
     */
    private String getServer() {
        return serverAddress.getText();
    }

    /**
     * The back button functionality
     */
    private void backButtonHandler() {
        ColorAdjust hover = new ColorAdjust();
        hover.setBrightness(-0.05);
        hover.setSaturation(0.1);
        hover.setHue(-0.02);

        backBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> goBackButton());
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
     * the click of enter continues as join
     *
     * @param e a click of the user
     */
    public void keyPressed(KeyEvent e) {
        switch(e.getCode()) {
            case ENTER:
                join();
                break;
            default:
                break;
        }
    }

    /**
     * Loads the back button image, i.e. initializes the image of the back button
     */
    protected void showImage() {
        backBtn.setImage(new Image("/client/img/back_btn.png"));
    }

//    private void addToDatabase(String username){
//        server.addNewScoreToDB(username);
//    }

    /**
     * when clicking back button the user is redirected to the main page
     */
    public void goBackButton() {
        fadeOutUser("main");
    }

    /**
     * goes to the given scene and does fading animation
     *
     * @param nextScene
     */
    public void fadeOutUser(String nextScene) {
        animation.fadeOut(anchorPane, mainCtrl, nextScene);
    }

    /**
     * when entering the scene it does fading animation
     */
    public void fadeInUser() {
        animation.fadeIn(anchorPane);
    }

    /**
     * join button hover effects
     */
    private void joinHandler() {

        join.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            join.getStyleClass().add("hover-cursor");
            join.getStyleClass().add("hover-buttonDark");
        });
        join.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            join.getStyleClass().remove("hover-cursor");
            join.getStyleClass().remove("hover-buttonDark");
        });
    }

}
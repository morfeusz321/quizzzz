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

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.gameupdate.*;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;

import java.util.UUID;

public class UserCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private String currentUsername;
    private UUID gameUUID;

    @FXML
    private TextField username;

    /**
     * Constructor
     * @param server
     * @param mainCtrl
     */
    @Inject
    public UserCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     * Sends the server a request to join the current game with the username specified in the TextField in
     * the GUI, and registers for updates for that game if it can be joined.
     */
    public void join() {

        String un = getUserName();

        GameUpdate gu;
        try {
            gu = server.addUserName(un);
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }

        if(gu instanceof GameUpdateNameInUse) {
            System.out.println("Name in use!");
            return;
        }

        if(gu instanceof GameUpdateFullPlayerList) {
            System.out.println(((GameUpdateFullPlayerList) gu).getPlayerList());
            this.gameUUID = ((GameUpdateFullPlayerList) gu).getGameUUID();
        }

        server.registerForGameUpdates(gameUUID, this::gameUpdateHandler);

        this.currentUsername = un;

        /*
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                server.startGame();
            }
        }, 10000);
        */

    }

    /**
     * The handler for all incoming game updates via the WebSocket connection
     * @param gameUpdate the update for this game received from the WebSocket session
     */
    private void gameUpdateHandler(GameUpdate gameUpdate) {

        System.out.print("Update received...\t");

        if(gameUpdate instanceof GameUpdatePlayerJoined) {
            System.out.print("Player joined: " + ((GameUpdatePlayerJoined) gameUpdate).getPlayer());
        } else if(gameUpdate instanceof GameUpdatePlayerLeft) {
            System.out.print("Player left: " + ((GameUpdatePlayerLeft) gameUpdate).getPlayer());
        } else if(gameUpdate instanceof GameUpdateGameStarting) {
            System.out.print("GAME STARTING!");
        }

        System.out.println();

    }

    /**
     * Informs the server that the client is leaving the game
     */
    public void sendLeaveMessageToServer() {

        server.leaveGame(currentUsername, gameUUID);

    }

    /**
     * gets the username
     * @return String which is the username
     */
    private String getUserName() {
        return username.getText();
    }

    /**
     *  the click of enter continues as join
     * @param e a click of the user
     */
    public void keyPressed(KeyEvent e) {
        switch (e.getCode()) {
        case ENTER:
            join();
            break;
        default:
            break;
        }
    }
}
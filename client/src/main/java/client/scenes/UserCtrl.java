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
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;

public class UserCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField username;

    @FXML
    private TextField serverAddress;

    /**
     * Constructor
     * @param server Utilities for communicating with the server (API endpoint)
     * @param mainCtrl The main control which is used for calling methods to switch scenes
     */
    @Inject
    public UserCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;

    }

    /**
     * Initializes the default text for the server address
     */
    public void initialize() {
        serverAddress.setText("localhost:8080");
    }

    /**
     *  sends the username that the user has entered
     */
    public void join() {
        try {
            server.changeServer(getServer());
            server.addUserName(getUserName());
        } catch (WebApplicationException e) {

            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }

    }

    /**
     * gets the username
     * @return String which is the username
     */
    private String getUserName() {
        return username.getText();
    }

    /**
     * gets the new server address
     * @return String which is the new server address
     */
    private String getServer() {
        return serverAddress.getText();
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
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

import commons.Activity;
import commons.GameType;
import commons.GeneralQuestion;
import commons.Question;

import commons.gameupdate.GameUpdate;
import commons.gameupdate.GameUpdateGameStarting;
import commons.gameupdate.GameUpdatePlayerJoined;
import commons.gameupdate.GameUpdatePlayerLeft;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

public class MainCtrl {

    private final ServerUtils server;
    private Stage primaryStage;

    private MainScreenCtrl mainScreenCtrl;
    private Scene mainScreen;

    private GeneralQuestionCtrl generalQuestionCtrl;
    private Scene generalQuestion;

    private ComparisonQuestionCtrl comparisonQuestionCtrl;
    private Scene comparisonQuestion;

    private EstimationQuestionCtrl estimationQuestionCtrl;
    private Scene estimationQuestion;

    private UserCtrl userCtrl;
    private Scene username;

    private WaitingRoomCtrl waitingRoomCtrl;
    private Scene waitingRoom;

    private AdminCtrl adminCtrl;
    private Scene adminScene;

    private AdminEditActivityCtrl adminEditCtrl;
    private Scene adminEditScene;

    /**
     * Creates a MainCtrl, which controls displaying and switching between screens.
     * @param server Utilities for communicating with the server (API endpoint)
     */
    @Inject
    public MainCtrl(ServerUtils server) {
        this.server = server;
    }

    /**
     * Initialize the main control with the different scenes and controllers of each scene. This class
     * manages the switching between the scenes.
     * @param primaryStage The stage (i.e. window) for all scenes
     * @param mainScreen Pair of the control and the scene of the main screen of the game
     * @param username Pair of the control and the scene of the username input screen
     * @param generalQ Pair of the control and the scene of the general question
     * @param comparisonQ Pair of the control and the scene of the comparison question
     * @param estimationQ Pair of the control and the scene of the estimation question
     * @param waitingRoom Pair of the control and the scene of the waiting room
     * @param adminScene Pair of the control and the scene of the admin interface
     * @param adminEditScene Pair of the control and the scene of the admin interface's activity editor
     */
    public void initialize(Stage primaryStage,
                           Pair<MainScreenCtrl, Parent> mainScreen,
                           Pair<UserCtrl, Parent> username,
                           Pair<GeneralQuestionCtrl, Parent> generalQ,
                           Pair<ComparisonQuestionCtrl, Parent> comparisonQ,
                           Pair<EstimationQuestionCtrl, Parent> estimationQ,
                           Pair<WaitingRoomCtrl, Parent> waitingRoom,
                           Pair<AdminCtrl, Parent> adminScene,
                           Pair<AdminEditActivityCtrl, Parent> adminEditScene) {

        this.primaryStage = primaryStage;

        this.mainScreenCtrl = mainScreen.getKey();
        this.mainScreen = new Scene(mainScreen.getValue());
        this.mainScreen.getStylesheets().add(
                MainScreenCtrl.class.getResource(
                        "/client/stylesheets/main-style.css"
                ).toExternalForm());

        this.userCtrl = username.getKey();
        this.username = new Scene(username.getValue());

        this.generalQuestionCtrl = generalQ.getKey();
        this.generalQuestion = new Scene(generalQ.getValue());
        this.generalQuestion.getStylesheets().add(
                QuestionCtrl.class.getResource(
                        "/client/stylesheets/question-style.css"
                ).toExternalForm());
        this.generalQuestion.getStylesheets().add(
                QuestionCtrl.class.getResource(
                        "/client/stylesheets/screen-style.css"
                ).toExternalForm());

        this.comparisonQuestionCtrl = comparisonQ.getKey();
        this.comparisonQuestion = new Scene(comparisonQ.getValue());
        this.comparisonQuestion.getStylesheets().add(
                QuestionCtrl.class.getResource(
                        "/client/stylesheets/question-style.css"
                ).toExternalForm());
        this.comparisonQuestion.getStylesheets().add(
                QuestionCtrl.class.getResource(
                        "/client/stylesheets/screen-style.css"
                ).toExternalForm());

        this.estimationQuestionCtrl = estimationQ.getKey();
        this.estimationQuestion = new Scene(estimationQ.getValue());
        this.estimationQuestion.getStylesheets().add(
                QuestionCtrl.class.getResource(
                        "/client/stylesheets/question-style.css"
                ).toExternalForm());
        this.estimationQuestion.getStylesheets().add(
                QuestionCtrl.class.getResource(
                        "/client/stylesheets/screen-style.css"
                ).toExternalForm());

        this.waitingRoomCtrl = waitingRoom.getKey();
        this.waitingRoom = new Scene(waitingRoom.getValue());
        this.waitingRoom.getStylesheets().add(
                WaitingRoomCtrl.class.getResource(
                        "/client/stylesheets/waiting-room-style.css"
                ).toExternalForm());
        this.waitingRoom.getStylesheets().add(
                WaitingRoomCtrl.class.getResource(
                        "/client/stylesheets/screen-style.css"
                ).toExternalForm());

        this.adminCtrl = adminScene.getKey();
        this.adminScene = new Scene(adminScene.getValue());

        this.adminEditCtrl = adminEditScene.getKey();
        this.adminEditScene = new Scene(adminEditScene.getValue());

        initializeOnCloseEvents();

        showMainScreen();
        primaryStage.show();
    }

    /**
     * Initializes all the events that should happen upon sending a close request to
     * the primary stage, that is, clicking the red x button on the window
     */
    public void initializeOnCloseEvents() {

        primaryStage.setOnCloseRequest(event -> {
            sendLeaveMessageToServer();
            System.exit(0);
        });

    }

    /**
     * Shows the waiting room screen
     */
    public void showWaitingRoom() {
        primaryStage.setTitle("Waiting room");
        primaryStage.setScene(waitingRoom);
    }

    /**
     * Shows the general question screen and loads a new question
     */
    public void showGeneralQuestion(Question q) {
        primaryStage.setTitle("General question");
        primaryStage.setScene(generalQuestion);
        generalQuestionCtrl.loadQuestion(q);
        // TODO: display same question synchronously to all clients (this will probably be complicated)
    }

    /**
     * Shows the comparison question screen and loads a new question
     */
    public void showComparisonQuestion(Question q) {
        primaryStage.setTitle("Comparison question");
        primaryStage.setScene(comparisonQuestion);
        comparisonQuestionCtrl.loadQuestion(q);
        // TODO: display same question synchronously to all clients (this will probably be complicated)
    }

    /**
     * Shows the estimation question screen and loads a new question
     */
    public void showEstimationQuestion(Question q) {
        primaryStage.setTitle("Estimation question");
        primaryStage.setScene(estimationQuestion);
        estimationQuestionCtrl.loadQuestion(q);
        // TODO: display same question synchronously to all clients (this will probably be complicated)
    }

    /**
     * Shows the main screen scene
     */
    public void showMainScreen() {
        primaryStage.setTitle("Quizzz");
        primaryStage.setScene(mainScreen);
    }

    /**
     * Shows next question, the question type is selected randomly
     */
    public void nextQuestion() {
        Question q = server.getRandomQuestion();
        if(q instanceof GeneralQuestion) {
            showGeneralQuestion(q);
        }
        // TODO: other questions are not implemented yet, this has to be modified after that
    }

     /**
      * Shows the username input screen
     */
    public void showUsernameInputScreen() {

        primaryStage.setTitle("Username input");
        primaryStage.setScene(username);

        username.setOnKeyPressed(e -> userCtrl.keyPressed(e));

    }

    /**
     * Informs the server that the client is leaving the game
     */
    public void sendLeaveMessageToServer() {

        server.leaveGame(userCtrl.getSavedCurrentUsername(), userCtrl.getSavedGameUUID());

    }

    /**
     * The handler for all incoming game updates via the WebSocket connection, such as players leaving or joining
     * @param gameUpdate the update for this game received from the WebSocket session
     */
    protected void gameUpdateHandler(GameUpdate gameUpdate) {

        System.out.print("Update received...\t");

        if(gameUpdate instanceof GameUpdatePlayerJoined) {
            System.out.print("Player joined: " + ((GameUpdatePlayerJoined) gameUpdate).getPlayer());
            waitingRoomCtrl.addPlayerToWaitingRoom(((GameUpdatePlayerJoined) gameUpdate).getPlayer());
        } else if(gameUpdate instanceof GameUpdatePlayerLeft) {
            System.out.print("Player left: " + ((GameUpdatePlayerLeft) gameUpdate).getPlayer());
            waitingRoomCtrl.removePlayerFromWaitingRoom(((GameUpdatePlayerLeft) gameUpdate).getPlayer());
        } else if(gameUpdate instanceof GameUpdateGameStarting) {
            System.out.print("GAME STARTING!");
        }

        System.out.println();

    }

    /**
     * Returns the game type that has been selected by the user by clicking on either the singleplayer
     * or multiplayer button
     * @return the game type selected by the user
     */
    public GameType getSelectedGameType() {

        return mainScreenCtrl.selectedGameType;

    }

    /**
     * Show the admin screen (table with all activities)
     */
    public void showAdmin() {
        primaryStage.setTitle("Admin");
        primaryStage.setScene(adminScene);
        adminCtrl.refresh();
        adminCtrl.setScene(adminScene);
    }

    /**
     * Show the edit activity screen
     * @param activity a previously selected activity
     */
    public void showAdminEdit(Activity activity) {
        primaryStage.setTitle("Admin - Edit activity");
        primaryStage.setScene(adminEditScene);
        adminEditCtrl.setActivity(activity);
    }

}

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

import client.utils.GameManager;
import client.utils.ServerUtils;

import com.google.inject.Inject;

import commons.*;

import commons.gameupdate.*;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

public class MainCtrl {

    private final ServerUtils server;
    private GameManager gameManager;
    private Stage primaryStage;

    private MainScreenCtrl mainScreenCtrl;
    private Scene mainScreen;

    private GeneralQuestionCtrl generalQuestionCtrl;
    private Scene generalQuestion;

    private MostExpensiveQuestionCtrl mostExpensiveQuestionCtrl;
    private Scene mostExpensiveQuestion;

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

    private ConnectToServerCtrl connectToServerCtrl;
    private Scene connectToServer;

    private LeaderboardCtrl leaderboardCtrl;
    private Scene leaderboard;

    private HelpScreenCtrl helpScreenCtrl;
    private Scene helpScene;

    private String usernamePrefill;
    private String serverAddressPrefill;

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
     * @param helpScreen Pair of the control and the scene of the help screen
     * @param username Pair of the control and the scene of the username input screen
     * @param generalQ Pair of the control and the scene of the general question
     * @param comparisonQ Pair of the control and the scene of the comparison question
     * @param estimationQ Pair of the control and the scene of the estimation question
     * @param mostExpensiveQ Pair of the control and the scene of the "most expensive" question
     * @param waitingRoom Pair of the control and the scene of the waiting room
     * @param adminScene Pair of the control and the scene of the admin interface
     * @param adminEditScene Pair of the control and the scene of the admin interface's activity editor
     * @param connectToServer Pair of the control and the scene of the "please connect to server" screen
     * @param leaderboard Pair of the control and the scene of the leaderboard screen
     */
    public void initialize(Stage primaryStage,
                           Pair<MainScreenCtrl, Parent> mainScreen,
                           Pair<HelpScreenCtrl, Parent> helpScreen,
                           Pair<UserCtrl, Parent> username,
                           Pair<GeneralQuestionCtrl, Parent> generalQ,
                           Pair<ComparisonQuestionCtrl, Parent> comparisonQ,
                           Pair<EstimationQuestionCtrl, Parent> estimationQ,
                           Pair<MostExpensiveQuestionCtrl, Parent> mostExpensiveQ,
                           Pair<WaitingRoomCtrl, Parent> waitingRoom,
                           Pair<AdminCtrl, Parent> adminScene,
                           Pair<AdminEditActivityCtrl, Parent> adminEditScene,
                           Pair<ConnectToServerCtrl, Parent> connectToServer,
                           Pair<LeaderboardCtrl, Parent> leaderboard) {

        this.primaryStage = primaryStage;

        this.primaryStage.getIcons().add(new Image("/client/img/lightbulb_icon.png"));

        this.mainScreenCtrl = mainScreen.getKey();
        this.mainScreen = new Scene(mainScreen.getValue());
        this.mainScreen.getStylesheets().add(
                MainScreenCtrl.class.getResource(
                        "/client/stylesheets/main-style.css"
                ).toExternalForm());

        this.helpScreenCtrl = helpScreen.getKey();
        this.helpScene = new Scene(helpScreen.getValue());
        this.helpScene.getStylesheets().add(
                MainScreenCtrl.class.getResource(
                        "/client/stylesheets/help-style.css"
                ).toExternalForm());

        this.userCtrl = username.getKey();
        this.username = new Scene(username.getValue());
        this.username.getStylesheets().add(
                MainScreenCtrl.class.getResource(
                        "/client/stylesheets/Input.css"
                ).toExternalForm());

        initializeQuestionControllersAndScenes(generalQ, comparisonQ, estimationQ, mostExpensiveQ);

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
        this.adminScene.getStylesheets().add(
                WaitingRoomCtrl.class.getResource(
                        "/client/stylesheets/admin-style.css"
                ).toExternalForm());

        this.adminEditCtrl = adminEditScene.getKey();
        this.adminEditScene = new Scene(adminEditScene.getValue());

        this.connectToServerCtrl = connectToServer.getKey();
        this.connectToServer = new Scene(connectToServer.getValue());

        this.leaderboardCtrl = leaderboard.getKey();
        this.leaderboard = new Scene(leaderboard.getValue());

        initializeOnCloseEvents();
        setUsernamePrefill(getUsernamePrefillFromFile());
        setServerAddressPrefill("localhost:8080");

        showMainScreen();

        primaryStage.setResizable(false);
        primaryStage.show();

    }

    /**
     * Initializes the question controllers and their respective scenes by adding them to this
     * class, and setting their stylesheets
     * @param generalQ Pair of the control and the scene of the general question
     * @param comparisonQ Pair of the control and the scene of the comparison question
     * @param estimationQ Pair of the control and the scene of the estimation question
     * @param mostExpensiveQ Pair of the control and the scene of the "most expensive" question
     */
    public void initializeQuestionControllersAndScenes(Pair<GeneralQuestionCtrl, Parent> generalQ,
                                                     Pair<ComparisonQuestionCtrl, Parent> comparisonQ,
                                                     Pair<EstimationQuestionCtrl, Parent> estimationQ,
                                                     Pair<MostExpensiveQuestionCtrl, Parent> mostExpensiveQ) {


        // TODO: this definitely needs restructuring, too much code duplication

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

        this.mostExpensiveQuestionCtrl = mostExpensiveQ.getKey();
        this.mostExpensiveQuestion = new Scene(mostExpensiveQ.getValue());
        this.mostExpensiveQuestion.getStylesheets().add(
                GeneralQuestionCtrl.class.getResource(
                        "/client/stylesheets/question-style.css"
                ).toExternalForm());
        this.mostExpensiveQuestion.getStylesheets().add(
                GeneralQuestionCtrl.class.getResource(
                        "/client/stylesheets/screen-style.css"
                ).toExternalForm());

    }

    /**
     * Initializes all the events that should happen upon sending a close request to
     * the primary stage, that is, clicking the red x button on the window
     */
    public void initializeOnCloseEvents() {

        primaryStage.setOnCloseRequest(event -> {
            sendLeaveMessageToServer();
            saveUsernamePrefillToFile(usernamePrefill);
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
     * Shows the general question screen
     */
    public void showGeneralQuestion(Question q) {
        primaryStage.setTitle("General question");
        primaryStage.setScene(generalQuestion);
        generalQuestionCtrl.loadQuestion(q);
        // TODO: display same question synchronously to all clients (this will probably be complicated)
    }

    /**
     * Shows the comparison question screen
     */
    public void showComparisonQuestion(Question q) {
        primaryStage.setTitle("Comparison question");
        primaryStage.setScene(comparisonQuestion);
        comparisonQuestionCtrl.loadQuestion(q);
        // TODO: display same question synchronously to all clients (this will probably be complicated)
    }

    /**
     * Shows the "most expensive" question screen
     */
    public void showMostExpensiveQuestion(Question q) {
        primaryStage.setTitle("Most expensive question");
        primaryStage.setScene(mostExpensiveQuestion);
        mostExpensiveQuestionCtrl.loadQuestion(q);
        // TODO: display same question synchronously to all clients (this will probably be complicated)
    }

    /**
     * Shows the estimation question screen
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
     * Shows the help screen scene
     */
    public void showHelpScreen() {
        primaryStage.setTitle("Help");
        primaryStage.setScene(helpScene);
    }

    /**
     * Shows next question, the question type is selected randomly
     */
    public void nextQuestion(Question q) {
        if(q instanceof GeneralQuestion) {
            showGeneralQuestion(q);
        } else if(q instanceof ComparisonQuestion) {
            showComparisonQuestion(q);
        } else if(q instanceof EstimationQuestion) {
            showEstimationQuestion(q);
        } else if(q instanceof WhichIsMoreQuestion) {
            showMostExpensiveQuestion(q);
        }
    }

     /**
      * Shows the username input screen
     */
    public void showUsernameInputScreen() {

        primaryStage.setTitle("Username input");

        primaryStage.setScene(username);
        userCtrl.setTextGameType();
        userCtrl.showImage();
        username.setOnKeyPressed(e -> userCtrl.keyPressed(e));

        userCtrl.updateServerAddressPrefill();
        userCtrl.updateUsernamePrefill();

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
            server.setInGameTrue();
            gameManager = new GameManager(); // "reset" game manager, because a new game is started
            gameManager.setQuestions(server.getQuestions());
            gameManager.setCurrentQuestionByIdx(0); // set the first question
            server.registerForGameLoop(this::incomingQuestionHandler, getSavedUsernamePrefill());
        }

        System.out.println();

    }

    /**
     * Handles updates incoming from the game long poll loop, displaying the right question or other screens
     * when it is necessary
     * @param gameUpdate the incoming game update
     */
    private void incomingQuestionHandler(GameUpdate gameUpdate) {

        if(gameUpdate instanceof GameUpdateGameFinished gameUpdateGameFinished) {

            // This game update can later contain metadata about the game like scores or anything
            // else the client would want to display after the game ends
            // TODO: for now this just goes to the main screen
            Platform.runLater(this::showMainScreen);

        } else if(gameUpdate instanceof GameUpdateNextQuestion gameUpdateNextQuestion) {

            gameManager.setCurrentQuestionByIdx(gameUpdateNextQuestion.getQuestionIdx());
            Platform.runLater(() -> nextQuestion(gameManager.getCurrentQuestion()));

        } else if(gameUpdate instanceof GameUpdateTransitionPeriodEntered gameUpdateTransitionPeriodEntered) {

            // TODO: display transition screen, this gameupdate already contains an answer response entity w/ the necessary information for the screen

            System.out.println("transition period");

        } else if(gameUpdate instanceof GameUpdateDisplayLeaderboard gameUpdateDisplayLeaderboard) {

            // TODO: display the transition leaderboard, this gameupdate contains the score list

            System.out.println("leaderboard");

        }

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
     * Sets the username prefill to be used throughout the application. The username prefill
     * is a String of text that is automatically entered for the user everywhere a username
     * can be entered, such as when joining a game. This way, the player doesn't have to re-enter
     * their username every time
     * @param usernamePrefill the username prefill to be used
     */
    public void setUsernamePrefill(String usernamePrefill) {

        this.usernamePrefill = usernamePrefill;

    }

    /**
     * Returns the username last entered by the player to join a server.
     * @return the current username prefill to be used
     */
    public String getSavedUsernamePrefill() {

        return this.usernamePrefill;

    }

    /**
     * Sets the server address prefill to be used throughout the application. The server address prefill
     * is a String of text that is automatically entered for the user everywhere a server address
     * can be entered, such as when joining a game. This way, the player doesn't have to remember
     * and type in the server address everywhere.
     * @param serverAddressPrefill the server address prefill to be used
     */
    public void setServerAddressPrefill(String serverAddressPrefill) {

        this.serverAddressPrefill = serverAddressPrefill;

    }

    /**
     * Returns the server address last entered by the player to attempt to join a server or otherwise connect
     * to a server. Note that the server address prefill thus also changes even if the URL was invalid or if a
     * connection could not be established.
     * @return the current server address prefill to be used
     */
    public String getSavedServerAddressPrefill() {

        return this.serverAddressPrefill;

    }

    /**
     * Show the admin screen (table with all activities) after verifying the server address.
     */
    public void showAdmin() {

        primaryStage.setTitle("Connect to server");
        connectToServerCtrl.updateServerAddressPrefill();
        connectToServerCtrl.setGoToScene(AdminCtrl.class.getName());
        primaryStage.setScene(connectToServer);

    }

    /**
     * This method is called by the connect to server screen. It shows the admin screen (table with all activities)
     * after having confirmed the server address.
     */
    public void showAdminServerConfirmed() {

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
        adminEditCtrl.clear();
        adminEditCtrl.setActivity(activity);
    }

    /**
     * Show the leaderboard screen after verifying the server address.
     */
    public void showLeaderboard() {

        primaryStage.setTitle("Connect to server");
        connectToServerCtrl.updateServerAddressPrefill();
        connectToServerCtrl.setGoToScene(LeaderboardCtrl.class.getName());
        primaryStage.setScene(connectToServer);

    }

    /**
     * This method is called by the connect to server screen. It shows the leaderboard screen
     * after having confirmed the server address.
     */
    public void showLeaderboardServerConfirmed() {

        primaryStage.setTitle("Leaderboard");
        primaryStage.setScene(leaderboard);

        leaderboardCtrl.populateLeaderboard();

    }

    /**
     * Loads the last used username by the player from a file that was created when
     * the application was last closed
     * @return the username loaded from the file
     */
    public String getUsernamePrefillFromFile() {

        Scanner fileReader;

        try {
            URI uri = MainCtrl.class.getResource("/client/data/data.quizzz").toURI();
            File data = new File(uri);
            fileReader = new Scanner(data);
        } catch (NullPointerException | URISyntaxException | FileNotFoundException e) {
            return "";
        }

        while(fileReader.hasNextLine()) {
            String line = fileReader.nextLine();
            if(line.startsWith("username: ")) {
                return line.split(": ")[1];
            }
        }

        return "";

    }

    /**
     * Saves the currently stored username prefill to a file, to be loaded again
     * when the application next starts up
     * @param username the username to store in the file
     */
    public void saveUsernamePrefillToFile(String username) {

        try {

            String filePath = MainCtrl.class.getResource("/client/data/").toExternalForm();
            filePath += "data.quizzz";
            URI uri = URI.create(filePath);

            File data = new File(uri);

            FileWriter fileWriter = new FileWriter(data, false);
            fileWriter.write("username: " + username);
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

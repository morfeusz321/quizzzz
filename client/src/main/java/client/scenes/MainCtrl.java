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
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Scanner;

public class MainCtrl {

    protected static final int MAXIMUM_USERNAME_SIZE = 15;

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
     *
     * @param server Utilities for communicating with the server (API endpoint)
     */
    @Inject
    public MainCtrl(ServerUtils server) {
        this.server = server;
    }

    /**
     * Initialize the main control with the different scenes and controllers of each scene. This class
     * manages the switching between the scenes.
     *
     * @param primaryStage    The stage (i.e. window) for all scenes
     * @param mainScreen      Pair of the control and the scene of the main screen of the game
     * @param helpScreen      Pair of the control and the scene of the help screen
     * @param username        Pair of the control and the scene of the username input screen
     * @param generalQ        Pair of the control and the scene of the general question
     * @param comparisonQ     Pair of the control and the scene of the comparison question
     * @param estimationQ     Pair of the control and the scene of the estimation question
     * @param mostExpensiveQ  Pair of the control and the scene of the "most expensive" question
     * @param waitingRoom     Pair of the control and the scene of the waiting room
     * @param adminScene      Pair of the control and the scene of the admin interface
     * @param adminEditScene  Pair of the control and the scene of the admin interface's activity editor
     * @param connectToServer Pair of the control and the scene of the "please connect to server" screen
     * @param leaderboard     Pair of the control and the scene of the leaderboard screen
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
        this.mainScreen.setFill(Color.valueOf("#F0EAD6"));

        this.helpScreenCtrl = helpScreen.getKey();
        this.helpScene = new Scene(helpScreen.getValue());
        this.helpScene.setFill(Color.valueOf("#F0EAD6"));

        this.userCtrl = username.getKey();
        this.username = new Scene(username.getValue());
        this.username.setFill(Color.valueOf("#F0EAD6"));

        initializeQuestionControllersAndScenes(generalQ, comparisonQ, estimationQ, mostExpensiveQ);

        this.waitingRoomCtrl = waitingRoom.getKey();
        this.waitingRoom = new Scene(waitingRoom.getValue());
        this.waitingRoom.setFill(Color.valueOf("#F0EAD6"));

        this.adminCtrl = adminScene.getKey();
        this.adminScene = new Scene(adminScene.getValue());
        this.adminScene.setFill(Color.valueOf("#F0EAD6"));

        this.adminEditCtrl = adminEditScene.getKey();
        this.adminEditScene = new Scene(adminEditScene.getValue());
        this.adminEditScene.setFill(Color.valueOf("#F0EAD6"));

        this.connectToServerCtrl = connectToServer.getKey();
        this.connectToServer = new Scene(connectToServer.getValue());
        this.connectToServer.setFill(Color.valueOf("#F0EAD6"));

        this.leaderboardCtrl = leaderboard.getKey();
        this.leaderboard = new Scene(leaderboard.getValue());
        this.leaderboard.setFill(Color.valueOf("#F0EAD6"));

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
     *
     * @param generalQ       Pair of the control and the scene of the general question
     * @param comparisonQ    Pair of the control and the scene of the comparison question
     * @param estimationQ    Pair of the control and the scene of the estimation question
     * @param mostExpensiveQ Pair of the control and the scene of the "most expensive" question
     */
    public void initializeQuestionControllersAndScenes(Pair<GeneralQuestionCtrl, Parent> generalQ,
                                                       Pair<ComparisonQuestionCtrl, Parent> comparisonQ,
                                                       Pair<EstimationQuestionCtrl, Parent> estimationQ,
                                                       Pair<MostExpensiveQuestionCtrl, Parent> mostExpensiveQ) {


        // TODO: this definitely needs restructuring, too much code duplication

        this.generalQuestionCtrl = generalQ.getKey();
        this.generalQuestion = new Scene(generalQ.getValue());


        this.comparisonQuestionCtrl = comparisonQ.getKey();
        this.comparisonQuestion = new Scene(comparisonQ.getValue());

        this.estimationQuestionCtrl = estimationQ.getKey();
        this.estimationQuestion = new Scene(estimationQ.getValue());

        this.mostExpensiveQuestionCtrl = mostExpensiveQ.getKey();
        this.mostExpensiveQuestion = new Scene(mostExpensiveQ.getValue());

    }

    /**
     * Initializes all the events that should happen upon sending a close request to
     * the primary stage, that is, clicking the red x button on the window
     */
    public void initializeOnCloseEvents() {

        primaryStage.setOnCloseRequest(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(getClass().getResource("/client/stylesheets/myDialog.css").toExternalForm());
            dialogPane.getStyleClass().add("myDialog");
            String pathToLightbulb = getClass().getResource("/client/img/main_lightbulb.png").toExternalForm();
            Stage stage = (Stage) dialogPane.getScene().getWindow();
            stage.getIcons().add(
                    new Image(pathToLightbulb));
            ImageView lightBulbIcon = new ImageView(pathToLightbulb);
            lightBulbIcon.setFitHeight(100);
            lightBulbIcon.setFitWidth(100);
            lightBulbIcon.setPreserveRatio(true);
            dialogPane.setGraphic(lightBulbIcon);
            alert.setHeaderText("Do you want to leave?");
            alert.setTitle("Leaving?");
            alert.showAndWait();

            if (alert.getResult() == ButtonType.OK) {
                sendLeaveMessageToServer();
                saveUsernamePrefillToFile(usernamePrefill);
                System.exit(0);
            } else {
                event.consume();
            }

        });
    }

    /**
     * Shows the waiting room screen
     */
    public void showWaitingRoom() {
        primaryStage.setTitle("Waiting room");
        waitingRoomCtrl.fadeInWait();
        primaryStage.setScene(waitingRoom);
    }

    /**
     * if the user is entering the waiting room from the end leaderboard the username scene doesnt have to be shown
     * and the joining is done here automatically with the pre-filled username
     */
    public void showWaitRoomFromTheEndScreen(){
        userCtrl.join();
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
        mainScreenCtrl.fadeInMain();
        primaryStage.setScene(mainScreen);
    }

    /**
     * Shows the help screen scene
     */
    public void showHelpScreen() {
        primaryStage.setTitle("Help");
        helpScreenCtrl.fadeInHelp();
        primaryStage.setScene(helpScene);
    }

    /**
     * Shows next question, the question type is selected randomly
     */
    public void nextQuestion(Question q) {
        if (q instanceof GeneralQuestion) {
            showGeneralQuestion(q);
        } else if (q instanceof ComparisonQuestion) {
            showComparisonQuestion(q);
        } else if (q instanceof EstimationQuestion) {
            showEstimationQuestion(q);
        } else if (q instanceof WhichIsMoreQuestion) {
            showMostExpensiveQuestion(q);
        }
    }

    /**
     * Shows the username input screen
     */
    public void showUsernameInputScreen() {

        primaryStage.setTitle("Username input");

        userCtrl.fadeInUser();
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
     *
     * @param gameUpdate the update for this game received from the WebSocket session
     */
    protected void gameUpdateHandler(GameUpdate gameUpdate) {

        System.out.print("Update received...\t");

        if (gameUpdate instanceof GameUpdatePlayerJoined) {
            System.out.print("Player joined: " + ((GameUpdatePlayerJoined) gameUpdate).getPlayer());
            waitingRoomCtrl.addPlayerToWaitingRoom(((GameUpdatePlayerJoined) gameUpdate).getPlayer());
        } else if (gameUpdate instanceof GameUpdatePlayerLeft) {
            System.out.print("Player left: " + ((GameUpdatePlayerLeft) gameUpdate).getPlayer());
            waitingRoomCtrl.removePlayerFromWaitingRoom(((GameUpdatePlayerLeft) gameUpdate).getPlayer());
        } else if (gameUpdate instanceof GameUpdateGameStarting) {
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
     *
     * @param gameUpdate the incoming game update
     */
    private void incomingQuestionHandler(GameUpdate gameUpdate) {

        if (gameUpdate instanceof GameUpdateGameFinished gameUpdateGameFinished) {

            // This game update can later contain metadata about the game like scores or anything
            // else the client would want to display after the game ends
            // TODO: for now this just goes to the main screen
            leaderboardCtrl.setLeaderboardCtrlState(LeaderboardCtrl.LeaderboardCtrlState.END_GAME_LEADERBOARD);
            leaderboardCtrl.initializeButtonsForMainScreen();
            Platform.runLater(() -> this.showLeaderboardWithPresetScores(gameUpdateGameFinished.getLeaderboard()));

        } else if (gameUpdate instanceof GameUpdateNextQuestion gameUpdateNextQuestion) {

            gameManager.setCurrentQuestionByIdx(gameUpdateNextQuestion.getQuestionIdx());
            Platform.runLater(() -> nextQuestion(gameManager.getCurrentQuestion()));

        } else if (gameUpdate instanceof GameUpdateTransitionPeriodEntered gameUpdateTransitionPeriodEntered) {

            // TODO: display transition screen, this gameupdate already contains an answer response entity w/ the necessary information for the screen

            System.out.println("transition period");

        } else if(gameUpdate instanceof GameUpdateDisplayLeaderboard gameUpdateDisplayLeaderboard) {

            leaderboardCtrl.setLeaderboardCtrlState(LeaderboardCtrl.LeaderboardCtrlState.MID_GAME_LEADERBOARD);
            leaderboardCtrl.disableButtonsForMainScreen();
            Platform.runLater(() -> this.showLeaderboardWithPresetScores(gameUpdateDisplayLeaderboard.getLeaderboard()));

        }

    }

    /**
     * Returns the game type that has been selected by the user by clicking on either the singleplayer
     * or multiplayer button
     *
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
     *
     * @param usernamePrefill the username prefill to be used
     */
    public void setUsernamePrefill(String usernamePrefill) {

        this.usernamePrefill = usernamePrefill;

    }

    /**
     * Returns the username last entered by the player to join a server.
     *
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
     *
     * @param serverAddressPrefill the server address prefill to be used
     */
    public void setServerAddressPrefill(String serverAddressPrefill) {

        this.serverAddressPrefill = serverAddressPrefill;

    }

    /**
     * Returns the server address last entered by the player to attempt to join a server or otherwise connect
     * to a server. Note that the server address prefill thus also changes even if the URL was invalid or if a
     * connection could not be established.
     *
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
        connectToServerCtrl.fadeInServer();
        primaryStage.setScene(connectToServer);

    }

    /**
     * This method is called by the connect to server screen. It shows the admin screen (table with all activities)
     * after having confirmed the server address.
     */
    public void showAdminServerConfirmed() {

        primaryStage.setTitle("Admin");
        adminCtrl.fadeInAdmin();
        primaryStage.setScene(adminScene);
        adminCtrl.refresh();
        adminCtrl.setScene(adminScene);

    }

    /**
     * Show the edit activity screen
     *
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
        connectToServerCtrl.fadeInServer();
        leaderboardCtrl.setLeaderboardCtrlState(LeaderboardCtrl.LeaderboardCtrlState.MAIN_LEADERBOARD);
        primaryStage.setScene(connectToServer);

    }

    /**
     * if the leaderboard is opened from main screen the text in the bubble should be changed
      */
    public void changeLeaderboardText(){
        leaderboardCtrl.disableButtonsForMainScreen();
        leaderboardCtrl.changeTextMainScreen();
    }

    /**
     * This method is called by the connect to server screen. It shows the leaderboard screen
     * after having confirmed the server address.
     */
    public void showLeaderboardServerConfirmed() {

        primaryStage.setTitle("Leaderboard");
        primaryStage.setScene(leaderboard);
        leaderboardCtrl.fadeInLeaderboard();
        leaderboardCtrl.populateLeaderboard();

    }

    /**
     * Displays the leaderboard with the given list of scores
     * @param scoreList the list of scores to display
     */
    public void showLeaderboardWithPresetScores(List<Score> scoreList) {

        primaryStage.setTitle("Leaderboard");
        primaryStage.setScene(leaderboard);
        leaderboardCtrl.fadeInLeaderboard();
        leaderboardCtrl.populateLeaderboard(scoreList);

    }

    /**
     * Shows a confirmation alert, if okay button is pressed
     * function sends leaving message to server and shows main screen
     */
    public void exitWhileInTheGame() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/client/stylesheets/myDialog.css").toExternalForm());
        dialogPane.getStyleClass().add("myDialog");
        String pathToLightbulb = getClass().getResource("/client/img/main_lightbulb.png").toExternalForm();
        Stage stage = (Stage) dialogPane.getScene().getWindow();
        stage.getIcons().add(
                new Image(pathToLightbulb));
        ImageView lightBulbIcon = new ImageView(pathToLightbulb);
        lightBulbIcon.setFitHeight(100);
        lightBulbIcon.setFitWidth(100);
        dialogPane.setGraphic(lightBulbIcon);
        alert.setHeaderText("Do you want to leave?");
        alert.setTitle("Leaving?");
        alert.showAndWait();

        if (alert.getResult() == ButtonType.OK) {
            sendLeaveMessageToServer();
            connectToServerCtrl.goBackButton();
        }
    }

    /**
     * Loads the last used username by the player from a file that was created when
     * the application was last closed
     *
     * @return the username form the file
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

        while (fileReader.hasNextLine()) {
            String line = fileReader.nextLine();
            if (line.startsWith("username: ")) {
                try {
                    String toReturn = line.split(": ")[1];
                    if(toReturn.length() > MAXIMUM_USERNAME_SIZE) {
                        toReturn = toReturn.substring(0, MAXIMUM_USERNAME_SIZE);
                    }
                    return toReturn;
                } catch (IndexOutOfBoundsException e) {
                    return "";
                }
            }
        }

        return "";
    }

    /**
     * Saves the currently stored username prefill to a file, to be loaded again
     * when the application next starts up
     *
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

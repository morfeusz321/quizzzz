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
import client.utils.ModalFactory;
import client.utils.ScoreUtils;
import client.utils.ServerUtils;

import com.google.inject.Inject;

import commons.*;

import commons.gameupdate.*;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class MainCtrl {

    protected static final int MAXIMUM_USERNAME_SIZE = 15;

    private final ServerUtils server;
    private GameManager gameManager;
    private Stage primaryStage;
    private final CommonUtils utils;
    private ScoreUtils scoreHelper;
    private final ModalFactory modalFactory;

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

    private boolean usedDouble = false;
    private boolean usedTime = false;
    private boolean usedRemove = false;

    /**
     * Creates a MainCtrl, which controls displaying and switching between screens.
     *
     * @param server       Utilities for communicating with the server (API endpoint)
     * @param utils        common utils to use
     * @param modalFactory the modal factory to use
     */
    @Inject
    public MainCtrl(ServerUtils server, CommonUtils utils, ModalFactory modalFactory) {
        this.server = server;
        this.utils = utils;
        this.modalFactory = modalFactory;
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

        this.scoreHelper = new ScoreUtils();

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
            Alert alert = modalFactory.getModal(Alert.AlertType.CONFIRMATION, "Leaving?", "Do you want to leave?");
            alert.showAndWait();

            if(alert.getResult() == ButtonType.OK) {
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
    public void showWaitRoomFromTheEndScreen() {
        userCtrl.join();
    }

    /**
     * Shows the general question screen
     */
    public void showGeneralQuestion(Question q) {
        primaryStage.setTitle("General question");
        primaryStage.setScene(generalQuestion);
        generalQuestionCtrl.loadQuestion(q);
    }

    /**
     * Shows the comparison question screen
     */
    public void showComparisonQuestion(Question q) {
        primaryStage.setTitle("Comparison question");
        primaryStage.setScene(comparisonQuestion);
        comparisonQuestionCtrl.loadQuestion(q);
    }

    /**
     * Shows the "most expensive" question screen
     */
    public void showMostExpensiveQuestion(Question q) {
        primaryStage.setTitle("Most expensive question");
        primaryStage.setScene(mostExpensiveQuestion);
        mostExpensiveQuestionCtrl.loadQuestion(q);
    }

    /**
     * Shows the estimation question screen
     */
    public void showEstimationQuestion(Question q) {
        primaryStage.setTitle("Estimation question");
        primaryStage.setScene(estimationQuestion);
        estimationQuestionCtrl.loadQuestion(q);
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

        if(gameUpdate instanceof GameUpdatePlayerJoined) {
            waitingRoomCtrl.addPlayerToWaitingRoom(((GameUpdatePlayerJoined) gameUpdate).getPlayer());
        } else if(gameUpdate instanceof GameUpdatePlayerLeft) {
            if(gameManager != null) {
                gameManager.setPlayerCount(gameManager.getPlayerCount() - 1);
                Platform.runLater(this::handleUpdatePlayerCount);
            }
            waitingRoomCtrl.removePlayerFromWaitingRoom(((GameUpdatePlayerLeft) gameUpdate).getPlayer());
        } else if(gameUpdate instanceof GameUpdateNoQuestions) {
            sendLeaveMessageToServer();
            Platform.runLater(
                    () -> {
                        Alert alert = modalFactory.getModal(Alert.AlertType.INFORMATION, "Error", "",
                                "Oh no, something went wrong and no questions could be generated! \n" +
                                        "Please try to create a new game. You will be redirected to the main menu after" +
                                        "closing this pop up.");
                        alert.showAndWait();

                        if(alert.getResult() == ButtonType.OK) {
                            connectToServerCtrl.goBackButton();
                        }
                    }
            );
        } else if(gameUpdate instanceof GameUpdateGameStarting) {
            resetJokers();
            server.setInGameTrue();
            gameManager = new GameManager(); // "reset" game manager, because a new game is started
            gameManager.setQuestions(server.getQuestions());
            gameManager.setCurrentQuestionByIdx(0); // set the first question
            this.scoreHelper.setPlayer(server.getPlayerByUsername(usernamePrefill));
            gameManager.setPlayerCount(server.getPlayers().size());
            handleUpdatePlayerCount();
            server.registerForGameLoop(this::incomingQuestionHandler, getSavedUsernamePrefill());
        } else if(gameUpdate instanceof GameEmojiUpdate) {
            if(!((GameEmojiUpdate) gameUpdate).getUsername().equals(userCtrl.getSavedCurrentUsername())) {
                try {
                    ImageView emoji = (ImageView) primaryStage.getScene().lookup('#' + ((GameEmojiUpdate) gameUpdate).getEmoji());
                    String username = ((GameEmojiUpdate) gameUpdate).getUsername();
                    Platform.runLater(() -> {
                        emojiAnimation(emoji, username);
                    });
                } catch(ClassCastException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    /**
     * Sends the message to server utils that emoji was pressed
     *
     * @param sentEmoji id of sent emoji
     */
    public void sendEmoji(String sentEmoji) {
        server.sendEmoji(new GameEmojiUpdate(sentEmoji, userCtrl.getSavedCurrentUsername()));
    }

    /**
     * Displays emoji animation
     *
     * @param clickedEmoji clicked emoji instance
     * @param username     name of the user who sent the emoji
     */
    public void emojiAnimation(ImageView clickedEmoji, String username) {

        ImageView emoji = new ImageView(clickedEmoji.getImage());
        AnchorPane anchorPane = (AnchorPane) primaryStage.getScene().lookup("#anchorPane");
        ImageView hoverEmoji = (ImageView) primaryStage.getScene().lookup("#hoverEmoji");
        anchorPane.getChildren().add(emoji);
        emojiNameAnimation(username, anchorPane, hoverEmoji);
        emoji.toBack();

        double sizeRatio = 0.78; // should be <= 1
        emoji.setFitWidth(hoverEmoji.getFitWidth() * sizeRatio);
        emoji.setPreserveRatio(true);
        emoji.setLayoutX(hoverEmoji.getLayoutX() + 20);
        emoji.setLayoutY(hoverEmoji.getLayoutY());

        Random r = new Random();
        CubicCurve cubic = new CubicCurve();
        cubic.setStartX(emoji.getFitWidth() / 2);
        cubic.setStartY(0);
        cubic.setControlX1(emoji.getFitWidth() / 2 + utils.randomIntInRange(-40, -20, r));
        cubic.setControlY1(utils.randomIntInRange(-125, -50, r));
        cubic.setControlX2(emoji.getFitWidth() / 2 + utils.randomIntInRange(20, 40, r));
        cubic.setControlY2(utils.randomIntInRange(-275, -175, r));
        cubic.setEndX(emoji.getFitWidth() / 2);
        cubic.setEndY(-300);

        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.millis(600));
        pathTransition.setPath(cubic);
        pathTransition.setNode(emoji);
        pathTransition.setCycleCount(1);
        pathTransition.setAutoReverse(false);

        emoji.opacityProperty().setValue(0.5);
        Timeline fade = new Timeline(
                new KeyFrame(Duration.millis(600), new KeyValue(emoji.opacityProperty(), 1)),
                new KeyFrame(Duration.millis(1050), new KeyValue(emoji.opacityProperty(), 1)),
                new KeyFrame(Duration.millis(1350), new KeyValue(emoji.opacityProperty(), 0))
        );
        fade.setAutoReverse(false);
        fade.setCycleCount(1);
        fade.setOnFinished(e -> {
            emoji.setVisible(false);
            anchorPane.getChildren().remove(emoji);
        });

        fade.play();
        pathTransition.play();
    }

    /**
     * Displays username of the person who sent emoji underneath the emoji
     *
     * @param username   username of person who sent the emoji
     * @param anchorPane anchorPane of the current scene
     * @param hoverEmoji hover Emoji of the current screen
     */
    private void emojiNameAnimation(String username, AnchorPane anchorPane, ImageView hoverEmoji) {

        Label label = new Label(username);
        anchorPane.getChildren().add(label);
        label.toBack();

        double sizeRatio = 0.78; // should be <= 1

        label.setLayoutX(hoverEmoji.getLayoutX() + 65);
        label.setLayoutY(hoverEmoji.getLayoutY() + 70);

        Random r = new Random();
        CubicCurve cubic = new CubicCurve();
        cubic.setStartX(label.getPrefWidth() / 2);
        cubic.setStartY(0);
        cubic.setControlX1(label.getPrefWidth() / 2 + utils.randomIntInRange(-40, -20, r));
        cubic.setControlY1(utils.randomIntInRange(-125, -50, r));
        cubic.setControlX2(label.getPrefWidth() / 2 + utils.randomIntInRange(20, 40, r));
        cubic.setControlY2(utils.randomIntInRange(-275, -175, r));
        cubic.setEndX(label.getPrefWidth() / 2);
        cubic.setEndY(-300);

        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.millis(600));
        pathTransition.setPath(cubic);
        pathTransition.setNode(label);
        pathTransition.setCycleCount(1);
        pathTransition.setAutoReverse(false);

        label.opacityProperty().setValue(0.5);
        Timeline fade = new Timeline(
                new KeyFrame(Duration.millis(600), new KeyValue(label.opacityProperty(), 1)),
                new KeyFrame(Duration.millis(1050), new KeyValue(label.opacityProperty(), 1)),
                new KeyFrame(Duration.millis(1350), new KeyValue(label.opacityProperty(), 0))
        );
        fade.setAutoReverse(false);
        fade.setCycleCount(1);
        fade.setOnFinished(e -> {
            label.setVisible(false);
            anchorPane.getChildren().remove(label);
        });

        fade.play();
        pathTransition.play();
    }

    /**
     * Handles updates incoming from the game long poll loop, displaying the right question or other screens
     * when it is necessary
     *
     * @param gameUpdate the incoming game update
     */
    private void incomingQuestionHandler(GameUpdate gameUpdate) {

        if(gameUpdate instanceof GameUpdateGameFinished gameUpdateGameFinished) {

            // This game update can later contain metadata about the game like scores or anything
            // else the client would want to display after the game ends
            leaderboardCtrl.setLeaderboardCtrlState(LeaderboardCtrl.LeaderboardCtrlState.END_GAME_LEADERBOARD);
            leaderboardCtrl.initializeButtonsForMainScreen();
            Platform.runLater(() -> this.showLeaderboardWithPresetScores(gameUpdateGameFinished.getLeaderboard()));

        } else if(gameUpdate instanceof GameUpdateNextQuestion gameUpdateNextQuestion) {

            gameManager.setCurrentQuestionByIdx(gameUpdateNextQuestion.getQuestionIdx());
            Platform.runLater(() -> nextQuestion(gameManager.getCurrentQuestion()));
            handleUpdatePlayerCount();

        } else if(gameUpdate instanceof GameUpdateTransitionPeriodEntered gameUpdateTransitionPeriodEntered) {
            scoreHelper.setScore(gameUpdateTransitionPeriodEntered.getAnswerResponseEntity());

            UUID id = gameManager.getCurrentQuestion().questionId;
            if(gameManager.getCurrentQuestion() instanceof GeneralQuestion) {
                generalQuestionCtrl.enterTransitionScreen(gameUpdateTransitionPeriodEntered);
            } else if(gameManager.getCurrentQuestion() instanceof WhichIsMoreQuestion) {
                mostExpensiveQuestionCtrl.enterTransitionScreen(gameUpdateTransitionPeriodEntered);
            } else if(gameManager.getCurrentQuestion() instanceof ComparisonQuestion) {
                comparisonQuestionCtrl.enterTransitionScreen(gameUpdateTransitionPeriodEntered);
            } else if(gameManager.getCurrentQuestion() instanceof EstimationQuestion) {
                estimationQuestionCtrl.enterTransitionScreen(gameUpdateTransitionPeriodEntered);
            }

        } else if(gameUpdate instanceof GameUpdateDisplayLeaderboard gameUpdateDisplayLeaderboard) {

            leaderboardCtrl.setLeaderboardCtrlState(LeaderboardCtrl.LeaderboardCtrlState.MID_GAME_LEADERBOARD);
            leaderboardCtrl.disableButtonsForMainScreen();
            Platform.runLater(() -> this.showLeaderboardWithPresetScores(gameUpdateDisplayLeaderboard.getLeaderboard()));

        } else if(gameUpdate instanceof GameUpdateTimerJoker update) {
            for(Map.Entry<String, Long> player : update.getTime().entrySet()) {
                if(this.userCtrl.getSavedCurrentUsername().equals(player.getKey())) {
                    handleTimerJoker(player.getValue());
                }
            }
        } else if(gameUpdate instanceof GameUpdateQuestionJoker update) {
            int buttonNumber = update.getButtonNumber();
            handleQuestionJoker(buttonNumber);
        }
    }

    /**
     * Method for handling the time joker for each question type.
     */
    public void handleTimerJoker(long time) {
        if(gameManager.getCurrentQuestion() instanceof GeneralQuestion) {
            generalQuestionCtrl.handleTimeJoker(time);
        } else if(gameManager.getCurrentQuestion() instanceof ComparisonQuestion) {
            comparisonQuestionCtrl.handleTimeJoker(time);
        } else if(gameManager.getCurrentQuestion() instanceof EstimationQuestion) {
            estimationQuestionCtrl.handleTimeJoker(time);
        } else if(gameManager.getCurrentQuestion() instanceof WhichIsMoreQuestion) {
            mostExpensiveQuestionCtrl.handleTimeJoker(time);
        }
    }

    /**
     * Method for handling the question joker for each question type.
     */
    public void handleQuestionJoker(int buttonNumber) {
        if(gameManager.getCurrentQuestion() instanceof GeneralQuestion) {
            generalQuestionCtrl.removeQuestion(buttonNumber);
        } else if(gameManager.getCurrentQuestion() instanceof ComparisonQuestion) {
            comparisonQuestionCtrl.removeQuestion(buttonNumber);
        } else if(gameManager.getCurrentQuestion() instanceof WhichIsMoreQuestion) {
            mostExpensiveQuestionCtrl.removeQuestion(buttonNumber);
        }
    }

    /**
     * Updates the player count label from the question screens when a player leaves the game
     */
    public void handleUpdatePlayerCount() {
        if(gameManager.getCurrentQuestion() instanceof GeneralQuestion) {
            generalQuestionCtrl.updatePlayerCount();
        } else if(gameManager.getCurrentQuestion() instanceof ComparisonQuestion) {
            comparisonQuestionCtrl.updatePlayerCount();
        } else if(gameManager.getCurrentQuestion() instanceof WhichIsMoreQuestion) {
            mostExpensiveQuestionCtrl.updatePlayerCount();
        } else if(gameManager.getCurrentQuestion() instanceof EstimationQuestion) {
            estimationQuestionCtrl.updatePlayerCount();
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
     * Returns the game manager (to be used for current question number)
     *
     * @return the game manager
     */
    public GameManager getGameManager() {
        return gameManager;
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
    public void changeLeaderboardText() {
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
     *
     * @param scoreList the list of scores to display
     */
    public void showLeaderboardWithPresetScores(List<Score> scoreList) {

        primaryStage.setTitle("Leaderboard");
        primaryStage.setScene(leaderboard);
        leaderboardCtrl.fadeInLeaderboard();
        leaderboardCtrl.setDisplayScore(scoreHelper.getPoints());
        leaderboardCtrl.populateLeaderboard(scoreList);

    }

    /**
     * Shows a confirmation alert, if okay button is pressed
     * function sends leaving message to server and shows main screen
     */
    public void exitWhileInTheGame() {

        Alert alert = modalFactory.getModal(Alert.AlertType.CONFIRMATION, "Leaving?", "Do you want to leave?");
        alert.showAndWait();

        if(alert.getResult() == ButtonType.OK) {
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
        } catch(NullPointerException | URISyntaxException | FileNotFoundException e) {
            return "";
        }

        while(fileReader.hasNextLine()) {
            String line = fileReader.nextLine();
            if(line.startsWith("username: ")) {
                try {
                    String toReturn = line.split(": ")[1];
                    if(toReturn.length() > MAXIMUM_USERNAME_SIZE) {
                        toReturn = toReturn.substring(0, MAXIMUM_USERNAME_SIZE);
                    }
                    return toReturn;
                } catch(IndexOutOfBoundsException e) {
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

        } catch(IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * retrieves the score from player
     *
     * @return player's score
     */
    public int getScore() {
        return this.scoreHelper.getPoints();
    }

    /**
     * Disables a joker button for the current game
     *
     * @param number the number of the button (1 - remove one wrong answer joker, 2 - double points joker, 3 - time joker)
     */
    public void disableJoker(int number) {
        switch(number) {
            case 1 -> usedRemove = true;
            case 2 -> usedDouble = true;
            case 3 -> usedTime = true;
        }
    }

    /**
     * Returns whether the selected joker button was used or not
     *
     * @param number the number of the button (1 - remove one wrong answer joker, 2 - double points joker, 3 - time joker)
     * @return true if already used, false otherwise
     */
    public boolean getJokerStatus(int number) {
        switch(number) {
            case 1 -> {
                return usedRemove;
            }
            case 2 -> {
                return usedDouble;
            }
            case 3 -> {
                return usedTime;
            }
        }
        return false;
    }

    /**
     * Resets the joker buttons (called every time when a new game is started)
     */
    public void resetJokers() {
        this.usedRemove = false;
        this.usedDouble = false;
        this.usedTime = false;
    }

    /**
     * Method for getting the current game UUID
     *
     * @return UUID of the current game
     */
    public UUID getGameUUID() {
        return userCtrl.getSavedGameUUID();
    }

}

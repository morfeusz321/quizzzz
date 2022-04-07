package client.scenes;

import client.utils.AnimationUtils;
import client.utils.ServerUtils;
import javafx.scene.effect.ColorAdjust;
import commons.GameType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Button;

import javax.inject.Inject;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainScreenCtrl implements Initializable {

    protected GameType selectedGameType;

    @FXML
    private ImageView help;

    @FXML
    private Button singlePlayer;

    @FXML
    private Button multiPlayer;

    @FXML
    private Button admin;

    @FXML
    private ImageView leaderboard;
    @FXML
    private ImageView lightning;
    @FXML
    private ImageView lightbulb;

    @FXML
    private AnchorPane anchorPane;

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private boolean lightOn;
    public final AnimationUtils animation;
    private List<Button> buttonList;
    private List<ImageView> imageList;

    /**
     * Constructor for main screen controller, which controls the interaction of the (main) overview screen
     *
     * @param server   Utilities for communicating with the server (API endpoint)
     * @param mainCtrl The main control which is used for calling methods to switch scenes
     */
    @Inject
    public MainScreenCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.animation = new AnimationUtils();
    }

    /**
     * Initializes the images on the main screen
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        leaderboard.setImage(new Image("/client/img/main_leaderboard.png"));
        lightning.setImage(new Image("/client/img/main_lightning.png"));
        lightbulb.setImage(new Image("/client/img/main_lightbulb.png"));
        help.setImage(new Image("/client/img/question_mark.png"));
        buttonList = List.of(singlePlayer, multiPlayer, admin);
        imageList = List.of(leaderboard, help);
        setLightBulbEventHandlers();
        initializeEventHandlers();
        leaderboardHandler();
        helpHandler();
    }

    /**
     * Sets the event handlers of the light bulb. This is so that there is an "easteregg" for the players
     * that they can turn the lightbulb on/off.
     */
    private void setLightBulbEventHandlers() {
        lightOn = true;

        lightbulb.setOnMouseClicked(e -> {
            if(lightOn) {
                // lightbulb is currently on
                lightbulb.setImage(new Image("/client/img/main_lightbulb_off.png"));
                lightOn = false;
                anchorPane.setStyle("-fx-background-color: #c2baae;");
            } else {
                // lightbulb is currently off
                lightbulb.setImage(new Image("/client/img/main_lightbulb.png"));
                lightOn = true;
                anchorPane.setStyle("-fx-background-color: #F0EAD6;");
            }
        });
    }

    /**
     * Starts the single player mode of the game
     *
     * @param event click on singleplayer button
     */
    @FXML
    private void singlePlayer(ActionEvent event) throws InterruptedException {
        this.selectedGameType = GameType.SINGLEPLAYER;
        fadeOutMain("user");

    }

    /**
     * Starts the multi player mode of the game
     *
     * @param event click on multiplayer button
     */
    @FXML
    private void multiPlayer(ActionEvent event) {
        this.selectedGameType = GameType.MULTIPLAYER;
        fadeOutMain("user");

    }

    /**
     * Opens the "please connect to server" screen, and subsequently the admin screen
     */
    public void showAdmin() {
        fadeOutMain("admin");
    }

    /**
     * Opens the "please connect to server" screen, and subsequently the leaderboard screen
     */
    public void showLeaderboard() {
        mainCtrl.changeLeaderboardText();
        mainCtrl.showLeaderboard();
    }

    /**
     * Opens the help screen
     */
    public void showHelp() {
        fadeOutMain("help");
    }

    /**
     * goes to the given scene and does fading animation
     *
     * @param nextScene
     */
    public void fadeOutMain(String nextScene) {
        animation.fadeOut(anchorPane, mainCtrl, nextScene);
    }

    /**
     * when entering the scene it does fading animation
     */
    public void fadeInMain() {
        animation.fadeIn(anchorPane);
    }

    /**
     * Gives a button its event handlers
     *
     * @param btn the button to give event handlers to
     */
    private void addEventHandlersToButton(Button btn) {

        btn.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> eventHandlerButtonMouseEntered(btn));
        btn.addEventHandler(MouseEvent.MOUSE_EXITED, e -> eventHandlerButtonMouseExited(btn));

    }

    /**
     * The event handler called when the user hovers over a button
     *
     * @param btn the button that was hovered over
     */
    private void eventHandlerButtonMouseEntered(Button btn) {

        btn.getStyleClass().add("hover-buttonDark");
        btn.getStyleClass().add("hover-cursor");
    }

    /**
     * The event handler called when the user stops hovering over a button
     *
     * @param btn the button that was stopped hovering over
     */
    private void eventHandlerButtonMouseExited(Button btn) {

        btn.getStyleClass().remove("hover-buttonDark");
        btn.getStyleClass().remove("hover-cursor");

    }

    /**
     * Initializes the event handlers of all buttons
     */
    private void initializeEventHandlers() {

        buttonList.forEach(this::addEventHandlersToButton);

    }

    /**
     * The leaderboard button functionality
     */
    private void leaderboardHandler() {

        ColorAdjust hover = new ColorAdjust();
        hover.setBrightness(-0.05);
        hover.setSaturation(0.1);
        hover.setHue(-0.02);


        leaderboard.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> showLeaderboard());
        leaderboard.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            leaderboard.getStyleClass().add("hover-cursor");
            leaderboard.setEffect(hover);
        });
        leaderboard.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            leaderboard.getStyleClass().remove("hover-cursor");
            leaderboard.setEffect(null);
        });
    }

    /**
     * The help button functionality
     */
    private void helpHandler() {

        ColorAdjust hover = new ColorAdjust();
        hover.setBrightness(-0.1);
        hover.setSaturation(0.15);
        hover.setHue(-0.03);

        help.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> showHelp());
        help.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            help.getStyleClass().add("hover-cursor");
            help.setEffect(hover);
        });
        help.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            help.getStyleClass().remove("hover-cursor");
            help.setEffect(null);
        });
    }

}
